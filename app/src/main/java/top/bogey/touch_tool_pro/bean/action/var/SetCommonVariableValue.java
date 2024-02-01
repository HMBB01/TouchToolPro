package top.bogey.touch_tool_pro.bean.action.var;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionCheckResult;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.save.SaveRepository;

public class SetCommonVariableValue extends SetVariableValue {

    public SetCommonVariableValue(String varKey, PinValue value) {
        super(ActionType.COMMON_VAR_SET, varKey, value);
    }

    public SetCommonVariableValue(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        if (isError(context)) return;
        PinValue value = (PinValue) getPinValue(runnable, context, valuePin);
        SaveRepository.getInstance().setVariable(varKey, (PinValue) value.copy());
        executeNext(runnable, context, outPin);
    }

    @Override
    public ActionCheckResult check(FunctionContext context) {
        PinValue value = SaveRepository.getInstance().getVariable(varKey);
        if (value == null) return new ActionCheckResult(ActionCheckResult.ActionResultType.ERROR, R.string.error_variable_no_find);
        return super.check(context);
    }

}
