package top.bogey.touch_tool_pro.bean.action.array;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinExecute;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArray;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class ArrayForLogicAction extends ArrayNormalAction {
    private transient Pin breakPin = new Pin(new PinExecute(), R.string.action_logic_subtitle_break);
    private transient Pin completePin = new Pin(new PinExecute(), R.string.action_logic_subtitle_complete, true);
    private transient Pin indexPin = new Pin(new PinInteger(), R.string.pin_index, true);
    private transient Pin valuePin = new Pin(new PinString(), R.string.action_array_subtitle_element, true);
    private transient boolean needBreak;

    public ArrayForLogicAction() {
        super(ActionType.ARRAY_FOR);
        breakPin = addPin(breakPin);
        completePin = addPin(completePin);
        indexPin = addPin(indexPin);
        valuePin = addPin(valuePin);
        needBreak = false;
    }

    public ArrayForLogicAction(JsonObject jsonObject) {
        super(jsonObject);
        breakPin = reAddPin(breakPin);
        completePin = reAddPin(completePin);
        indexPin = reAddPin(indexPin);
        valuePin = reAddPin(valuePin, getPinType());
        needBreak = false;
    }

    @Override
    public void resetReturnValue(Pin pin) {

    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        if (pin.equals(inPin)) {
            needBreak = false;
            PinValueArray array = (PinValueArray) getPinValue(runnable, context, arrayPin);
            ArrayList<PinValue> values = array.getValues();
            for (int i = 0; i < values.size(); i++) {
                if (runnable.isInterrupt() || context.isEnd()) return;
                PinValue value = values.get(i);
                indexPin.getValue(PinInteger.class).setValue(i + 1);
                valuePin.setValue(value);
                executeNext(runnable, context, outPin);
                if (needBreak) break;
            }
            executeNext(runnable, context, completePin);
        } else {
            needBreak = true;
        }
    }

    @Override
    public void setValueType(FunctionContext context, PinType type) {
        super.setValueType(context, type);
        valuePin.setValue(createPinValue(type));
        valuePin.cleanLinks(context);
    }
}
