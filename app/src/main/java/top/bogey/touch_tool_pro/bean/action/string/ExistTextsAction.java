package top.bogey.touch_tool_pro.bean.action.string;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionCheckResult;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.other.CheckAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinArea;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinNode;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArray;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;

public class ExistTextsAction extends CheckAction {
    private transient Pin textPin = new Pin(new PinString(), R.string.pin_string);
    private transient Pin justScreenPin = new Pin(new PinBoolean(true), R.string.action_exist_text_check_subtitle_just);
    private transient Pin areaPin = new Pin(new PinArea(), R.string.pin_area);
    private transient Pin nodesPin = new Pin(new PinValueArray(PinType.NODE, false), R.string.pin_value_array, true);

    public ExistTextsAction() {
        super(ActionType.CHECK_EXIST_TEXTS);
        textPin = addPin(textPin);
        justScreenPin = addPin(justScreenPin);
        areaPin = addPin(areaPin);
        nodesPin = addPin(nodesPin);
    }

    public ExistTextsAction(JsonObject jsonObject) {
        super(jsonObject);
        textPin = reAddPin(textPin);
        justScreenPin = reAddPin(justScreenPin);
        areaPin = reAddPin(areaPin);
        nodesPin = reAddPin(nodesPin);
    }

    @Override
    public void resetReturnValue(Pin pin) {
        if (pin.equals(resultPin)) super.resetReturnValue(pin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        if (!pin.equals(resultPin)) return;

        PinBoolean result = resultPin.getValue(PinBoolean.class);
        ArrayList<PinValue> values = nodesPin.getValue(PinValueArray.class).getValues();

        PinString text = (PinString) getPinValue(runnable, context, textPin);
        if (text.getValue() == null || text.getValue().isEmpty()) return;

        MainAccessibilityService service = MainApplication.getInstance().getService();
        AccessibilityNodeInfo root = service.getRootInActiveWindow();
        PinBoolean justScreen = (PinBoolean) getPinValue(runnable, context, justScreenPin);
        PinArea area = (PinArea) getPinValue(runnable, context, areaPin);
        ArrayList<AccessibilityNodeInfo> nodes = searchNodes(root, Pattern.compile(text.getValue()), justScreen.isBool(), area.getArea(service));
        if (nodes.isEmpty()) return;

        result.setBool(true);
        for (AccessibilityNodeInfo node : nodes) {
            values.add(new PinNode(node));
        }
    }

    private ArrayList<AccessibilityNodeInfo> searchNodes(AccessibilityNodeInfo root, Pattern pattern, boolean justScreen, Rect area) {
        ArrayList<AccessibilityNodeInfo> nodes = new ArrayList<>();
        if (root == null) return nodes;
        Rect rect = new Rect();
        for (int i = 0; i < root.getChildCount(); i++) {
            AccessibilityNodeInfo child = root.getChild(i);
            if (child != null) {
                if (justScreen) {
                    child.getBoundsInScreen(rect);
                    if (!Rect.intersects(area, rect)) continue;
                }
                CharSequence text = child.getText();
                if (text != null && text.length() > 0) {
                    Matcher matcher = pattern.matcher(text);
                    if (matcher.find()) {
                        nodes.add(child);
                    }
                }
                ArrayList<AccessibilityNodeInfo> nodeList = searchNodes(child, pattern, justScreen, area);
                nodes.addAll(nodeList);
            }
        }
        return nodes;
    }

    @Override
    public ActionCheckResult check(FunctionContext context) {
        if (resultPin.getLinks().isEmpty()) {
            if (!nodesPin.getLinks().isEmpty()) {
                return new ActionCheckResult(ActionCheckResult.ActionResultType.ERROR, R.string.error_result_pin_no_use);
            }
        }
        return super.check(context);
    }
}
