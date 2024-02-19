package top.bogey.touch_tool_pro.bean.action.image;

import android.graphics.Bitmap;
import android.os.Build;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionCheckResult;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.normal.NormalAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinImage;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.utils.AppUtils;

public class SaveImageAction extends NormalAction {
    private transient Pin imagePin = new Pin(new PinImage(), R.string.pin_image);

    public SaveImageAction() {
        super(ActionType.SAVE_IMAGE);
        imagePin = addPin(imagePin);
    }

    public SaveImageAction(JsonObject jsonObject) {
        super(jsonObject);
        imagePin = reAddPin(imagePin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinImage image = (PinImage) getPinValue(runnable, context, imagePin);
        Bitmap bitmap = image.getImage(MainApplication.getInstance());
        if (bitmap != null) {
            AppUtils.saveImage(MainApplication.getInstance(), bitmap);
        }
        executeNext(runnable, context, outPin);
    }

    @Override
    public ActionCheckResult check(FunctionContext context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return new ActionCheckResult(ActionCheckResult.ActionResultType.ERROR, R.string.error_save_image_no_permission);
        }
        return super.check(context);
    }
}
