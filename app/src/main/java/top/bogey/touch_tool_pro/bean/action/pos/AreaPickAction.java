package top.bogey.touch_tool_pro.bean.action.pos;

import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.JsonObject;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.normal.NormalAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinArea;
import top.bogey.touch_tool_pro.bean.pin.pins.PinExecute;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.ui.custom.KeepAliveFloatView;
import top.bogey.touch_tool_pro.ui.picker.AreaPickerFloatView;
import top.bogey.touch_tool_pro.ui.picker.PickerCallback;

public class AreaPickAction extends NormalAction {
    private transient Pin areaPin = new Pin(new PinArea(), R.string.pin_area, true);
    private transient Pin falsePin = new Pin(new PinExecute(), R.string.action_logic_subtitle_false, true);

    public AreaPickAction() {
        super(ActionType.AREA_PICK);
        areaPin = addPin(areaPin);
        falsePin = addPin(falsePin);
    }

    public AreaPickAction(JsonObject jsonObject) {
        super(jsonObject);
        areaPin = reAddPin(areaPin);
        falsePin = reAddPin(falsePin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        KeepAliveFloatView keepView = MainApplication.getInstance().getKeepView();
        AtomicBoolean complete = new AtomicBoolean(false);
        if (keepView != null) {
            PinArea area = areaPin.getValue(PinArea.class);
            AtomicReference<AreaPickerFloatView> floatView = new AtomicReference<>();

            new Handler(Looper.getMainLooper()).post(() -> {
                AreaPickerFloatView view = new AreaPickerFloatView(keepView.getContext(), new PickerCallback() {
                    @Override
                    public void onComplete() {
                        complete.set(true);
                        runnable.resume();
                    }

                    @Override
                    public void onCancel() {
                        complete.set(false);
                        runnable.resume();
                    }
                }, area);
                floatView.set(view);
                view.setPickerCallback(new AreaPickerFloatView.AreaPickerInTaskCallback(view));
                view.show();
            });

            runnable.pause(600000);
            AreaPickerFloatView view = floatView.get();
            if (view != null) view.dismiss();
        } else {
            areaPin.getValue(PinArea.class).setArea(MainApplication.getInstance(), new Rect());
        }

        if (complete.get()) executeNext(runnable, context, outPin);
        else executeNext(runnable, context, falsePin);
    }
}
