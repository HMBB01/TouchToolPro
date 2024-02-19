package top.bogey.touch_tool_pro.bean.action;

import android.content.Context;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import java.io.IOException;
import java.io.InputStream;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.task.TaskExample;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class ActionConfigInfo {
    private final ActionType type;
    private final @StringRes int title;
    private final @StringRes int des;
    private final @DrawableRes int icon;
    private final Class<? extends Action> actionClass;

    public ActionConfigInfo() {
        type = ActionType.BASE;
        title = 0;
        des = 0;
        icon = 0;
        actionClass = null;
    }

    public ActionConfigInfo(ActionType type, int title, int icon, Class<? extends Action> actionClass) {
        this.type = type;
        this.title = title;
        des = 0;
        this.icon = icon;
        this.actionClass = actionClass;
    }

    public ActionConfigInfo(ActionType type, int title, int des, int icon, Class<? extends Action> actionClass) {
        this.type = type;
        this.title = title;
        this.des = des;
        this.icon = icon;
        this.actionClass = actionClass;
    }

    public TaskExample getExample(Context context) {
        try (InputStream inputStream = context.getAssets().open("help/" + type.name())) {
            byte[] bytes = new byte[inputStream.available()];
            int read = inputStream.read(bytes);
            if (read > 0) return (TaskExample) GsonUtils.getAsObject(new String(bytes), FunctionContext.class, null);
        } catch (IOException ignored) {
        }
        return null;
    }

    public String getTitle() {
        if (title != 0) return MainApplication.getInstance().getString(title);
        return "";
    }

    public String getDescription() {
        if (des != 0) return MainApplication.getInstance().getString(des);
        return "";
    }

    public int getIcon() {
        return icon;
    }

    public Class<? extends Action> getActionClass() {
        return actionClass;
    }

    public boolean isValid() {
        return true;
    }
}
