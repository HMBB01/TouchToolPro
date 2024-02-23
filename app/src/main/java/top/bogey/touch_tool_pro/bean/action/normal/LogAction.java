package top.bogey.touch_tool_pro.bean.action.normal;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.save.SaveRepository;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;

public class LogAction extends NormalAction {
    private transient Pin logPin = new Pin(new PinString(), R.string.action_log_action_subtitle_log);
    private transient Pin logValuePin = new Pin(new PinValue(), R.string.pin_value);
    private transient Pin toastPin = new Pin(new PinBoolean(true), R.string.action_log_action_subtitle_toast, false, false, true);
    private transient Pin savePin = new Pin(new PinBoolean(true), R.string.action_log_action_subtitle_save, false, false, true);

    public LogAction() {
        super(ActionType.LOG);
        logPin = addPin(logPin);
        logValuePin = addPin(logValuePin);
        toastPin = addPin(toastPin);
        savePin = addPin(savePin);
    }

    public LogAction(JsonObject jsonObject) {
        super(jsonObject);
        logPin = reAddPin(logPin);
        logValuePin = reAddPin(logValuePin);
        toastPin = reAddPin(toastPin);
        savePin = reAddPin(savePin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinString log = (PinString) getPinValue(runnable, context, logPin);
        PinValue logValue = (PinValue) getPinValue(runnable, context, logValuePin);
        String logString = log.getValue() == null ? "" : log.getValue();
        if (logString.isEmpty() && logValue != null) logString = logValue.toString();

        PinBoolean save = (PinBoolean) getPinValue(runnable, context, savePin);
        if (save.isBool()) {
            SaveRepository.getInstance().addLog(runnable.getTask().getId(), runnable.getStartAction().getFullDescription() + ":" + logString);
        }
        PinBoolean toast = (PinBoolean) getPinValue(runnable, context, toastPin);
        if (toast.isBool()) {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            service.showToast(logString);
        }

        executeNext(runnable, context, outPin);
    }

    public Pin getLogPin() {
        return logPin;
    }
}
