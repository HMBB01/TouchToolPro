package top.bogey.touch_tool_pro.bean.action.normal;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionCheckResult;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinSubType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinExecute;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.utils.super_user.CmdResult;
import top.bogey.touch_tool_pro.utils.super_user.SuperUser;

public class ShellAction extends NormalAction {
    private transient Pin cmdPin = new Pin(new PinString(PinSubType.MULTI_LINE), R.string.action_shell_action_subtitle_cmd);
    private transient Pin falsePin = new Pin(new PinExecute(), R.string.action_logic_subtitle_false, true);
    private transient Pin outValuePin = new Pin(new PinString(), R.string.action_check_subtitle_result, true);

    public ShellAction() {
        super(ActionType.SHELL);
        cmdPin = addPin(cmdPin);
        falsePin = addPin(falsePin);
        outValuePin = addPin(outValuePin);
    }

    public ShellAction(JsonObject jsonObject) {
        super(jsonObject);
        cmdPin = reAddPin(cmdPin);
        falsePin = reAddPin(falsePin);
        outValuePin = reAddPin(outValuePin);
    }

    @Override
    public void resetReturnValue(Pin pin) {

    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        if (SuperUser.isSuperUser()) {
            PinString cmd = (PinString) getPinValue(runnable, context, cmdPin);
            CmdResult cmdResult = SuperUser.runCommand(cmd.getValue());
            if (cmdResult != null) {
                outValuePin.getValue(PinString.class).setValue(cmdResult.info);
                if (cmdResult.result) {
                    executeNext(runnable, context, outPin);
                    return;
                }
            }
        }
        executeNext(runnable, context, falsePin);
    }

    @Override
    public ActionCheckResult check(FunctionContext context) {
        if (!SuperUser.isSuperUser()) {
            return new ActionCheckResult(ActionCheckResult.ActionResultType.ERROR, R.string.error_super_user_no_permission);
        }
        return super.check(context);
    }
}
