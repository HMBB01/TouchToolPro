package top.bogey.touch_tool_pro.bean.action.image;

import android.graphics.Bitmap;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.other.CheckAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinImage;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.utils.DisplayUtils;
import top.bogey.touch_tool_pro.utils.MatchResult;

public class ImageContainAction extends CheckAction {
    private transient Pin imagePin = new Pin(new PinImage(), R.string.pin_image);
    private transient Pin otherPin = new Pin(new PinImage(), R.string.action_image_check_subtitle_other);
    private transient Pin similarPin = new Pin(new PinInteger(85), R.string.action_exist_image_check_subtitle_similar);

    public ImageContainAction() {
        super(ActionType.CHECK_IMAGE);
        imagePin = addPin(imagePin);
        otherPin = addPin(otherPin);
        similarPin = addPin(similarPin);
    }

    public ImageContainAction(JsonObject jsonObject) {
        super(jsonObject);
        imagePin = reAddPin(imagePin);
        otherPin = reAddPin(otherPin);
        similarPin = reAddPin(similarPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinBoolean result = resultPin.getValue(PinBoolean.class);

        MainApplication instance = MainApplication.getInstance();
        PinImage image = (PinImage) getPinValue(runnable, context, imagePin);
        Bitmap bitmap = image.getImage(instance);
        if (bitmap == null) return;
        PinImage other = (PinImage) getPinValue(runnable, context, otherPin);
        Bitmap otherBitmap = other.getImage(instance);
        if (otherBitmap == null) return;

        PinInteger similar = (PinInteger) getPinValue(runnable, context, similarPin);
        MatchResult matchResult = DisplayUtils.nativeMatchTemplate(bitmap, otherBitmap);
        if (matchResult.value >= similar.getValue()) result.setBool(true);
    }
}
