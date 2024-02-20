package top.bogey.touch_tool_pro.bean.action.start;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinExecute;
import top.bogey.touch_tool_pro.bean.pin.pins.PinSpinner;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public abstract class StartAction extends Action {
    private transient boolean checkStopping = false;

    protected transient Pin breakPin = new Pin(new PinBoolean(false), R.string.action_start_subtitle_break);
    private transient Pin executePin = new Pin(new PinExecute(), R.string.action_subtitle_execute, true);
    private transient Pin enablePin = new Pin(new PinBoolean(true), R.string.action_start_subtitle_enable);
    private transient Pin restartPin = new Pin(new PinSpinner(R.array.restart_type), R.string.action_start_subtitle_restart);

    public StartAction(ActionType type) {
        super(type);
        executePin = addPin(executePin);
        enablePin = addPin(enablePin);
        restartPin = addPin(restartPin);
        breakPin = addPin(breakPin);
    }

    public StartAction(JsonObject jsonObject) {
        super(jsonObject);
        executePin = reAddPin(executePin);
        enablePin = reAddPin(enablePin);
        restartPin = reAddPin(restartPin);
        breakPin = reAddPin(breakPin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        checkStopping = false;
        executeNext(runnable, context, executePin);
    }

    public abstract boolean checkReady(TaskRunnable runnable, FunctionContext context);

    public boolean checkStop(TaskRunnable runnable, FunctionContext context) {
        if (checkStopping) return false;
        checkStopping = true;
        PinBoolean bool = (PinBoolean) getPinValue(runnable, context, breakPin);
        checkStopping = false;
        return bool.isBool();
    }

    @Override
    public void resetReturnValue(Pin pin) {

    }

    public boolean isEnable() {
        return enablePin.getValue(PinBoolean.class).isBool();
    }

    public void setEnable(boolean enable) {
        enablePin.getValue(PinBoolean.class).setBool(enable);
    }

    public RestartType getRestartType() {
        int index = restartPin.getValue(PinSpinner.class).getIndex();
        return RestartType.values()[index];
    }
}
