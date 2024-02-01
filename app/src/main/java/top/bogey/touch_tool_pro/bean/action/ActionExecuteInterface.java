package top.bogey.touch_tool_pro.bean.action;

import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinObject;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public interface ActionExecuteInterface {
    void execute(TaskRunnable runnable, FunctionContext context, Pin pin);

    void executeNext(TaskRunnable runnable, FunctionContext context, Pin pin);

    void calculate(TaskRunnable runnable, FunctionContext context, Pin pin);

    void resetReturnValue(Pin pin);

    PinObject getPinValue(TaskRunnable runnable, FunctionContext context, Pin pin);
}
