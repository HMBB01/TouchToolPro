package top.bogey.touch_tool_pro.bean.action.node;

import android.graphics.Rect;
import android.view.ViewGroup;
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
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinNode;
import top.bogey.touch_tool_pro.bean.pin.pins.PinPoint;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArray;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;

public class GetNodesInPosAction extends Action {
    private transient Pin nodesPin = new Pin(new PinValueArray(PinType.NODE, false), R.string.pin_value_array, true);
    private transient Pin posPin = new Pin(new PinPoint(), R.string.pin_point);
    private transient Pin excludeGroupView = new Pin(new PinBoolean(true), R.string.action_get_nodes_in_pos_subtitle_exclude);


    public GetNodesInPosAction() {
        super(ActionType.NODES_IN_POS);
        nodesPin = addPin(nodesPin);
        posPin = addPin(posPin);
        excludeGroupView = addPin(excludeGroupView);
    }

    public GetNodesInPosAction(JsonObject jsonObject) {
        super(jsonObject);
        nodesPin = reAddPin(nodesPin);
        posPin = reAddPin(posPin);
        excludeGroupView = reAddPin(excludeGroupView);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        ArrayList<PinValue> values = nodesPin.getValue(PinValueArray.class).getValues();
        PinPoint pos = (PinPoint) getPinValue(runnable, context, posPin);
        PinBoolean exclude = (PinBoolean) getPinValue(runnable, context, excludeGroupView);
        ArrayList<AccessibilityNodeInfo> nodes = getNodeIn(pos.getX(MainApplication.getInstance()), pos.getY(MainApplication.getInstance()));
        for (AccessibilityNodeInfo node : nodes) {
            if (exclude.isBool()) {
                CharSequence className = node.getClassName();
                if (className != null) {
                    try {
                        Class<?> aClass = Class.forName(className.toString());
                        if (!ViewGroup.class.isAssignableFrom(aClass)) {
                            values.add(new PinNode(node));
                        }
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                values.add(new PinNode(node));
            }
        }
    }

    private ArrayList<AccessibilityNodeInfo> getNodeIn(int x, int y) {
        ArrayList<AccessibilityNodeInfo> nodes = new ArrayList<>();
        MainAccessibilityService service = MainApplication.getInstance().getService();
        for (AccessibilityNodeInfo info : service.getNeedWindowsRoot()) {
            nodes.addAll(findNodeIn(info, x, y));
        }
        return nodes;
    }

    private ArrayList<AccessibilityNodeInfo> findNodeIn(AccessibilityNodeInfo nodeInfo, int x, int y) {
        ArrayList<AccessibilityNodeInfo> nodes = new ArrayList<>();

        if (nodeInfo.isVisibleToUser()) {
            Rect rect = new Rect();
            nodeInfo.getBoundsInScreen(rect);
            if (rect.contains(x, y)) {
                nodes.add(nodeInfo);
                for (int i = 0; i < nodeInfo.getChildCount(); i++) {
                    AccessibilityNodeInfo child = nodeInfo.getChild(i);
                    if (child != null) {
                        nodes.addAll(findNodeIn(child, x, y));
                    }
                }
            }
        }
        return nodes;
    }
}
