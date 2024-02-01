package top.bogey.touch_tool_pro.bean.action.array;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArray;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class ArrayAppendAction extends ArrayNormalAction {
    private transient Pin otherPin = new Pin(new PinValueArray(PinType.STRING), R.string.pin_value_array);
    private transient Pin resultPin = new Pin(new PinValueArray(PinType.STRING), R.string.pin_value_array, true);

    public ArrayAppendAction() {
        super(ActionType.ARRAY_APPEND);
        otherPin = addPin(otherPin);
        resultPin = addPin(resultPin);
    }

    public ArrayAppendAction(JsonObject jsonObject) {
        super(jsonObject);
        otherPin = reAddPin(otherPin);
        resultPin = reAddPin(resultPin);
    }

    @Override
    public void resetReturnValue(Pin pin) {

    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinValueArray array = (PinValueArray) getPinValue(runnable, context, arrayPin);
        PinValueArray other = (PinValueArray) getPinValue(runnable, context, otherPin);
        for (PinValue value : other.getValues()) {
            array.getValues().add((PinValue) value.copy());
        }
        resultPin.setValue(array);
        executeNext(runnable, context, outPin);
    }

    @Override
    public void setValueType(FunctionContext context, PinType type) {
        super.setValueType(context, type);
        otherPin.getValue(PinValueArray.class).setPinType(type);
        otherPin.cleanLinks(context);
        resultPin.getValue(PinValueArray.class).setPinType(type);
        resultPin.cleanLinks(context);
    }
}
