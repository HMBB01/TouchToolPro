package top.bogey.touch_tool_pro.bean.task;

import top.bogey.touch_tool_pro.bean.action.Action;

public interface TaskRunningListener {
    void onStart(TaskRunnable runnable);

    void onEnd(TaskRunnable runnable);

    void onProgress(TaskRunnable runnable, Action action, int progress, boolean execute);
}
