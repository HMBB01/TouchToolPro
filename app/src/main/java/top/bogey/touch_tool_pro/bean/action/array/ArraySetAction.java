package top.bogey.touch_tool_pro.bean.action.array;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.normal.NormalAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinExecute;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArray;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class ArraySetAction extends ArrayNormalAction {
    private transient Pin falsePin = new Pin(new PinExecute(), R.string.action_logic_subtitle_false, true);
    private transient Pin indexPin = new Pin(new PinInteger(1), R.string.pin_index);
    private transient Pin valuePin = new Pin(new PinString(), R.string.action_array_subtitle_element);

    public ArraySetAction() {
        super(ActionType.ARRAY_SET);
        falsePin = addPin(falsePin);
        indexPin = addPin(indexPin);
        valuePin = addPin(valuePin);
    }

    public ArraySetAction(JsonObject jsonObject) {
        super(jsonObject);
        falsePin = reAddPin(falsePin);
        indexPin = reAddPin(indexPin);
        valuePin = reAddPin(valuePin, getPinType());
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinValueArray array = (PinValueArray) getPinValue(runnable, context, arrayPin);
        PinInteger index = (PinInteger) getPinValue(runnable, context, indexPin);
        PinValue value = (PinValue) getPinValue(runnable, context, valuePin);
        ArrayList<PinValue> values = array.getValues();
        if (index.getValue() > 0 && index.getValue() <= values.size()) {
            values.set(index.getValue() - 1, value);
            executeNext(runnable, context, outPin);
        } else {
            executeNext(runnable, context, falsePin);
        }
    }

    @Override
    public void setValueType(FunctionContext context, PinType type) {
        super.setValueType(context, type);
        valuePin.setValue(createPinValue(type));
        valuePin.cleanLinks(context);
    }
}
