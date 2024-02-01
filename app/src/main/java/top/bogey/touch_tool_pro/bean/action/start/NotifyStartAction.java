package top.bogey.touch_tool_pro.bean.action.start;

import com.google.gson.JsonObject;

import java.util.regex.Pattern;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinSubType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinApplication;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;
import top.bogey.touch_tool_pro.service.WorldState;

public class NotifyStartAction extends StartAction {
    private transient Pin appPin = new Pin(new PinApplication(PinSubType.MULTI), R.string.pin_app);
    private transient Pin matchPin = new Pin(new PinString(".+"), R.string.action_notification_start_subtitle_text);
    private transient Pin noticePin = new Pin(new PinString(), R.string.action_notification_start_subtitle_notice, true);

    public NotifyStartAction() {
        super(ActionType.NOTIFY_START);
        appPin = addPin(appPin);
        matchPin = addPin(matchPin);
        noticePin = addPin(noticePin);
    }

    public NotifyStartAction(JsonObject jsonObject) {
        super(jsonObject);
        appPin = reAddPin(appPin);
        matchPin = reAddPin(matchPin);
        noticePin = reAddPin(noticePin);
    }

    @Override
    public void resetReturnValue(Pin pin) {

    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinString notice = noticePin.getValue(PinString.class);
        notice.setValue(WorldState.getInstance().getNotificationText());
        super.execute(runnable, context, pin);
    }

    @Override
    public boolean checkReady(TaskRunnable runnable, FunctionContext context) {
        WorldState worldState = WorldState.getInstance();
        String packageName = worldState.getNotificationPackage();
        if (packageName == null) return false;

        String notificationText = worldState.getNotificationText();
        if (notificationText == null) return false;

        PinString text = (PinString) getPinValue(runnable, context, matchPin);
        if (text.getValue() == null || text.getValue().isEmpty()) return false;

        Pattern compile = Pattern.compile(text.getValue());
        boolean result = compile.matcher(notificationText).find();

        if (!result) return false;

        MainAccessibilityService service = MainApplication.getInstance().getService();
        PinApplication appValue = (PinApplication) getPinValue(runnable, context, appPin);
        return appValue.contain(service, packageName, null);
    }
}
