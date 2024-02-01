package top.bogey.touch_tool_pro.bean.action.color;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.google.gson.JsonObject;

import java.util.List;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionCheckResult;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.other.CheckAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinArea;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinColor;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.pin.pins.PinPoint;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;
import top.bogey.touch_tool_pro.utils.DisplayUtils;

public class ExistColorAction extends CheckAction {
    private transient Pin colorPin = new Pin(new PinColor(), R.string.pin_color);
    private transient Pin areaPin = new Pin(new PinArea(), R.string.pin_area);
    private transient Pin posPin = new Pin(new PinPoint(), R.string.pin_point, true);
    private transient Pin offsetPin = new Pin(new PinInteger(5), R.string.action_exist_color_check_subtitle_similar);

    public ExistColorAction() {
        super(ActionType.CHECK_EXIST_COLOR);
        needCapture = true;
        colorPin = addPin(colorPin);
        areaPin = addPin(areaPin);
        posPin = addPin(posPin);
        offsetPin = addPin(offsetPin);
    }

    public ExistColorAction(JsonObject jsonObject) {
        super(jsonObject);
        needCapture = true;
        colorPin = reAddPin(colorPin);
        areaPin = reAddPin(areaPin);
        posPin = reAddPin(posPin);
        offsetPin = reAddPin(offsetPin);
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
        if (!service.isCaptureEnabled()) return;

        PinColor color = (PinColor) getPinValue(runnable, context, colorPin);
        if (color.getColor() == null) return;

        Bitmap currImage = runnable.getCurrImage(service);
        PinArea area = (PinArea) getPinValue(runnable, context, areaPin);
        PinInteger offset = (PinInteger) getPinValue(runnable, context, offsetPin);
        List<Rect> rectList = DisplayUtils.matchColor(currImage, color.getColor(), area.getArea(service), offset.getValue());
        if (rectList == null || rectList.isEmpty()) return;

        result.setBool(true);
        Rect rect = rectList.get(0);
        posPin.getValue(PinPoint.class).setPoint(service, rect.centerX(), rect.centerY());
    }

    @Override
    public ActionCheckResult check(FunctionContext context) {
        if (resultPin.getLinks().isEmpty()) {
            if (!posPin.getLinks().isEmpty()) {
                return new ActionCheckResult(ActionCheckResult.ActionResultType.ERROR, R.string.error_result_pin_no_use);
            }
        }
        return super.check(context);
    }

    public Pin getColorPin() {
        return colorPin;
    }

    public Pin getPosPin() {
        return posPin;
    }
}
