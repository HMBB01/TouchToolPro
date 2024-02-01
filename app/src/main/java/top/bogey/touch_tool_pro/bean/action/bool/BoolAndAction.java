package top.bogey.touch_tool_pro.bean.action.bool;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionMorePinInterface;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.other.CheckAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinAdd;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class BoolAndAction extends CheckAction implements ActionMorePinInterface {
    private final transient Pin morePin = new Pin(new PinBoolean(), R.string.pin_boolean);
    private transient Pin firstPin = new Pin(new PinBoolean(), R.string.pin_boolean);
    private transient Pin secondPin = new Pin(new PinBoolean(), R.string.pin_boolean);
    private transient Pin addPin = new Pin(new PinAdd(morePin), R.string.action_subtitle_add_pin);

    public BoolAndAction() {
        super(ActionType.BOOL_AND);
        firstPin = addPin(firstPin);
        secondPin = addPin(secondPin);
        addPin = addPin(addPin);
    }

    public BoolAndAction(JsonObject jsonObject) {
        super(jsonObject);
        firstPin = reAddPin(firstPin);
        secondPin = reAddPin(secondPin);
        reAddPin(morePin, 1);
        addPin = reAddPin(addPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinBoolean result = resultPin.getValue(PinBoolean.class);
        for (Pin boolPin : calculateMorePins()) {
            PinBoolean bool = (PinBoolean) getPinValue(runnable, context, boolPin);
            if (!bool.isBool()) return;
        }

        result.setBool(true);
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
