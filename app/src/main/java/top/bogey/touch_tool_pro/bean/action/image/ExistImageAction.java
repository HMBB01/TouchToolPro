package top.bogey.touch_tool_pro.bean.action.image;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionCheckResult;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.other.CheckAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinArea;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinImage;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.pin.pins.PinPoint;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;
import top.bogey.touch_tool_pro.utils.DisplayUtils;
import top.bogey.touch_tool_pro.utils.MatchResult;

public class ExistImageAction extends CheckAction {
    private transient Pin imagePin = new Pin(new PinImage(), R.string.pin_image);
    private transient Pin similarPin = new Pin(new PinInteger(85), R.string.action_exist_image_check_subtitle_similar);
    private transient Pin areaPin = new Pin(new PinArea(), R.string.pin_area);
    private transient Pin rectPin = new Pin(new PinArea(), R.string.pin_area, true);
    private transient Pin posPin = new Pin(new PinPoint(), R.string.pin_point, true);
    private transient Pin realSimilarPin = new Pin(new PinInteger(), R.string.action_exist_image_check_subtitle_result, true);

    public ExistImageAction() {
        super(ActionType.CHECK_EXIST_IMAGE);
        needCapture = true;
        imagePin = addPin(imagePin);
        similarPin = addPin(similarPin);
        areaPin = addPin(areaPin);
        rectPin = addPin(rectPin);
        posPin = addPin(posPin);
        realSimilarPin = addPin(realSimilarPin);
    }

    public ExistImageAction(JsonObject jsonObject) {
        super(jsonObject);
        needCapture = true;
        imagePin = reAddPin(imagePin);
        similarPin = reAddPin(similarPin);
        areaPin = reAddPin(areaPin);
        rectPin = reAddPin(rectPin);
        posPin = reAddPin(posPin);
        realSimilarPin = reAddPin(realSimilarPin);
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

        PinImage image = (PinImage) getPinValue(runnable, context, imagePin);
        Bitmap bitmap = image.getImage(service);
        if (bitmap == null) return;

        Bitmap currImage = runnable.getCurrImage(service);
        PinInteger similar = (PinInteger) getPinValue(runnable, context, similarPin);
        PinArea area = (PinArea) getPinValue(runnable, context, areaPin);
        Rect rect = area.getArea(service);
        MatchResult matchResult = DisplayUtils.matchImage(currImage, bitmap, rect);
        if (matchResult == null) return;
        if (matchResult.value >= Math.min(100, similar.getValue())) {
            result.setBool(true);
            matchResult.rect.offset(rect.left, rect.top);
            rectPin.getValue(PinArea.class).setArea(service, matchResult.rect);
            posPin.getValue(PinPoint.class).setPoint(service, matchResult.rect.centerX(), matchResult.rect.centerY());
            realSimilarPin.getValue(PinInteger.class).setValue(matchResult.value);
        }
    }

    @Override
    public ActionCheckResult check(FunctionContext context) {
        if (resultPin.getLinks().isEmpty()) {
            if (!posPin.getLinks().isEmpty() || !realSimilarPin.getLinks().isEmpty()) {
                return new ActionCheckResult(ActionCheckResult.ActionResultType.ERROR, R.string.error_result_pin_no_use);
            }
        }
        return super.check(context);
    }

    public Pin getImagePin() {
        return imagePin;
    }

    public Pin getPosPin() {
        return posPin;
    }
}
