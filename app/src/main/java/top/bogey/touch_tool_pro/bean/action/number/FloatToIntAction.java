package top.bogey.touch_tool_pro.bean.action.number;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinFloat;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.pin.pins.PinObject;
import top.bogey.touch_tool_pro.bean.pin.pins.PinSpinner;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class FloatToIntAction extends Action {
    protected transient Pin resultPin = new Pin(new PinInteger(), R.string.pin_int, true);
    protected transient Pin floatPin = new Pin(new PinFloat(), R.string.pin_float);
    protected transient Pin typePin = new Pin(new PinSpinner(R.array.float_to_int_type), R.string.action_float_to_int_subtitle_type);

    public FloatToIntAction() {
        super(ActionType.FLOAT_TO_INT);
        resultPin = addPin(resultPin);
        floatPin = addPin(floatPin);
        typePin = addPin(typePin);
    }

    public FloatToIntAction(JsonObject jsonObject) {
        super(jsonObject);
        resultPin = reAddPin(resultPin);
        floatPin = reAddPin(floatPin);
        typePin = reAddPin(typePin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinObject value = getPinValue(runnable, context, floatPin);
        float v = PinFloat.getFloatValue(value);
        PinSpinner type = (PinSpinner) getPinValue(runnable, context, typePin);
        int result = 0;
        switch (type.getIndex()) {
            case 0 -> result = Math.round(v);
            case 1 -> result = (int) Math.floor(v);
            case 2 -> result = (int) Math.ceil(v);
        }
        resultPin.getValue(PinInteger.class).setValue(result);
    }
}
