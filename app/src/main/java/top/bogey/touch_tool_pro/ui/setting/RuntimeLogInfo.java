package top.bogey.touch_tool_pro.ui.setting;

import java.util.HashMap;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.base.LogInfo;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinObject;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;

public class RuntimeLogInfo extends LogInfo {
    private final int x;
    private final int y;
    private final String actionId;
    private final HashMap<String, PinValue> values;
    private final boolean execute;

    public RuntimeLogInfo(int index, String log, Action action, boolean execute) {
        super(index, log);
        x = action.getX();
        y = action.getY();
        actionId = action.getId();
        values = new HashMap<>();
        for (Pin pin : action.getPins()) {
            PinObject value = pin.getValue();
            if (value instanceof PinValue) {
                values.put(pin.getId(), (PinValue) value.copy());
            }
        }
        this.execute = execute;
    }

    @Override
    public String getLogString() {
        return "【" + getIndex() + "】 " + getTimeString() + "\n" + getExecuteTypeString() + "-" + getLog() + "(" + x + "," + y + ")";
    }

    private String getExecuteTypeString() {
        return MainApplication.getInstance().getString(execute ? R.string.pin_execute : R.string.pin_value);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getActionId() {
        return actionId;
    }

    public HashMap<String, PinValue> getValues() {
        return values;
    }
}
