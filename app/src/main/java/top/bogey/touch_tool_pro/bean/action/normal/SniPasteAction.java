package top.bogey.touch_tool_pro.bean.action.normal;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinColor;
import top.bogey.touch_tool_pro.bean.pin.pins.PinImage;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.ui.custom.KeepAliveFloatView;
import top.bogey.touch_tool_pro.ui.custom.SniPasteFloatView;

public class SniPasteAction extends NormalAction {
    private transient Pin valuePin = new Pin(new PinValue(), R.string.pin_value);

    public SniPasteAction() {
        super(ActionType.SNI_PASTE);
        valuePin = addPin(valuePin);
    }

    public SniPasteAction(JsonObject jsonObject) {
        super(jsonObject);
        valuePin = reAddPin(valuePin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinValue value = (PinValue) getPinValue(runnable, context, valuePin);
        KeepAliveFloatView keepView = MainApplication.getInstance().getKeepView();
        if (keepView != null) {
            final PinValue copyValue = (PinValue) value.copy();
            new Handler(Looper.getMainLooper()).post(() -> {
                Context ctx = keepView.getContext();
                if (copyValue instanceof PinImage image) {
                    new SniPasteFloatView(ctx, image.getImage(ctx)).show();
                } else if (copyValue instanceof PinColor color) {
                    new SniPasteFloatView(ctx, color.getColor()).show();
                } else {
                    new SniPasteFloatView(ctx, copyValue.toString()).show();
                }
            });
        }

        executeNext(runnable, context, outPin);
    }
}
