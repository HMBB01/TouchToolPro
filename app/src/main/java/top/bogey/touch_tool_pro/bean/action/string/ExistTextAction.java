package top.bogey.touch_tool_pro.bean.action.string;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.JsonObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionCheckResult;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.other.CheckAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinArea;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinNode;
import top.bogey.touch_tool_pro.bean.pin.pins.PinPoint;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;

public class ExistTextAction extends CheckAction {
    private transient Pin textPin = new Pin(new PinString(), R.string.pin_string);
    private transient Pin justScreenPin = new Pin(new PinBoolean(true), R.string.action_exist_text_check_subtitle_just);
    private transient Pin areaPin = new Pin(new PinArea(), R.string.pin_area);
    private transient Pin posPin = new Pin(new PinPoint(), R.string.pin_point, true);
    private transient Pin nodePin = new Pin(new PinNode(), R.string.pin_node, true);

    public ExistTextAction() {
        super(ActionType.CHECK_EXIST_TEXT);
        textPin = addPin(textPin);
        justScreenPin = addPin(justScreenPin);
        areaPin = addPin(areaPin);
        posPin = addPin(posPin);
        nodePin = addPin(nodePin);
    }

    public ExistTextAction(JsonObject jsonObject) {
        super(jsonObject);
        textPin = reAddPin(textPin);
        justScreenPin = reAddPin(justScreenPin);
        areaPin = reAddPin(areaPin);
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

        PinString text = (PinString) getPinValue(runnable, context, textPin);
        if (text.getValue() == null || text.getValue().isEmpty()) return;

        MainAccessibilityService service = MainApplication.getInstance().getService();
        AccessibilityNodeInfo root = service.getRootInActiveWindow();
        PinBoolean justScreen = (PinBoolean) getPinValue(runnable, context, justScreenPin);
        PinArea area = (PinArea) getPinValue(runnable, context, areaPin);
        AccessibilityNodeInfo searchNode = searchNode(root, Pattern.compile(text.getValue()), justScreen.isBool(), area.getArea(service));
        if (searchNode == null) return;

        result.setBool(true);
        PinPoint value = posPin.getValue(PinPoint.class);
        Rect rect = new Rect();
        searchNode.getBoundsInScreen(rect);
        value.setPoint(service, rect.centerX(), rect.centerY());

        nodePin.getValue(PinNode.class).setNode(searchNode);
    }

    private AccessibilityNodeInfo searchNode(AccessibilityNodeInfo root, Pattern pattern, boolean justScreen, Rect area) {
        if (root == null) return null;
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
                    if (matcher.find()) return child;
                }
                AccessibilityNodeInfo node = searchNode(child, pattern, justScreen, area);
                if (node != null) return node;
            }
        }
        return null;
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

    public Pin getTextPin() {
        return textPin;
    }

    public Pin getNodePin() {
        return nodePin;
    }
}
