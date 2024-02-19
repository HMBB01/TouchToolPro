package top.bogey.touch_tool_pro.bean.action.start;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.other.BatteryStateAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.pin.pins.PinSpinner;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.service.WorldState;

public class BatteryStartAction extends StartAction {
    private transient Pin statePin = new Pin(new PinSpinner(R.array.charging_state), R.string.action_battery_start_subtitle_state, true);
    private transient Pin valuePin = new Pin(new PinInteger(), R.string.action_battery_start_subtitle_battery, true);

    public BatteryStartAction() {
        super(ActionType.BATTERY_START);
        statePin = addPin(statePin);
        valuePin = addPin(valuePin);
    }

    public BatteryStartAction(JsonObject jsonObject) {
        super(jsonObject);
        statePin = reAddPin(statePin);
        valuePin = reAddPin(valuePin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        int state = WorldState.getInstance().getBatteryState();
        int percent = WorldState.getInstance().getBatteryPercent();
        statePin.getValue(PinSpinner.class).setIndex(BatteryStateAction.stateToIndex(state));
        valuePin.getValue(PinInteger.class).setValue(percent);
        super.execute(runnable, context, pin);
    }

    @Override
    public boolean checkReady(TaskRunnable runnable, FunctionContext context) {
        return true;
    }
}
