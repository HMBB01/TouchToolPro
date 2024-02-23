package top.bogey.touch_tool_pro.bean.action.start;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionCheckResult;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinSubType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinLong;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.utils.AppUtils;
import top.bogey.touch_tool_pro.utils.SettingSave;

public class TimeStartAction extends StartAction {
    private transient Pin datePin = new Pin(new PinLong(PinSubType.DATE, System.currentTimeMillis()), R.string.action_time_start_subtitle_date);
    private transient Pin timePin = new Pin(new PinLong(PinSubType.TIME, System.currentTimeMillis()), R.string.action_time_start_subtitle_time);
    private transient Pin periodicPin = new Pin(new PinLong(PinSubType.PERIODIC, 0L), R.string.action_time_start_subtitle_periodic);

    public TimeStartAction() {
        super(ActionType.TIME_START);
        datePin = addPin(datePin);
        timePin = addPin(timePin);
        periodicPin = addPin(periodicPin);
    }

    public TimeStartAction(JsonObject jsonObject) {
        super(jsonObject);
        datePin = reAddPin(datePin);
        timePin = reAddPin(timePin);
        periodicPin = reAddPin(periodicPin);
    }

    @Override
    public boolean checkReady(TaskRunnable runnable, FunctionContext context) {
        return true;
    }

    public long getStartTime() {
        long date = ((PinLong) datePin.getValue()).getValue();
        long time = ((PinLong) timePin.getValue()).getValue();
        return AppUtils.mergeDateTime(date, time);
    }

    public long getPeriodic() {
        return ((PinLong) periodicPin.getValue()).getValue();
    }

    @Override
    public ActionCheckResult check(FunctionContext context) {
        if (!SettingSave.getInstance().isUseExactAlarm()) {
            return new ActionCheckResult(ActionCheckResult.ActionResultType.ERROR, R.string.error_no_alarm_permission);
        }
        return super.check(context);
    }
}
