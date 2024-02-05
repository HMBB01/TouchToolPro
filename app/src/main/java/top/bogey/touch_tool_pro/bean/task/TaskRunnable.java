package top.bogey.touch_tool_pro.bean.task;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.start.StartAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;

public class TaskRunnable implements Runnable {
    private final Task task;
    private final StartAction startAction;
    private final HashSet<TaskRunningListener> listeners = new HashSet<>();
    private FunctionContext context;
    private int progress = 0;
    private boolean interrupt = false;
    private Boolean paused = null;

    private Future<?> future;
    private final HashMap<String, Object> runtimeEnv = new HashMap<>();

    public TaskRunnable(Task task, FunctionContext context, StartAction startAction) {
        this.task = task;
        this.context = context;
        this.startAction = startAction;
    }

    @Override
    public void run() {
        try {
            // 优先使用context中的动作，因为context执行环境用完就扔，任务的变量可以随便保存
            StartAction action = (StartAction) context.getActionById(startAction.getId());
            if (action == null) action = startAction;
            if (!action.checkReady(this, context)) return;
            listeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onStart(this));
            action.execute(this, context, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        listeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onEnd(this));
        context = null;
        interrupt = true;
    }

    public void addProgress(Action action, boolean execute) {
        progress++;
        listeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onProgress(this, action, progress, execute));
        checkStop();
    }

    public Bitmap getCurrImage(MainAccessibilityService service) {
        AtomicReference<Bitmap> bitmapReference = new AtomicReference<>();
        service.getCurrImage(bitmap -> {
            bitmapReference.set(bitmap);
            resume();
        });
        pause();
        return bitmapReference.get();
    }

    public void checkStop() {
        if (startAction.checkStop(this, context)) {
            stop();
        }
    }

    public void stop() {
        if (future != null) future.cancel(true);
        interrupt = true;
    }

    public void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ignored) {
        }
    }

    public void pause() {
        pause(60000);
    }

    public synchronized void pause(long ms) {
        if (paused != null && !paused) paused = null;
        else {
            try {
                paused = true;
                wait(ms);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public synchronized void resume() {
        notifyAll();
        if (paused != null && paused) paused = null;
        else paused = false;
    }

    public boolean isInterrupt() {
        return interrupt;
    }

    public void addListener(TaskRunningListener listener) {
        listeners.add(listener);
    }

    public Task getTask() {
        return task;
    }

    public StartAction getStartAction() {
        return startAction;
    }

    public void setFuture(Future<?> future) {
        this.future = future;
    }

    public void addRuntimeEnv(String key, Object value) {
        runtimeEnv.put(key, value);
    }

    public Object getRuntimeEnv(String key) {
        return runtimeEnv.get(key);
    }
}
