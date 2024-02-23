package top.bogey.touch_tool_pro.bean.action.node;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinArea;
import top.bogey.touch_tool_pro.bean.pin.pins.PinNode;
import top.bogey.touch_tool_pro.bean.pin.pins.PinNodePath;
import top.bogey.touch_tool_pro.bean.pin.pins.PinPoint;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class GetNodeInfoStateAction extends Action {
    private transient Pin textPin = new Pin(new PinString(), R.string.pin_string, true);
    private transient Pin idPin = new Pin(new PinString(), R.string.pin_string_node_id, true, false, true);
    private transient Pin pathPin = new Pin(new PinNodePath(), R.string.pin_node_path, true, false, true);
    private transient Pin areaPin = new Pin(new PinArea(), R.string.pin_area, true);
    private transient Pin posPin = new Pin(new PinPoint(), R.string.pin_point, true);
    private transient Pin typePin = new Pin(new PinString(), R.string.action_get_node_info_subtitle_type, true, false, true);
    private transient Pin nodePin = new Pin(new PinNode(), R.string.pin_node);

    public GetNodeInfoStateAction() {
        super(ActionType.NODE_INFO_STATE);
        textPin = addPin(textPin);
        idPin = addPin(idPin);
        pathPin = addPin(pathPin);
        areaPin = addPin(areaPin);
        posPin = addPin(posPin);
        typePin = addPin(typePin);
        nodePin = addPin(nodePin);
    }

    public GetNodeInfoStateAction(JsonObject jsonObject) {
        super(jsonObject);
        textPin = reAddPin(textPin);
        idPin = reAddPin(idPin);
        pathPin = reAddPin(pathPin);
        areaPin = reAddPin(areaPin);
        posPin = reAddPin(posPin);
        typePin = reAddPin(typePin);
        nodePin = reAddPin(nodePin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinNode node = (PinNode) getPinValue(runnable, context, nodePin);
        AccessibilityNodeInfo nodeInfo = node.getNode();
        if (nodeInfo == null) return;
        textPin.getValue(PinString.class).setValue(node.toString());
        idPin.getValue(PinString.class).setValue(nodeInfo.getViewIdResourceName());
        pathPin.getValue(PinNodePath.class).setValue(nodeInfo);
        Rect rect = new Rect();
        nodeInfo.getBoundsInScreen(rect);
        areaPin.getValue(PinArea.class).setArea(MainApplication.getInstance(), rect);
        posPin.getValue(PinPoint.class).setPoint(MainApplication.getInstance(), rect.centerX(), rect.centerY());
        CharSequence className = nodeInfo.getClassName();
        if (className == null) {
            typePin.getValue(PinString.class).setValue("");
        } else {
            typePin.getValue(PinString.class).setValue(className.toString());
        }
    }
}
