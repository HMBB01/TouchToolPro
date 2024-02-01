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
import top.bogey.touch_tool_pro.save.SaveRepository;

public class GetCommonVariableValue extends GetVariableValue {

    public GetCommonVariableValue(String varKey, PinValue value) {
        super(ActionType.COMMON_VAR_GET, varKey, value);
    }

    public GetCommonVariableValue(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinValue value = SaveRepository.getInstance().getVariable(varKey);
        if (value == null) return;
        if (value instanceof PinValueArray) {
            valuePin.setValue(value);
        } else {
            valuePin.setValue(value.copy());
        }
    }

    @Override
    public ActionCheckResult check(FunctionContext context) {
        PinValue value = SaveRepository.getInstance().getVariable(varKey);
        if (value == null) return new ActionCheckResult(ActionCheckResult.ActionResultType.ERROR, R.string.error_variable_no_find);
        return super.check(context);
    }

}
