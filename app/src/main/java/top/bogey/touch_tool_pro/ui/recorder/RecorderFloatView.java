package top.bogey.touch_tool_pro.ui.recorder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.color.ExistColorAction;
import top.bogey.touch_tool_pro.bean.action.function.FunctionEndAction;
import top.bogey.touch_tool_pro.bean.action.function.FunctionInnerAction;
import top.bogey.touch_tool_pro.bean.action.function.FunctionStartAction;
import top.bogey.touch_tool_pro.bean.action.image.ExistImageAction;
import top.bogey.touch_tool_pro.bean.action.logic.WaitIfLogicAction;
import top.bogey.touch_tool_pro.bean.action.node.ExistNodeAction;
import top.bogey.touch_tool_pro.bean.action.normal.ClickKeyAction;
import top.bogey.touch_tool_pro.bean.action.normal.ClickNodeAction;
import top.bogey.touch_tool_pro.bean.action.normal.ClickPositionAction;
import top.bogey.touch_tool_pro.bean.action.normal.DelayAction;
import top.bogey.touch_tool_pro.bean.action.normal.LogAction;
import top.bogey.touch_tool_pro.bean.action.normal.NormalAction;
import top.bogey.touch_tool_pro.bean.action.normal.TouchAction;
import top.bogey.touch_tool_pro.bean.action.start.InnerStartAction;
import top.bogey.touch_tool_pro.bean.action.string.ExistTextAction;
import top.bogey.touch_tool_pro.bean.function.Function;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinColor;
import top.bogey.touch_tool_pro.bean.pin.pins.PinImage;
import top.bogey.touch_tool_pro.bean.pin.pins.PinNodePath;
import top.bogey.touch_tool_pro.bean.pin.pins.PinSpinner;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.pin.pins.PinTouch;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArea;
import top.bogey.touch_tool_pro.bean.task.Task;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.bean.task.TaskRunningListener;
import top.bogey.touch_tool_pro.databinding.FloatRecorderBinding;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;
import top.bogey.touch_tool_pro.ui.picker.BasePickerFloatView;
import top.bogey.touch_tool_pro.ui.picker.FloatBaseCallback;
import top.bogey.touch_tool_pro.ui.picker.IPickerCallback;
import top.bogey.touch_tool_pro.ui.picker.ImagePickerFloatPreview;
import top.bogey.touch_tool_pro.ui.picker.NodePickerFloatPreview;
import top.bogey.touch_tool_pro.ui.picker.PickerCallback;
import top.bogey.touch_tool_pro.ui.picker.TextPickerFloatPreview;
import top.bogey.touch_tool_pro.utils.easy_float.EasyFloat;

@SuppressLint("ViewConstructor")
public class RecorderFloatView extends BasePickerFloatView {
    private static final int MAIN_X = 21;
    private static final int STATE_X = 1;
    private static final int START_Y = 1;
    private static final int OFFSET_Y = 20;

    private final FloatRecorderBinding binding;
    private final QuickRecordFloatView quickRecordFloatView;
    private final ArrayList<RecorderStep> steps = new ArrayList<>();
    private final ArrayList<RecorderStep> history = new ArrayList<>();

    private final MainAccessibilityService service;

    private long delayStartTime;
    private long pauseStartTime;
    private boolean recording = false;

    public RecorderFloatView(@NonNull Context context, IPickerCallback pickerCallback, Function function) {
        super(context, pickerCallback);
        quickRecordFloatView = new QuickRecordFloatView(context, this);
        binding = FloatRecorderBinding.inflate(LayoutInflater.from(context), this, true);
        service = MainApplication.getInstance().getService();

        binding.closeButton.setOnClickListener(v -> dismiss());

        binding.saveButton.setOnClickListener(v -> {
            addActionsToFunction(function);
            if (pickerCallback != null) pickerCallback.onComplete();
            dismiss();
        });

        binding.recodeButton.setOnClickListener(v -> {
            if (recording) {
                pauseStartTime = System.currentTimeMillis();
            } else {
                if (pauseStartTime > 0) {
                    // 恢复延迟偏移
                    delayStartTime = System.currentTimeMillis() - pauseStartTime + delayStartTime;
                }
            }
            recording = !recording;
            binding.recodeButton.setChecked(recording);
            showQuickTouch(recording);
        });

        binding.backButton.setOnClickListener(v -> addHistory());

        binding.nextButton.setOnClickListener(v -> removeHistory());

        binding.countText.setText(String.valueOf(steps.size()));

        binding.widgetButton.setOnClickListener(v -> {
            if (isStopRecord()) return;
            PinNodePath nodePath = new PinNodePath();
            new NodePickerFloatPreview(context, new PickerCallback() {
                @Override
                public void onComplete() {
                    RecorderStep recorderStep = new RecorderStep();
                    recorderStep.addTouchNodeAction(getDelay(), nodePath);
                    addStep(recorderStep);
                }
            }, nodePath).show();
        });

        binding.imageButton.setOnClickListener(v -> {
            if (isStopRecord()) return;
            PinImage pinImage = new PinImage();
            new ImagePickerFloatPreview(context, new PickerCallback() {
                @Override
                public void onComplete() {
                    RecorderStep recorderStep = new RecorderStep();
                    recorderStep.addTouchPosAction(getDelay(), pinImage);
                    addStep(recorderStep);
                }
            }, pinImage).show();
        });

        binding.colorButton.setOnClickListener(v -> {
            if (isStopRecord()) return;
            PinColor pinColor = new PinColor();
            new ImagePickerFloatPreview(context, new PickerCallback() {
                @Override
                public void onComplete() {
                    RecorderStep recorderStep = new RecorderStep();
                    recorderStep.addTouchPosAction(getDelay(), pinColor);
                    addStep(recorderStep);
                }
            }, pinColor).show();
        });

        binding.textButton.setOnClickListener(v -> {
            if (isStopRecord()) return;
            PinString pinString = new PinString();
            new TextPickerFloatPreview(context, new PickerCallback() {
                @Override
                public void onComplete() {
                    RecorderStep recorderStep = new RecorderStep();
                    recorderStep.addTouchNodeAction(getDelay(), pinString.getValue());
                    addStep(recorderStep);
                }
            }, pinString).show();
        });

        binding.logButton.setOnClickListener(v -> {
            if (isStopRecord()) return;
            PinString pinString = new PinString();
            new TextPickerFloatPreview(context, new PickerCallback() {
                @Override
                public void onComplete() {
                    RecorderStep recorderStep = new RecorderStep();
                    recorderStep.addLogAction(pinString.getValue());
                    addStep(recorderStep);
                }
            }, pinString).show();
        });

        binding.backKeyButton.setOnClickListener(v -> {
            if (isStopRecord()) return;
            RecorderStep recorderStep = new RecorderStep();
            recorderStep.addBackAction(getDelay());
            addStep(recorderStep);
        });
    }

    private static void addLink(Pin pin1, Pin pin2) {
        pin1.addLink(pin2);
        pin2.addLink(pin1);
    }

    private int getDelay() {
        if (delayStartTime == 0) return 0;
        return (int) (System.currentTimeMillis() - delayStartTime);
    }

    public void showQuickTouch(boolean show) {
        if (show && recording) {
            EasyFloat.show(quickRecordFloatView.getTag());
        } else {
            EasyFloat.hide(quickRecordFloatView.getTag());
        }
    }

    private boolean isStopRecord() {
        if (recording) return false;
        Toast.makeText(getContext(), R.string.function_record_tips, Toast.LENGTH_SHORT).show();
        return true;
    }

    public void addTouchStep(PinTouch path) {
        if (isStopRecord()) return;

        RecorderStep recorderStep = new RecorderStep();
        recorderStep.addTouchPathAction(getDelay(), path);
        addStep(recorderStep);
    }

    private void addStep(RecorderStep step) {
        steps.add(step);
        binding.backButton.setVisibility(VISIBLE);
        binding.countText.setText(String.valueOf(steps.size()));

        history.clear();
        binding.nextButton.setVisibility(GONE);
        runRecorderStep(step);
    }

    private void runRecorderStep(RecorderStep step) {
        if (service != null && service.isServiceEnabled()) {
            Task task = new Task();
            InnerStartAction innerStartAction = null;
            for (Action action : step.getActions()) {
                if (action instanceof LogAction) continue;
                if (action instanceof NormalAction normalAction) {
                    HashMap<String, String> links = normalAction.getInPin().getLinks();
                    if (links.isEmpty()) {
                        innerStartAction = new InnerStartAction(normalAction.getOutPin());
                    }
                }
                task.addAction(action);
            }
            if (innerStartAction == null) {
                delayStartTime = System.currentTimeMillis();
                return;
            }

            task.addAction(innerStartAction);

            EasyFloat.hide(tag);
            InnerStartAction finalInnerStartAction = innerStartAction;
            postDelayed(() -> service.runTask(task, finalInnerStartAction, task, new TaskRunningListener() {
                @Override
                public void onStart(TaskRunnable runnable) {

                }

                @Override
                public void onEnd(TaskRunnable runnable) {
                    delayStartTime = System.currentTimeMillis();
                    post(() -> EasyFloat.show(tag));
                }

                @Override
                public void onProgress(TaskRunnable runnable, Action action, int progress, boolean execute) {

                }
            }), 100);
        }
    }

    private void addHistory() {
        RecorderStep step = steps.remove(steps.size() - 1);
        history.add(step);
        binding.nextButton.setVisibility(VISIBLE);
        binding.backButton.setVisibility(steps.isEmpty() ? GONE : VISIBLE);
        binding.countText.setText(String.valueOf(steps.size()));
        delayStartTime = System.currentTimeMillis();
    }

    private void removeHistory() {
        RecorderStep step = history.remove(history.size() - 1);
        steps.add(step);
        binding.backButton.setVisibility(VISIBLE);
        binding.countText.setText(String.valueOf(steps.size()));
        binding.nextButton.setVisibility(history.isEmpty() ? GONE : VISIBLE);
        delayStartTime = System.currentTimeMillis();
    }

    private void addActionsToFunction(Function function) {
        if (steps.isEmpty()) return;

        FunctionInnerAction start = null, end = null;
        for (Action action : function.getActionsByClass(FunctionInnerAction.class)) {
            if (action instanceof FunctionStartAction) {
                start = (FunctionInnerAction) action;
            } else if (action instanceof FunctionEndAction) {
                end = (FunctionInnerAction) action;
            }
        }
        if (start == null || end == null) return;

        int stepIndex = setActionPosition(start, 0);

        Iterator<RecorderStep> iterator = steps.iterator();
        RecorderStep step = iterator.next();
        // 首个录制的动作移除延迟
        step.detachDelayAction();
        addLink(step.getInPin(), start.getDefaultExecutePin());
        stepIndex = setStepPosition(function, step, stepIndex);

        while (iterator.hasNext()) {
            RecorderStep nextStep = iterator.next();
            addLink(step.getOutPin(), nextStep.getInPin());
            stepIndex = setStepPosition(function, nextStep, stepIndex);
            step = nextStep;
        }

        addLink(step.getOutPin(), end.getDefaultExecutePin());
        setActionPosition(end, stepIndex);
    }

    private int setStepPosition(Function function, RecorderStep step, int index) {
        for (Action action : step.getActions()) {
            index = setActionPosition(action, index);
            action.setExpand(false);
            function.addAction(action);
        }
        return index;
    }

    private int setActionPosition(Action action, int index) {
        action.setY(START_Y + index * OFFSET_Y);
        if (action instanceof NormalAction || action instanceof FunctionInnerAction) {
            action.setX(MAIN_X);
            index++;
        } else {
            action.setX(STATE_X);
            action.setY(action.getY() + 3);
        }
        return index;
    }

    @Override
    public void show() {
        if (EasyFloat.getView(tag) != null) {
            EasyFloat.show(tag);
            return;
        }

        quickRecordFloatView.show();
        EasyFloat.with(MainApplication.getInstance().getService())
                .setLayout(this)
                .setTag(tag)
                .setDragEnable(true)
                .setCallback(new RecorderFloatCallback())
                .setAnimator(null)
                .show();
    }

    private static class RecorderStep {
        private final ArrayList<Action> actions = new ArrayList<>();
        private Pin inPin;
        private Pin outPin;

        private void attachDelayAction(int delay) {
            if (delay == 0) return;
            DelayAction delayAction = new DelayAction();

            Pin delayPin = delayAction.getDelayPin();
            delayPin.getValue(PinValueArea.class).setLow(delay);
            delayPin.getValue(PinValueArea.class).setHigh(delay);

            addLink(delayAction.getOutPin(), inPin);
            inPin = delayAction.getInPin();

            actions.add(0, delayAction);
        }

        public void detachDelayAction() {
            for (Action action : actions) {
                if (action instanceof DelayAction) {
                    Pin delayOutPin = ((DelayAction) action).getOutPin();
                    inPin = delayOutPin.getLinkedPin(actions);
                    if (inPin != null) {
                        actions.remove(action);
                        return;
                    }
                }
            }
        }

        public void addTouchPathAction(int delay, PinTouch path) {
            TouchAction touchAction = new TouchAction();
            inPin = touchAction.getInPin();
            outPin = touchAction.getOutPin();

            Pin touchPin = touchAction.getTouchPin();
            touchPin.setValue(path);

            actions.add(touchAction);
            attachDelayAction(delay);
        }

        public void addTouchNodeAction(int delay, String text) {
            ExistTextAction existTextAction = new ExistTextAction();
            existTextAction.getTextPin().getValue(PinString.class).setValue(text);

            WaitIfLogicAction waitIfLogicAction = new WaitIfLogicAction();
            inPin = waitIfLogicAction.getInPin();

            ClickNodeAction clickNodeAction = new ClickNodeAction();
            outPin = clickNodeAction.getOutPin();

            addLink(existTextAction.getResultPin(), waitIfLogicAction.getConditionPin());
            addLink(existTextAction.getNodePin(), clickNodeAction.getNodePin());
            addLink(waitIfLogicAction.getOutPin(), clickNodeAction.getInPin());

            actions.add(existTextAction);
            actions.add(waitIfLogicAction);
            actions.add(clickNodeAction);

            attachDelayAction(delay);
        }

        public void addTouchNodeAction(int delay, PinNodePath nodePath) {
            ExistNodeAction existNodeAction = new ExistNodeAction();
            existNodeAction.getPathPin().setValue(nodePath);

            WaitIfLogicAction waitIfLogicAction = new WaitIfLogicAction();
            inPin = waitIfLogicAction.getInPin();

            ClickNodeAction clickNodeAction = new ClickNodeAction();
            outPin = clickNodeAction.getOutPin();

            addLink(existNodeAction.getResultPin(), waitIfLogicAction.getConditionPin());
            addLink(existNodeAction.getNodePin(), clickNodeAction.getNodePin());
            addLink(waitIfLogicAction.getOutPin(), clickNodeAction.getInPin());

            actions.add(existNodeAction);
            actions.add(waitIfLogicAction);
            actions.add(clickNodeAction);

            attachDelayAction(delay);
        }

        public void addTouchPosAction(int delay, PinImage image) {
            ExistImageAction existImageAction = new ExistImageAction();
            existImageAction.getImagePin().setValue(image);

            WaitIfLogicAction waitIfLogicAction = new WaitIfLogicAction();
            inPin = waitIfLogicAction.getInPin();

            ClickPositionAction clickPositionAction = new ClickPositionAction();
            outPin = clickPositionAction.getOutPin();

            addLink(existImageAction.getResultPin(), waitIfLogicAction.getConditionPin());
            addLink(existImageAction.getPosPin(), clickPositionAction.getPosPin());
            addLink(waitIfLogicAction.getOutPin(), clickPositionAction.getInPin());

            actions.add(existImageAction);
            actions.add(waitIfLogicAction);
            actions.add(clickPositionAction);

            attachDelayAction(delay);
        }

        public void addTouchPosAction(int delay, PinColor color) {
            ExistColorAction existColorAction = new ExistColorAction();
            existColorAction.getColorPin().setValue(color);

            WaitIfLogicAction waitIfLogicAction = new WaitIfLogicAction();
            inPin = waitIfLogicAction.getInPin();

            ClickPositionAction clickPositionAction = new ClickPositionAction();
            outPin = clickPositionAction.getOutPin();

            addLink(existColorAction.getResultPin(), waitIfLogicAction.getConditionPin());
            addLink(existColorAction.getPosPin(), clickPositionAction.getPosPin());
            addLink(waitIfLogicAction.getOutPin(), clickPositionAction.getInPin());

            actions.add(existColorAction);
            actions.add(waitIfLogicAction);
            actions.add(clickPositionAction);

            attachDelayAction(delay);
        }

        public void addLogAction(String log) {
            LogAction logAction = new LogAction();
            inPin = logAction.getInPin();
            outPin = logAction.getOutPin();
            logAction.getLogPin().getValue(PinString.class).setValue(log);

            actions.add(logAction);
        }

        public void addBackAction(int delay) {
            ClickKeyAction clickKeyAction = new ClickKeyAction();
            inPin = clickKeyAction.getInPin();
            outPin = clickKeyAction.getOutPin();
            clickKeyAction.getKeyPin().getValue(PinSpinner.class).setIndex(0);

            actions.add(clickKeyAction);
            attachDelayAction(delay);
        }

        public ArrayList<Action> getActions() {
            return actions;
        }

        public Pin getInPin() {
            return inPin;
        }

        public Pin getOutPin() {
            return outPin;
        }
    }

    private class RecorderFloatCallback extends FloatBaseCallback {

        @Override
        public void onShow(String tag) {
            super.onShow(tag);
            showQuickTouch(true);
        }

        @Override
        public void onHide() {
            super.onHide();
            showQuickTouch(false);
        }

        @Override
        public void onDismiss() {
            quickRecordFloatView.dismiss();
            super.onDismiss();
        }
    }
}
