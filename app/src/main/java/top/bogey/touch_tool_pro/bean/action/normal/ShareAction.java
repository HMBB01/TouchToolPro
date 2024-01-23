package top.bogey.touch_tool_pro.bean.action.normal;

import android.content.Context;
import android.content.Intent;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinSubType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinApplication;
import top.bogey.touch_tool_pro.bean.pin.pins.PinImage;
import top.bogey.touch_tool_pro.bean.pin.pins.PinSpinner;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.utils.AppUtils;

public class ShareAction extends NormalAction {
    private final transient LinkedHashMap<String, ArrayList<String>> apps = new LinkedHashMap<>(Collections.singletonMap(MainApplication.getInstance().getString(R.string.common_package_name), new ArrayList<>()));
    private transient Pin appPin = new Pin(new PinApplication(PinSubType.SHARE_ACTIVITY, apps), R.string.pin_app);
    private transient Pin valuePin = new Pin(new PinValue(), R.string.pin_value);
    private transient Pin typePin = new Pin(new PinSpinner(R.array.share_type), R.string.action_share_action_subtitle_as);

    public ShareAction() {
        super(ActionType.SHARE);
        appPin = addPin(appPin);
        valuePin = addPin(valuePin);
        typePin = addPin(typePin);
    }

    public ShareAction(JsonObject jsonObject) {
        super(jsonObject);
        appPin = reAddPin(appPin);
        valuePin = reAddPin(valuePin);
        typePin = reAddPin(typePin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinApplication app = (PinApplication) getPinValue(runnable, context, appPin);
        PinValue value = (PinValue) getPinValue(runnable, context, valuePin);
        if (!app.getApps().isEmpty() && !value.toString().isEmpty()) {
            Context instance = MainApplication.getInstance();
            String packageName = null, activity = null;
            for (Map.Entry<String, ArrayList<String>> entry : app.getApps().entrySet()) {
                packageName = entry.getKey();
                ArrayList<String> activities = entry.getValue();
                if (!activities.isEmpty()) activity = activities.get(0);
                break;
            }

            int type = ((PinSpinner) getPinValue(runnable, context, typePin)).getIndex();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            switch (type) {
                case 0 -> {
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, value.toString());
                }
                case 1 -> {
                    if (value instanceof PinImage) {
                        intent.setType("image/*");
                    } else {
                        intent.setType("text/*");
                    }
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putExtra(Intent.EXTRA_STREAM, AppUtils.valueToCacheFile(instance, value));
                }
            }

            if (!instance.getString(R.string.common_package_name).equals(packageName)) {
                intent.setPackage(packageName);
                if (activity != null) intent.setClassName(packageName, activity);
                instance.startActivity(intent);
            } else {
                Intent chooser = Intent.createChooser(intent, "");
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                instance.startActivity(chooser);
            }
        }

        executeNext(runnable, context, outPin);
    }
}
