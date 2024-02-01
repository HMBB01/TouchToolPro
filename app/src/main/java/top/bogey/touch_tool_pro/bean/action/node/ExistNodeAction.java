package top.bogey.touch_tool_pro.bean.action.node;

import android.graphics.Rect;
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
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinNode;
import top.bogey.touch_tool_pro.bean.pin.pins.PinNodePath;
import top.bogey.touch_tool_pro.bean.pin.pins.PinPoint;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;

public class ExistNodeAction extends CheckAction {
    private transient Pin pathPin = new Pin(new PinNodePath(), R.string.pin_node_path);
    private transient Pin idPin = new Pin(new PinString(PinSubType.NODE_ID), R.string.pin_string_node_id);
    private transient Pin posPin = new Pin(new PinPoint(), R.string.pin_point, true);
    private transient Pin nodePin = new Pin(new PinNode(), R.string.pin_node, true);


    public ExistNodeAction() {
        super(ActionType.CHECK_EXIST_NODE);
        pathPin = addPin(pathPin);
        idPin = addPin(idPin);
        posPin = addPin(posPin);
        nodePin = addPin(nodePin);
    }

    public ExistNodeAction(JsonObject jsonObject) {
        super(jsonObject);
        pathPin = reAddPin(pathPin);
        idPin = reAddPin(idPin);
        posPin = reAddPin(posPin);
        nodePin = reAddPin(nodePin);
    }

    @Override
    public void resetReturnValue(Pin pin) {
        if (pin.equals(resultPin)) super.resetReturnValue(pin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        if (!pin.equals(resultPin)) return;

        PinBoolean result = resultPin.getValue(PinBoolean.class);

        MainAccessibilityService service = MainApplication.getInstance().getService();
        ArrayList<AccessibilityNodeInfo> roots = service.getNeedWindowsRoot();
        PinNodePath nodePath = (PinNodePath) getPinValue(runnable, context, pathPin);
        if (nodePath.getValue() == null || nodePath.getValue().isEmpty()) {
            PinString id = (PinString) getPinValue(runnable, context, idPin);
            for (AccessibilityNodeInfo root : roots) {
                List<AccessibilityNodeInfo> children = root.findAccessibilityNodeInfosByViewId(id.getValue());
                if (!children.isEmpty()) {
                    AccessibilityNodeInfo child = children.get(0);
                    Rect rect = new Rect();
                    child.getBoundsInScreen(rect);
                    posPin.getValue(PinPoint.class).setPoint(service, rect.centerX(), rect.centerY());
                    nodePin.getValue(PinNode.class).setNode(child);
                    result.setBool(true);
                    return;
                }
            }
        } else {
            AccessibilityNodeInfo node = nodePath.getNode(roots);
            if (node != null) {
                Rect rect = new Rect();
                node.getBoundsInScreen(rect);
                posPin.getValue(PinPoint.class).setPoint(service, rect.centerX(), rect.centerY());
                nodePin.getValue(PinNode.class).setNode(node);
                result.setBool(true);
            }
        }
    }

    @Override
    public ActionCheckResult check(FunctionContext context) {
        if (resultPin.getLinks().isEmpty()) {
            if (!posPin.getLinks().isEmpty() || !nodePin.getLinks().isEmpty()) {
                return new ActionCheckResult(ActionCheckResult.ActionResultType.ERROR, R.string.error_result_pin_no_use);
            }
        }
        return super.check(context);
    }

    public Pin getPathPin() {
        return pathPin;
    }

    public Pin getNodePin() {
        return nodePin;
    }
}
