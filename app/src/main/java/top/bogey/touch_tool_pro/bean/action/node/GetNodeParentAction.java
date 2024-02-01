package top.bogey.touch_tool_pro.bean.action.node;

import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinNode;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class GetNodeParentAction extends Action {
    private transient Pin parentNode = new Pin(new PinNode(), R.string.pin_node, true);
    private transient Pin nodePin = new Pin(new PinNode(), R.string.pin_node);


    public GetNodeParentAction() {
        super(ActionType.NODE_PARENT);
        parentNode = addPin(parentNode);
        nodePin = addPin(nodePin);
    }

    public GetNodeParentAction(JsonObject jsonObject) {
        super(jsonObject);
        parentNode = reAddPin(parentNode);
        nodePin = reAddPin(nodePin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinNode node = (PinNode) getPinValue(runnable, context, nodePin);
        AccessibilityNodeInfo nodeInfo = node.getNode();
        if (nodeInfo != null) {
            AccessibilityNodeInfo parent = nodeInfo.getParent();
            parentNode.getValue(PinNode.class).setNode(parent);
        }
    }
}
