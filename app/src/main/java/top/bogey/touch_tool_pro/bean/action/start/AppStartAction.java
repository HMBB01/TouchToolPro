package top.bogey.touch_tool_pro.bean.action.start;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinSubType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinApplication;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;
import top.bogey.touch_tool_pro.service.WorldState;

public class AppStartAction extends StartAction {
    private transient Pin appPin = new Pin(new PinApplication(PinSubType.MULTI_ALL_ACTIVITY), R.string.pin_app);
    private transient Pin autoBreakPin = new Pin(new PinBoolean(true), R.string.action_app_start_subtitle_break);
    private transient Pin startAppPin = new Pin(new PinApplication(PinSubType.SINGLE_ACTIVITY), R.string.action_app_start_subtitle_info, true);

    public AppStartAction() {
        super(ActionType.ENTER_APP_START);
        appPin = addPin(appPin);
        autoBreakPin = addPin(autoBreakPin);
        startAppPin = addPin(startAppPin);
    }

    public AppStartAction(JsonObject jsonObject) {
        super(jsonObject);
        appPin = reAddPin(appPin);
        autoBreakPin = reAddPin(autoBreakPin);
        startAppPin = reAddPin(startAppPin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        WorldState state = WorldState.getInstance();
        String packageName = state.getPackageName();
        String activityName = state.getActivityName();

        PinApplication startApp = startAppPin.getValue(PinApplication.class);
        startApp.getApps().clear();
        startApp.getApps().put(packageName, new ArrayList<>(Collections.singleton(activityName)));

        super.execute(runnable, context, pin);
    }

    @Override
    public boolean checkReady(TaskRunnable runnable, FunctionContext context) {
        WorldState state = WorldState.getInstance();
        String packageName = state.getPackageName();
        if (packageName == null) return false;

        String activityName = state.getActivityName();
        MainAccessibilityService service = MainApplication.getInstance().getService();

        PinApplication appValue = (PinApplication) getPinValue(runnable, context, appPin);
        return appValue.contain(service, packageName, activityName);
    }

    @Override
    public boolean checkStop(TaskRunnable runnable, FunctionContext context) {
        if (super.checkStop(runnable, context)) return true;

        if (((PinBoolean) getPinValue(runnable, context, autoBreakPin)).isBool()) {
            return !checkReady(runnable, context);
        }
        return false;
    }

}
