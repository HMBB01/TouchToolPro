package top.bogey.touch_tool_pro.bean.action.var;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionCheckResult;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArray;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class GetLocalVariableValue extends GetVariableValue {

    public GetLocalVariableValue(String varKey, PinValue value) {
        super(ActionType.VAR_GET, varKey, value);
    }

    public GetLocalVariableValue(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinValue value = context.findVar(varKey);
        if (value == null) return;
        if (value instanceof PinValueArray) {
            valuePin.setValue(value);
        } else {
            valuePin.setValue(value.copy());
        }
    }

    @Override
    public ActionCheckResult check(FunctionContext context) {
        PinValue value = context.findVar(varKey);
        if (value == null) return new ActionCheckResult(ActionCheckResult.ActionResultType.ERROR, R.string.error_variable_no_find);
        return super.check(context);
    }

}
