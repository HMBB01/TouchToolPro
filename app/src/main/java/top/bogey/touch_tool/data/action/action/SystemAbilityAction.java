package top.bogey.touch_tool.data.action.action;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.os.Build;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinSpinner;

public class SystemAbilityAction extends NormalAction {
    private transient final Pin abilityPin;

    public SystemAbilityAction(Context context) {
        super(context, R.string.action_system_ability_action_title);
        abilityPin = addPin(new Pin(new PinSpinner(context.getResources().getStringArray(R.array.system_ability))));
    }

    public SystemAbilityAction(JsonObject jsonObject) {
        super(jsonObject);
        abilityPin = addPin(tmpPins.remove(0));
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinSpinner ability = (PinSpinner) getPinValue(runnable, actionContext, abilityPin);
        MainAccessibilityService service = MainApplication.getInstance().getService();
        switch (ability.getIndex()) {
            case 0:
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                break;
            case 1:
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
                break;
            case 2:
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
                break;
            case 3:
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
                break;
            case 4:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT);
                } else {
                    service.showToast(service.getString(R.string.device_not_support_snap));
                }
                break;
        }
        doNextAction(runnable, actionContext, outPin);
    }
}
