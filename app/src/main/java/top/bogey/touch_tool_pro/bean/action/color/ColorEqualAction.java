package top.bogey.touch_tool_pro.bean.action.color;

import android.util.Log;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.other.CheckAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinColor;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class ColorEqualAction extends CheckAction {
    private transient Pin colorPin = new Pin(new PinColor(), R.string.pin_color);
    private transient Pin otherPin = new Pin(new PinColor(), R.string.action_color_check_subtitle_other);
    private transient Pin offsetPin = new Pin(new PinInteger(5), R.string.action_exist_color_check_subtitle_similar);

    public ColorEqualAction() {
        super(ActionType.CHECK_COLOR);
        colorPin = addPin(colorPin);
        otherPin = addPin(otherPin);
        offsetPin = addPin(offsetPin);
    }

    public ColorEqualAction(JsonObject jsonObject) {
        super(jsonObject);
        colorPin = reAddPin(colorPin);
        otherPin = reAddPin(otherPin);
        offsetPin = reAddPin(offsetPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinBoolean result = resultPin.getValue(PinBoolean.class);

        PinColor color = (PinColor) getPinValue(runnable, context, colorPin);
        PinColor other = (PinColor) getPinValue(runnable, context, otherPin);
        PinInteger offset = (PinInteger) getPinValue(runnable, context, offsetPin);
        Log.d("TAG", "color: " + color);
        Log.d("TAG", "other: " + other);

        int[] colorColor = color.getColor();
        int[] otherColor = other.getColor();
        int offsetValue = offset.getValue();

        if (colorColor[0] - offsetValue <= otherColor[0] && otherColor[0] <= colorColor[0] + offsetValue &&
                colorColor[1] - offsetValue <= otherColor[1] && otherColor[1] <= colorColor[1] + offsetValue &&
                colorColor[2] - offsetValue <= otherColor[2] && otherColor[2] <= colorColor[2] + offsetValue) {
            result.setBool(true);
        }
    }
}
