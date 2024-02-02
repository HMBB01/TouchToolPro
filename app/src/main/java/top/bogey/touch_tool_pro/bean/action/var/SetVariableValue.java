package top.bogey.touch_tool_pro.bean.action.var;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.normal.NormalAction;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class SetVariableValue extends NormalAction {
    protected final String varKey;
    protected final transient Pin valuePin;

    public SetVariableValue(ActionType type, String varKey, PinValue value) {
        super(type);
        this.varKey = varKey;
        valuePin = addPin(new Pin(value));
        valuePin.setTitle(varKey);
    }

    public SetVariableValue(JsonObject jsonObject) {
        super(jsonObject);
        varKey = GsonUtils.getAsString(jsonObject, "varKey", null);
        valuePin = addPin(tmpPins.remove(0));
    }

    @Override
    public String getTitle() {
        return varKey;
    }

    public String getVarKey() {
        return varKey;
    }

    public void setValue(PinValue value) {
        valuePin.setValue(value);
    }

    public PinValue getValue() {
        return valuePin.getValue(PinValue.class);
    }
}
