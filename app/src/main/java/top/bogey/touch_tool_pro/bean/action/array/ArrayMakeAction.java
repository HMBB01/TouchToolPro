package top.bogey.touch_tool_pro.bean.action.array;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionMorePinInterface;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinAdd;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArray;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class ArrayMakeAction extends ArrayAction implements ActionMorePinInterface {
    private final transient Pin morePin = new Pin(new PinString(), R.string.action_array_subtitle_element);
    private transient Pin arrayPin = new Pin(new PinValueArray(PinType.STRING), R.string.pin_value_array, true);
    private transient Pin addPin = new Pin(new PinAdd(morePin), R.string.action_subtitle_add_pin);

    public ArrayMakeAction() {
        super(ActionType.ARRAY_MAKE);
        arrayPin = addPin(arrayPin);
        addPin = addPin(addPin);
    }

    public ArrayMakeAction(JsonObject jsonObject) {
        super(jsonObject);
        arrayPin = reAddPin(arrayPin);
        reAddPin(morePin, 1, getPinType());
        addPin = reAddPin(addPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        ArrayList<PinValue> values = arrayPin.getValue(PinValueArray.class).getValues();
        for (Pin valuePin : calculateMorePins()) {
            PinValue value = (PinValue) getPinValue(runnable, context, valuePin);
            values.add((PinValue) value.copy());
        }
    }

    @Override
    public void setValueType(FunctionContext context, PinType type) {
        arrayPin.getValue(PinValueArray.class).setPinType(type);
        arrayPin.cleanLinks(context);
        for (Pin pin : calculateMorePins()) {
            pin.setValue(createPinValue(type));
            pin.cleanLinks(context);
        }
        addPin.getValue(PinAdd.class).getPin().setValue(createPinValue(type));
    }

    @Override
    public ArrayList<Pin> calculateMorePins() {
        ArrayList<Pin> pins = new ArrayList<>();
        boolean start = false;
        for (Pin pin : getPins()) {
            if (pin == addPin) start = false;
            if (start) pins.add(pin);
            if (pin == arrayPin) start = true;
        }
        return pins;
    }

    @Override
    protected PinType getPinType() {
        return arrayPin.getValue(PinValueArray.class).getPinType();
    }
}
