package top.bogey.touch_tool_pro.bean.pin.pins;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.bean.pin.PinSubType;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.utils.DisplayUtils;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public abstract class PinScreen extends PinValue {
    private int screen = 1080;

    public PinScreen(PinType type) {
        super(type);
    }

    public PinScreen(PinType type, PinSubType subType) {
        super(type, subType);
    }

    public PinScreen(PinType type, Context context) {
        super(type);
        screen = DisplayUtils.getScreen(context);
    }

    public PinScreen(JsonObject jsonObject) {
        super(jsonObject);
        screen = GsonUtils.getAsInt(jsonObject, "screen", 1080);
    }

    @Override
    public void resetValue() {
        screen = 1080;
    }

    @NonNull
    @Override
    public String toString() {
        return String.valueOf(screen);
    }

    public float getScale(Context context) {
        return DisplayUtils.getScreen(context) * 1f / screen;
    }

    public int getScreen() {
        return screen;
    }

    public void setScreen(Context context) {
        screen = DisplayUtils.getScreen(context);
    }
}
