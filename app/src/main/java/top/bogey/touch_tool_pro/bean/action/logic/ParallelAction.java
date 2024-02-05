package top.bogey.touch_tool_pro.bean.action.logic;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionMorePinInterface;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.normal.NormalAction;
import top.bogey.touch_tool_pro.bean.action.start.InnerStartAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinAdd;
import top.bogey.touch_tool_pro.bean.pin.pins.PinExecute;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.bean.task.TaskRunningListener;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;

public class ParallelAction extends NormalAction implements ActionMorePinInterface {
    private final transient Pin morePin = new Pin(new PinExecute(), R.string.pin_execute, true);
    protected transient Pin countPin = new Pin(new PinInteger(1), R.string.action_parallel_logic_subtitle_condition);
    protected transient Pin timeoutPin = new Pin(new PinInteger(5000), R.string.action_parallel_logic_subtitle_timeout);
    private transient Pin secondPin = new Pin(new PinExecute(), R.string.pin_execute, true);
    private transient Pin addPin = new Pin(new PinAdd(morePin, 3), R.string.action_subtitle_add_execute, true);

    private transient Pin completePin = new Pin(new PinExecute(), R.string.action_logic_subtitle_complete, true);
    private transient Pin falsePin = new Pin(new PinExecute(), R.string.action_logic_subtitle_false, true);


    public ParallelAction() {
        super(ActionType.LOGIC_PARALLEL);
        countPin = addPin(countPin);
        timeoutPin = addPin(timeoutPin);
        secondPin = addPin(secondPin);
        addPin = addPin(addPin);
        completePin = addPin(completePin);
        falsePin = addPin(falsePin);
    }

    public ParallelAction(JsonObject jsonObject) {
        super(jsonObject);
        countPin = reAddPin(countPin);
        timeoutPin = reAddPin(timeoutPin);
        secondPin = reAddPin(secondPin);
        reAddPin(morePin, 3);
        addPin = reAddPin(addPin);
        completePin = reAddPin(completePin);
        falsePin = reAddPin(falsePin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinInteger count = (PinInteger) getPinValue(runnable, context, countPin);
        PinInteger timeout = (PinInteger) getPinValue(runnable, context, timeoutPin);

        MainAccessibilityService service = MainApplication.getInstance().getService();
        CountDownLatch latch = new CountDownLatch(count.getValue() > 0 ? count.getValue() : 1);
        ArrayList<TaskRunnable> runnableList = new ArrayList<>();
        for (Pin executePin : calculateMorePins()) {
            TaskRunnable taskRunnable = service.runTask(runnable.getTask(), new InnerStartAction(executePin), context, new TaskRunningListener() {
                @Override
                public void onStart(TaskRunnable run) {

                }

                @Override
                public void onEnd(TaskRunnable run) {
                    latch.countDown();
                }

                @Override
                public void onProgress(TaskRunnable run, Action action, int progress, boolean execute) {
                    if (runnable.isInterrupt()) run.stop();
                }
            });
            runnableList.add(taskRunnable);
        }
        try {
            boolean result = latch.await(timeout.getValue(), TimeUnit.MILLISECONDS);
            runnableList.forEach(TaskRunnable::stop);
            if (result) {
                executeNext(runnable, context, completePin);
            } else {
                executeNext(runnable, context, falsePin);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<Pin> calculateMorePins() {
        ArrayList<Pin> pins = new ArrayList<>();
        pins.add(outPin);
        boolean start = false;
        for (Pin pin : getPins()) {
            if (pin == secondPin) start = true;
            if (pin == addPin) start = false;
            if (start) pins.add(pin);
        }
        return pins;
    }
}
