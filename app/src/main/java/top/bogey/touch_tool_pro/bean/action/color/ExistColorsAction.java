package top.bogey.touch_tool_pro.bean.action.color;

import android.graphics.Bitmap;
import android.graphics.Rect;

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
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinArea;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinColor;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.pin.pins.PinPoint;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArray;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;
import top.bogey.touch_tool_pro.utils.DisplayUtils;

public class ExistColorsAction extends CheckAction {
    private transient Pin colorPin = new Pin(new PinColor(), R.string.pin_color);
    private transient Pin areaPin = new Pin(new PinArea(), R.string.pin_area);
    private transient Pin offsetPin = new Pin(new PinInteger(5), R.string.action_exist_color_check_subtitle_similar);
    private transient Pin allPosPin = new Pin(new PinValueArray(PinType.POINT, false), R.string.pin_value_array, true);

    public ExistColorsAction() {
        super(ActionType.CHECK_EXIST_COLORS);
        needCapture = true;
        colorPin = addPin(colorPin);
        areaPin = addPin(areaPin);
        offsetPin = addPin(offsetPin);
        allPosPin = addPin(allPosPin);
    }

    public ExistColorsAction(JsonObject jsonObject) {
        super(jsonObject);
        needCapture = true;
        colorPin = reAddPin(colorPin);
        areaPin = reAddPin(areaPin);
        offsetPin = reAddPin(offsetPin);
        allPosPin = reAddPin(allPosPin);
    }

    @Override
    public void resetReturnValue(Pin pin) {
        if (pin.equals(resultPin)) super.resetReturnValue(pin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        if (!pin.equals(resultPin)) return;

        PinBoolean result = resultPin.getValue(PinBoolean.class);
        ArrayList<PinValue> values = allPosPin.getValue(PinValueArray.class).getValues();

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
        for (Rect r : rectList) {
            values.add(new PinPoint(service, r.centerX(), r.centerY()));
        }
    }

    @Override
    public ActionCheckResult check(FunctionContext context) {
        if (resultPin.getLinks().isEmpty()) {
            if (!allPosPin.getLinks().isEmpty()) {
                return new ActionCheckResult(ActionCheckResult.ActionResultType.ERROR, R.string.error_result_pin_no_use);
            }
        }
        return super.check(context);
    }
}
