package top.bogey.touch_tool_pro.bean.action.array;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionCheckResult;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArray;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class ArrayGetAction extends ArrayWithAction {
    private transient Pin boolPin = new Pin(new PinBoolean(), R.string.action_check_subtitle_result, true);
    private transient Pin valuePin = new Pin(new PinString(), R.string.action_array_subtitle_element, true);
    private transient Pin indexPin = new Pin(new PinInteger(1), R.string.pin_index);

    public ArrayGetAction() {
        super(ActionType.ARRAY_GET);
        boolPin = addPin(boolPin);
        valuePin = addPin(valuePin);
        indexPin = addPin(indexPin);
    }

    public ArrayGetAction(JsonObject jsonObject) {
        super(jsonObject);
        boolPin = reAddPin(boolPin);
        valuePin = reAddPin(valuePin, getPinType());
        indexPin = reAddPin(indexPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinValueArray array = (PinValueArray) getPinValue(runnable, context, arrayPin);
        PinInteger index = (PinInteger) getPinValue(runnable, context, indexPin);
        ArrayList<PinValue> values = array.getValues();
        if (index.getValue() > 0 && index.getValue() <= values.size()) {
            PinValue value = values.get(index.getValue() - 1);
            valuePin.setValue(value);
            boolPin.getValue(PinBoolean.class).setBool(true);
        } else {
            valuePin.setValue(createPinValue(array.getPinType()));
            boolPin.getValue(PinBoolean.class).setBool(false);
        }
    }

    @Override
    public void setValueType(FunctionContext context, PinType type) {
        super.setValueType(context, type);
        valuePin.setValue(createPinValue(type));
        valuePin.cleanLinks(context);
    }

    @Override
    public ActionCheckResult check(FunctionContext context) {
        if (boolPin.getLinks().isEmpty()) {
            if (!valuePin.getLinks().isEmpty()) {
                return new ActionCheckResult(ActionCheckResult.ActionResultType.ERROR, R.string.error_exist_action_tips);
            }
        }
        return super.check(context);
    }
}
