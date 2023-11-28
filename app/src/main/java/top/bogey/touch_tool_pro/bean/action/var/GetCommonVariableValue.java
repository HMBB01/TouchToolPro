package top.bogey.touch_tool_pro.bean.action.var;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionCheckResult;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.base.SaveRepository;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class GetCommonVariableValue extends Action {
    private final String varKey;
    private final transient Pin valuePin;

    public GetCommonVariableValue(String varKey, PinValue value) {
        super(ActionType.COMMON_VAR_GET);
        this.varKey = varKey;
        valuePin = addPin(new Pin(value, true));
        valuePin.setTitle(varKey);
    }

    public GetCommonVariableValue(JsonObject jsonObject) {
        super(jsonObject);
        varKey = GsonUtils.getAsString(jsonObject, "varKey", null);
        valuePin = addPin(tmpPins.remove(0));
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinValue value = SaveRepository.getInstance().getVariable(varKey);
        if (value == null) return;
        valuePin.setValue(value.copy());
    }

    @Override
    public ActionCheckResult check(FunctionContext context) {
        PinValue value = SaveRepository.getInstance().getVariable(varKey);
        if (value == null) return new ActionCheckResult(ActionCheckResult.ActionResultType.ERROR, R.string.error_variable_action_tips);
        return super.check(context);
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
