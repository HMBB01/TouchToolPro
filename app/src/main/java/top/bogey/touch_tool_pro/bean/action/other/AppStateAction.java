package top.bogey.touch_tool_pro.bean.action.other;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinApplication;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.service.WorldState;

public class AppStateAction extends Action {
    private transient Pin appPin = new Pin(new PinApplication(), R.string.action_app_state_subtitle_where, true);

    public AppStateAction() {
        super(ActionType.APP_STATE);
        appPin = addPin(appPin);
    }

    public AppStateAction(JsonObject jsonObject) {
        super(jsonObject);
        appPin = reAddPin(appPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        WorldState state = WorldState.getInstance();
        String packageName = state.getPackageName();
        String activityName = state.getActivityName();

        PinApplication app = appPin.getValue(PinApplication.class);
        app.getApps().put(packageName, new ArrayList<>(Collections.singleton(activityName)));
    }
}
