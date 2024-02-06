package top.bogey.touch_tool_pro.bean.action;

import android.content.Context;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import java.io.IOException;
import java.io.InputStream;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.task.TaskExample;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class ActionConfigInfo {
    private final ActionType type;
    private final @StringRes int title;
    private final @DrawableRes int icon;
    private final Class<? extends Action> actionClass;
    private final boolean superAction;

    public ActionConfigInfo() {
        type = ActionType.BASE;
        title = 0;
        icon = 0;
        actionClass = null;
        superAction = false;
    }

    public ActionConfigInfo(ActionType type, int title, int icon, Class<? extends Action> actionClass) {
        this.type = type;
        this.title = title;
        this.icon = icon;
        this.actionClass = actionClass;
        superAction = false;
    }

    public ActionConfigInfo(ActionType type, int title, int icon, Class<? extends Action> actionClass, boolean superAction) {
        this.type = type;
        this.title = title;
        this.icon = icon;
        this.actionClass = actionClass;
        this.superAction = superAction;
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
        String[] array = MainApplication.getInstance().getResources().getStringArray(R.array.action_help);
        if (array.length == 0) return "";
        if (array.length < type.ordinal()) return "";
        return array[type.ordinal()];
    }

    public int getIcon() {
        return icon;
    }

    public Class<? extends Action> getActionClass() {
        return actionClass;
    }

    public boolean isSuperAction() {
        return superAction;
    }
}
