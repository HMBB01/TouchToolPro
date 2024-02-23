package top.bogey.touch_tool_pro.bean.action.other;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionCheckResult;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.service.WorldState;
import top.bogey.touch_tool_pro.utils.SettingSave;

public class BluetoothStateAction extends Action {
    private transient Pin statePin = new Pin(new PinBoolean(), R.string.action_bluetooth_start_subtitle_state, true);
    private transient Pin devicePin = new Pin(new PinString(), R.string.action_bluetooth_start_subtitle_devices);

    public BluetoothStateAction() {
        super(ActionType.BLUETOOTH_STATE);
        devicePin = addPin(devicePin);
        statePin = addPin(statePin);
    }

    public BluetoothStateAction(JsonObject jsonObject) {
        super(jsonObject);
        devicePin = reAddPin(devicePin);
        statePin = reAddPin(statePin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinString name = (PinString) getPinValue(runnable, context, devicePin);
        if (name.getValue() == null) return;
        boolean existed = WorldState.getInstance().existBluetoothDevice(name.getValue());
        statePin.getValue(PinBoolean.class).setBool(existed);
    }

    @Override
    public ActionCheckResult check(FunctionContext context) {
        if (!SettingSave.getInstance().getUseBluetooth(MainApplication.getInstance())) {
            return new ActionCheckResult(ActionCheckResult.ActionResultType.ERROR, R.string.error_no_bluetooth_permission);
        }
        return super.check(context);
    }
}
