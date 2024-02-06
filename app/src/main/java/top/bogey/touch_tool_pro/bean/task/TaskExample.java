package top.bogey.touch_tool_pro.bean.task;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.bean.function.FunctionType;

public class TaskExample extends Task {

    public TaskExample() {
        super(FunctionType.TASK_EXAMPLE);
    }

    public TaskExample(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public void save() {

    }
}
