package top.bogey.touch_tool_pro.bean.action.string;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionMorePinInterface;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinSubType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinAdd;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class StringAddAction extends Action implements ActionMorePinInterface {
    private final transient Pin morePin = new Pin(new PinString(PinSubType.MULTI_LINE), R.string.pin_string);
    private transient Pin resultPin = new Pin(new PinString(), R.string.pin_string, true);
    private transient Pin firstPin = new Pin(new PinString(PinSubType.MULTI_LINE), R.string.pin_string);
    private transient Pin secondPin = new Pin(new PinString(PinSubType.MULTI_LINE), R.string.pin_string);
    private transient Pin addPin = new Pin(new PinAdd(morePin), R.string.action_subtitle_add_pin);

    public StringAddAction() {
        super(ActionType.STRING_ADD);
        resultPin = addPin(resultPin);
        firstPin = addPin(firstPin);
        secondPin = addPin(secondPin);
        addPin = addPin(addPin);
    }

    public StringAddAction(JsonObject jsonObject) {
        super(jsonObject);
        resultPin = reAddPin(resultPin);
        firstPin = reAddPin(firstPin);
        secondPin = reAddPin(secondPin);
        reAddPin(morePin, 1);
        addPin = reAddPin(addPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinString result = resultPin.getValue(PinString.class);

        StringBuilder builder = new StringBuilder();
        for (Pin stringPin : calculateMorePins()) {
            PinString string = (PinString) getPinValue(runnable, context, stringPin);
            if (string.getValue() == null || string.getValue().isEmpty()) continue;
            builder.append(string.getValue());
        }

        result.setValue(builder.toString());
    }

    @Override
    public ArrayList<Pin> calculateMorePins() {
        ArrayList<Pin> pins = new ArrayList<>();
        boolean start = false;
        for (Pin pin : getPins()) {
            if (pin == firstPin) start = true;
            if (pin == addPin) start = false;
            if (start) pins.add(pin);
        }
        return pins;
    }
}
