package top.bogey.touch_tool_pro.bean.action.node;

import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.JsonObject;

import java.util.ArrayList;

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

public class GetNodeChildrenAction extends Action {
    private transient Pin childrenPin = new Pin(new PinValueArray(PinType.NODE, false), R.string.pin_value_array, true);
    private transient Pin nodePin = new Pin(new PinNode(), R.string.pin_node);


    public GetNodeChildrenAction() {
        super(ActionType.NODE_CHILDREN);
        childrenPin = addPin(childrenPin);
        nodePin = addPin(nodePin);
    }

    public GetNodeChildrenAction(JsonObject jsonObject) {
        super(jsonObject);
        childrenPin = reAddPin(childrenPin);
        nodePin = reAddPin(nodePin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        ArrayList<PinValue> values = childrenPin.getValue(PinValueArray.class).getValues();
        PinNode node = (PinNode) getPinValue(runnable, context, nodePin);
        AccessibilityNodeInfo nodeInfo = node.getNode();
        if (nodeInfo != null) {
            for (int i = 0; i < nodeInfo.getChildCount(); i++) {
                AccessibilityNodeInfo child = nodeInfo.getChild(i);
                if (child != null) {
                    values.add(new PinNode(child));
                }
            }
        }
    }
}
