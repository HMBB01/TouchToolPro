package top.bogey.touch_tool_pro.bean.action.var;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionCheckResult;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class SetLocalVariableValue extends SetVariableValue {

    public SetLocalVariableValue(String varKey, PinValue value) {
        super(ActionType.VAR_SET, varKey, value);
    }

    public SetLocalVariableValue(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        if (isError(context)) return;
        PinValue value = (PinValue) getPinValue(runnable, context, valuePin);
        context.setVarOnParent(varKey, (PinValue) value.copy());
        executeNext(runnable, context, outPin);
    }

    @Override
    public ActionCheckResult check(FunctionContext context) {
        PinValue value = context.findVar(varKey);
        if (value == null) return new ActionCheckResult(ActionCheckResult.ActionResultType.ERROR, R.string.error_variable_no_find);
        return super.check(context);
    }
}
