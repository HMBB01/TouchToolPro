package top.bogey.touch_tool_pro.bean.action.start;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.other.ScreenStateAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinSpinner;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.utils.AppUtils;

public class ScreenStartAction extends StartAction {
    private transient Pin statePin = new Pin(new PinSpinner(R.array.screen_state), R.string.action_screen_start_subtitle_screen, true);

    public ScreenStartAction() {
        super(ActionType.SCREEN_START);
        statePin = addPin(statePin);
    }

    public ScreenStartAction(JsonObject jsonObject) {
        super(jsonObject);
        statePin = reAddPin(statePin);
    }

    @Override
    public void resetReturnValue(Pin pin) {

    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        ScreenStateAction.ScreenState state = AppUtils.getScreenState(MainApplication.getInstance());
        statePin.getValue(PinSpinner.class).setIndex(state.ordinal());
        super.execute(runnable, context, pin);
    }

    @Override
    public boolean checkReady(TaskRunnable runnable, FunctionContext context) {
        return true;
    }
}
