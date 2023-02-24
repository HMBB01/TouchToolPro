package top.bogey.touch_tool.data.action.state;

import android.content.Context;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinBoolean;

public class CaptureStateAction extends StateAction {

    public CaptureStateAction(Context context) {
        super(context, R.string.action_capture_state_title);
    }

    public CaptureStateAction(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    protected void calculatePinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinBoolean value = (PinBoolean) statePin.getValue();
        MainAccessibilityService service = MainApplication.getInstance().getService();
        value.setValue(service.isCaptureEnabled());
    }
}
