package top.bogey.touch_tool.data.action.attribute;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.data.pin.object.PinString;

public class SetValueAction extends NormalAction {
    private transient Pin valuePin = new Pin(new PinString());
    private final String key;

    public SetValueAction(String key, PinObject value) {
        super(R.string.action_set_value_action_title);
        this.key = key;
        valuePin = addPin(new Pin(value, key, PinDirection.IN));
    }

    public SetValueAction(JsonObject jsonObject) {
        super(R.string.action_set_value_action_title, jsonObject);
        key = jsonObject.get("key").getAsString();
        valuePin = reAddPin(valuePin);
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinObject value = getPinValue(runnable, actionContext, valuePin);

        PinObject attr = actionContext.getAttr(key);
        ActionContext context = actionContext;
        while (attr == null) {
            context = context.getParent();
            if (context == null) break;
            attr = context.getAttr(key);
        }

        if (attr != null) {
            context.addAttr(key, value);
        }

        doNextAction(runnable, actionContext, outPin);
    }

    public String getKey() {
        return key;
    }

    public void setValue(PinObject value) {
        valuePin.setValue(value);
    }
}