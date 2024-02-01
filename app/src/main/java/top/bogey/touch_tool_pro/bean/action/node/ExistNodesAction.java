package top.bogey.touch_tool_pro.bean.action.node;

import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionCheckResult;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.other.CheckAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinSubType;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinNode;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArray;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;

public class ExistNodesAction extends CheckAction {
    private transient Pin idPin = new Pin(new PinString(PinSubType.NODE_ID), R.string.pin_string_node_id);
    private transient Pin allNodePin = new Pin(new PinValueArray(PinType.NODE, false), R.string.pin_value_array, true);


    public ExistNodesAction() {
        super(ActionType.CHECK_EXIST_NODES);
        idPin = addPin(idPin);
        allNodePin = addPin(allNodePin);
    }

    public ExistNodesAction(JsonObject jsonObject) {
        super(jsonObject);
        idPin = reAddPin(idPin);
        allNodePin = reAddPin(allNodePin);
    }

    @Override
    public void resetReturnValue(Pin pin) {
        if (pin.equals(resultPin)) super.resetReturnValue(pin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        if (!pin.equals(resultPin)) return;

        PinBoolean result = resultPin.getValue(PinBoolean.class);
        ArrayList<PinValue> values = allNodePin.getValue(PinValueArray.class).getValues();

        MainAccessibilityService service = MainApplication.getInstance().getService();
        ArrayList<AccessibilityNodeInfo> roots = service.getNeedWindowsRoot();

        PinString id = (PinString) getPinValue(runnable, context, idPin);
        for (AccessibilityNodeInfo root : roots) {
            List<AccessibilityNodeInfo> children = root.findAccessibilityNodeInfosByViewId(id.getValue());
            if (!children.isEmpty()) {
                result.setBool(true);
                for (AccessibilityNodeInfo nodeInfo : children) {
                    values.add(new PinNode(nodeInfo));
                }
                return;
            }
        }
    }

    @Override
    public ActionCheckResult check(FunctionContext context) {
        if (resultPin.getLinks().isEmpty()) {
            if (!allNodePin.getLinks().isEmpty()) {
                return new ActionCheckResult(ActionCheckResult.ActionResultType.ERROR, R.string.error_result_pin_no_use);
            }
        }
        return super.check(context);
    }
}
