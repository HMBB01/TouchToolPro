package top.bogey.touch_tool.data.action.start;

import android.os.Parcel;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.pin.Pin;
import top.bogey.touch_tool.data.action.pin.PinSubType;
import top.bogey.touch_tool.data.action.pin.object.PinLong;
import top.bogey.touch_tool.data.action.pin.object.PinObject;
import top.bogey.touch_tool.utils.AppUtils;

public class TimeStartAction extends StartAction {
    private final Pin<? extends PinObject> datePin;
    private final Pin<? extends PinObject> timePin;
    private final Pin<? extends PinObject> periodicPin;

    public TimeStartAction() {
        super();
        long timeMillis = System.currentTimeMillis();
        datePin = addPin(new Pin<>(new PinLong(timeMillis), R.string.time_condition_start_date, PinSubType.DATE));
        timePin = addPin(new Pin<>(new PinLong(timeMillis), R.string.time_condition_start_time, PinSubType.TIME));
        periodicPin = addPin(new Pin<>(new PinLong(0), R.string.time_condition_periodic, PinSubType.PERIODIC));
        addPin(restartPin);
        titleId = R.string.task_type_time;
    }

    public TimeStartAction(Parcel in) {
        super(in);
        datePin = addPin(pinsTmp.remove(0));
        timePin = addPin(pinsTmp.remove(0));
        periodicPin = addPin(pinsTmp.remove(0));
        restartPin = addPin(pinsTmp.remove(0));
        titleId = R.string.task_type_time;
    }

    @Override
    public boolean checkReady(WorldState worldState, Task task) {
        long date = ((PinLong) datePin.getValue()).getValue();
        long time = ((PinLong) timePin.getValue()).getValue();
        long periodic = ((PinLong) periodicPin.getValue()).getValue();
        if (periodic > 0) {
            return true;
        } else {
            return AppUtils.mergeDateTime(date, time) > System.currentTimeMillis();
        }
    }
}
