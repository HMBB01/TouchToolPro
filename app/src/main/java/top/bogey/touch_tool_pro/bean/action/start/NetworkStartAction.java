package top.bogey.touch_tool_pro.bean.action.start;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.other.NetworkStateAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinSpinner;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.service.WorldState;

public class NetworkStartAction extends StartAction {
    private transient Pin statePin = new Pin(new PinSpinner(R.array.network_state), R.string.action_network_start_subtitle_network, true);

    public NetworkStartAction() {
        super(ActionType.NETWORK_START);
        statePin = addPin(statePin);
    }

    public NetworkStartAction(JsonObject jsonObject) {
        super(jsonObject);
        statePin = reAddPin(statePin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        int networkType = WorldState.getInstance().getNetworkType();
        statePin.getValue(PinSpinner.class).setIndex(NetworkStateAction.networkTypeToIndex(networkType));
        super.execute(runnable, context, pin);
    }

    @Override
    public boolean checkReady(TaskRunnable runnable, FunctionContext context) {
        return true;
    }
}
