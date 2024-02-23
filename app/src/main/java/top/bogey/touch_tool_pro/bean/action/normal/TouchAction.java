package top.bogey.touch_tool_pro.bean.action.normal;

import android.os.Build;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinFloat;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.pin.pins.PinObject;
import top.bogey.touch_tool_pro.bean.pin.pins.PinTouch;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;

public class TouchAction extends NormalAction {
    private transient Pin touchPin = new Pin(new PinTouch(), R.string.pin_touch);
    private transient Pin scalePin = new Pin(new PinFloat(1), R.string.action_touch_path_action_subtitle_scale);
    private transient Pin offsetPin = new Pin(new PinInteger(), R.string.action_touch_path_action_subtitle_offset);

    public TouchAction() {
        super(ActionType.TOUCH);
        touchPin = addPin(touchPin);
        scalePin = addPin(scalePin);
        offsetPin = addPin(offsetPin);
    }

    public TouchAction(JsonObject jsonObject) {
        super(jsonObject);
        touchPin = reAddPin(touchPin);
        scalePin = reAddPin(scalePin);
        offsetPin = reAddPin(offsetPin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinTouch touch = (PinTouch) getPinValue(runnable, context, touchPin);
        PinObject scale = getPinValue(runnable, context, scalePin);
        float scaleValue = PinFloat.getFloatValue(scale);

        PinInteger offset = (PinInteger) getPinValue(runnable, context, offsetPin);

        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            service.runGesture(touch.getStrokeList(service, scaleValue, offset.getValue()), result -> runnable.resume());
        } else {
            service.runGesture(touch.getStrokes(service, scaleValue, offset.getValue()), result -> runnable.resume());
        }
        service.showTouch(touch, scaleValue);
        runnable.pause();
        executeNext(runnable, context, outPin);
    }

    public Pin getTouchPin() {
        return touchPin;
    }
}
