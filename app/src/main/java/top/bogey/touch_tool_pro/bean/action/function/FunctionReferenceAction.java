package top.bogey.touch_tool_pro.bean.action.function;

import android.util.Log;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionCheckResult;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.Function;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinObject;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.save.SaveRepository;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class FunctionReferenceAction extends Action {
    private final String functionId;
    private String parentId;
    private transient Function function;

    private transient boolean synced = false;
    private transient Function executeFunction;

    public FunctionReferenceAction(Function function) {
        super(ActionType.CUSTOM);
        parentId = function.getParentId();
        functionId = function.getId();
    }

    public FunctionReferenceAction(JsonObject jsonObject) {
        super(jsonObject);
        parentId = GsonUtils.getAsString(jsonObject, "parentId", null);
        functionId = GsonUtils.getAsString(jsonObject, "functionId", null);
        tmpPins.forEach(this::addPin);
        tmpPins.clear();
    }

    @Override
    public String getTitle() {
        function = SaveRepository.getInstance().getFunction(parentId, functionId);
        if (function == null) return super.getTitle();
        return function.getTitle();
    }

    @Override
    public ActionCheckResult check(FunctionContext context) {
        function = SaveRepository.getInstance().getFunction(parentId, functionId);
        if (function == null) {
            return new ActionCheckResult(ActionCheckResult.ActionResultType.ERROR, R.string.error_function_no_find);
        }
        return super.check(context);
    }

    @Override
    public void resetReturnValue(Pin pin) {
        if (function == null) return;
        if (function.isJustCall()) super.resetReturnValue(pin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        Log.d("TAG", "execute: " + context);
        if (!synced) sync(context);
        if (function == null) return;
        if (executeFunction == null) executeFunction = new Function(function, this, context);
        function.execute(runnable, executeFunction, pin);
    }

    @Override
    public void executeNext(TaskRunnable runnable, FunctionContext context, Pin pin) {
        Function executeFunction = (Function) context;
        super.executeNext(runnable, executeFunction.getOutContext(), getPinByUid(pin.getUid()));
    }

    @Override
    public PinObject getPinValue(TaskRunnable runnable, FunctionContext context, Pin pin) {
        if (!synced) sync(context);
        if (pin.isOut() && !isError(context)) {
            if (executeFunction == null) executeFunction = new Function(function, this, context);
            function.calculate(runnable, executeFunction, pin);

            FunctionEndAction endAction = executeFunction.getEndAction();
            if (endAction == null) return pin.getValue();
            Pin pinByUid = endAction.getPinByUid(pin.getUid());
            return endAction.getPinValue(runnable, executeFunction, pinByUid);
        }
        return super.getPinValue(runnable, context, pin);
    }

    public void sync(FunctionContext context) {
        function = SaveRepository.getInstance().getFunction(parentId, functionId);
        if (function == null) return;

        ArrayList<Pin> pins = new ArrayList<>(getPins());
        ArrayList<Pin> functionPins = function.getPins();

        // 移除多余的针脚
        for (Pin pin : pins) {
            boolean flag = true;
            for (Pin functionPin : functionPins) {
                if (pin.getUid().equals(functionPin.getUid())) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                removePin(pin, context);
            }
        }

        // 再同步最新的针脚
        for (Pin functionPin : functionPins) {
            Pin pin = getPinByUid(functionPin.getUid());
            // 如果之前没有，直接复制一份加上
            if (pin == null) {
                Pin addPin = addPin((Pin) functionPin.copy());
                addPin.newInfo();
                continue;
            }
            // 如果之前有，但值不对，断开所有连接，并复制值
            if (!functionPin.isSameValueType(pin)) {
                pin.cleanLinks(context);
                pin.setValue(functionPin.getValue().copy());
            }
            pin.setTitle(functionPin.getTitle());
        }
        synced = true;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getFunctionId() {
        return functionId;
    }
}
