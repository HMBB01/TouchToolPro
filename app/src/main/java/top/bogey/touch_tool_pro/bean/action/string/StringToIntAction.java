package top.bogey.touch_tool_pro.bean.action.string;

import com.google.gson.JsonObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class StringToIntAction extends Action {
    private transient Pin valuePin = new Pin(new PinInteger(), R.string.pin_int, true);
    private transient Pin textPin = new Pin(new PinString(), R.string.pin_string);

    public StringToIntAction() {
        super(ActionType.STRING_TO_INT);
        valuePin = addPin(valuePin);
        textPin = addPin(textPin);
    }

    public StringToIntAction(JsonObject jsonObject) {
        super(jsonObject);
        valuePin = reAddPin(valuePin);
        textPin = reAddPin(textPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        valuePin.getValue(PinInteger.class).setValue(0);
        PinString text = (PinString) getPinValue(runnable, context, textPin);
        if (text.getValue() == null || text.getValue().isEmpty()) return;
        Pattern pattern = Pattern.compile("^.*?(\\d+)");
        Matcher matcher = pattern.matcher(text.getValue());
        if (matcher.find()) {
            valuePin.getValue(PinInteger.class).cast(matcher.group(1));
        }
    }
}
