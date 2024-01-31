package top.bogey.touch_tool_pro.bean.action.number;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArea;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class IntToValueAreaAction extends Action {
    private transient Pin valueAreaPin = new Pin(new PinValueArea(Integer.MIN_VALUE, Integer.MAX_VALUE, 1, 1, 100), R.string.pin_value_area, true);
    private transient Pin xPin = new Pin(new PinInteger(), R.string.pin_value_area_low);
    private transient Pin yPin = new Pin(new PinInteger(), R.string.pin_value_area_high);

    public IntToValueAreaAction() {
        super(ActionType.INT_TO_VALUE_AREA);
        valueAreaPin = addPin(valueAreaPin);
        xPin = addPin(xPin);
        yPin = addPin(yPin);
    }

    public IntToValueAreaAction(JsonObject jsonObject) {
        super(jsonObject);
        valueAreaPin = reAddPin(valueAreaPin);
        xPin = reAddPin(xPin);
        yPin = reAddPin(yPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinInteger x = (PinInteger) getPinValue(runnable, context, xPin);
        PinInteger y = (PinInteger) getPinValue(runnable, context, yPin);
        PinValueArea valueArea = valueAreaPin.getValue(PinValueArea.class);
        valueArea.setLow(x.getValue());
        valueArea.setHigh(y.getValue());
    }
}
