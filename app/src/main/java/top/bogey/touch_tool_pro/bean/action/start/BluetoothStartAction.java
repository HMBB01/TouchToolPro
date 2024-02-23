package top.bogey.touch_tool_pro.bean.action.start;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionCheckResult;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.service.WorldState;
import top.bogey.touch_tool_pro.utils.SettingSave;

public class BluetoothStartAction extends StartAction {
    private transient Pin devicePin = new Pin(new PinString(), R.string.action_bluetooth_start_subtitle_devices, true);
    private transient Pin statePin = new Pin(new PinBoolean(), R.string.action_bluetooth_start_subtitle_state, true);

    public BluetoothStartAction() {
        super(ActionType.BLUETOOTH_START);
        devicePin = addPin(devicePin);
        statePin = addPin(statePin);
    }

    public BluetoothStartAction(JsonObject jsonObject) {
        super(jsonObject);
        devicePin = reAddPin(devicePin);
        statePin = reAddPin(statePin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        String name = WorldState.getInstance().getBluetoothName();
        boolean newBluetoothDevice = WorldState.getInstance().isNewBluetoothDevice();
        devicePin.getValue(PinString.class).setValue(name);
        statePin.getValue(PinBoolean.class).setBool(newBluetoothDevice);
        super.execute(runnable, context, pin);
    }

    @Override
    public boolean checkReady(TaskRunnable runnable, FunctionContext context) {
        return true;
    }

    @Override
    public ActionCheckResult check(FunctionContext context) {
        if (!SettingSave.getInstance().getUseBluetooth(MainApplication.getInstance())) {
            return new ActionCheckResult(ActionCheckResult.ActionResultType.ERROR, R.string.error_no_bluetooth_permission);
        }
        return super.check(context);
    }
}
