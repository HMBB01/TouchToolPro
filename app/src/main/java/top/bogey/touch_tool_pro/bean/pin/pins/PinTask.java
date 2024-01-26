package top.bogey.touch_tool_pro.bean.pin.pins;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.Objects;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.start.StartAction;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.bean.task.Task;
import top.bogey.touch_tool_pro.save.SaveRepository;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class PinTask extends PinValue {
    private String taskId;
    private String startId;

    public PinTask() {
        super(PinType.TASK);
    }

    public PinTask(String taskId, String startId) {
        this();
        this.taskId = taskId;
        this.startId = startId;
    }

    public PinTask(JsonObject jsonObject) {
        super(jsonObject);
        taskId = GsonUtils.getAsString(jsonObject, "taskId", null);
        startId = GsonUtils.getAsString(jsonObject, "startId", null);
    }

    @Override
    public void newInfo() {
        taskId = null;
        startId = null;
    }

    @NonNull
    @Override
    public String toString() {
        Task task = getTask();
        StartAction startAction = getStartAction();
        if (task == null || startAction == null) return "";
        return task.getTitle() + "-" + startAction.getTitle();
    }

    @Override
    public int getPinColor(Context context) {
        return context.getColor(R.color.TaskPinColor);
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Task getTask() {
        if (taskId == null) return null;
        return SaveRepository.getInstance().getTaskById(taskId);
    }

    public String getStartId() {
        return startId;
    }

    public void setStartId(String startId) {
        this.startId = startId;
    }

    public StartAction getStartAction() {
        if (startId == null) return null;
        Task task = getTask();
        if (task == null) return null;
        return (StartAction) task.getActionById(startId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PinTask pinTask = (PinTask) o;

        if (!Objects.equals(taskId, pinTask.taskId)) return false;
        return Objects.equals(startId, pinTask.startId);
    }

    @Override
    public int hashCode() {
        int result = taskId != null ? taskId.hashCode() : 0;
        result = 31 * result + (startId != null ? startId.hashCode() : 0);
        return result;
    }
}
