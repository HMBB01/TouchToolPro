package top.bogey.touch_tool_pro.bean.action.node;

import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinNode;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArray;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;

public class GetNodesInWindowAction extends Action {
    private transient Pin nodesPin = new Pin(new PinValueArray(PinType.NODE, false), R.string.pin_value_array, true);


    public GetNodesInWindowAction() {
        super(ActionType.NODES_IN_WINDOW);
        nodesPin = addPin(nodesPin);
    }

    public GetNodesInWindowAction(JsonObject jsonObject) {
        super(jsonObject);
        nodesPin = reAddPin(nodesPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        ArrayList<PinValue> values = nodesPin.getValue(PinValueArray.class).getValues();

        MainAccessibilityService service = MainApplication.getInstance().getService();
        for (AccessibilityNodeInfo info : service.getNeedWindowsRoot()) {
            values.add(new PinNode(info));
        }
    }
}
