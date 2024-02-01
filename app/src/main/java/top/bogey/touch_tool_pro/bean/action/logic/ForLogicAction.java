package top.bogey.touch_tool_pro.bean.action.logic;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.normal.NormalAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinExecute;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class ForLogicAction extends NormalAction {
    private transient Pin completePin = new Pin(new PinExecute(), R.string.action_logic_subtitle_complete, true);
    private transient Pin currPin = new Pin(new PinInteger(), R.string.action_for_loop_logic_subtitle_curr, true);
    private transient Pin breakPin = new Pin(new PinExecute(), R.string.action_for_loop_logic_subtitle_break);
    private transient Pin startPin = new Pin(new PinInteger(1), R.string.action_for_loop_logic_subtitle_start);
    private transient Pin endPin = new Pin(new PinInteger(5), R.string.action_for_loop_logic_subtitle_end);
    private transient boolean needBreak;

    public ForLogicAction() {
        super(ActionType.LOGIC_FOR);
        completePin = addPin(completePin);
        currPin = addPin(currPin);
        breakPin = addPin(breakPin);
        startPin = addPin(startPin);
        endPin = addPin(endPin);
        needBreak = false;
    }

    public ForLogicAction(JsonObject jsonObject) {
        super(jsonObject);
        completePin = reAddPin(completePin);
        currPin = reAddPin(currPin);
        breakPin = reAddPin(breakPin);
        startPin = reAddPin(startPin);
        endPin = reAddPin(endPin);
        needBreak = false;
    }

    @Override
    public void resetReturnValue(Pin pin) {

    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        if (pin.equals(inPin)) {
            needBreak = false;
            PinInteger start = (PinInteger) getPinValue(runnable, context, startPin);
            PinInteger end = (PinInteger) getPinValue(runnable, context, endPin);
            for (int i = start.getValue(); i <= end.getValue(); i++) {
                if (runnable.isInterrupt() || context.isEnd()) return;
                if (needBreak) break;
                currPin.getValue(PinInteger.class).setValue(i);
                executeNext(runnable, context, outPin);
            }
            executeNext(runnable, context, completePin);
        } else {
            needBreak = true;
        }
    }
}
