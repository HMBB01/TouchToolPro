package top.bogey.touch_tool_pro.bean.function;

import top.bogey.touch_tool_pro.bean.task.Task;
import top.bogey.touch_tool_pro.bean.task.TaskExample;

public enum FunctionType {
    FUNCTION,
    TASK,
    TASK_EXAMPLE;

    public Class<? extends FunctionContext> getFunctionClass() {
        return switch (this) {
            case FUNCTION -> Function.class;
            case TASK -> Task.class;
            case TASK_EXAMPLE -> TaskExample.class;
        };
    }
}
