package top.bogey.touch_tool_pro.bean.pin.pins;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class PinBoolean extends PinValue {
    private boolean bool = false;

    public PinBoolean() {
        super(PinType.BOOLEAN);
    }

    public PinBoolean(boolean bool) {
        this();
        this.bool = bool;
    }

    public PinBoolean(JsonObject jsonObject) {
        super(jsonObject);
        bool = GsonUtils.getAsBoolean(jsonObject, "bool", false);
    }

    @Override
    public int getPinColor(Context context) {
        return context.getColor(R.color.BooleanPinColor);
    }

    @Override
    public boolean cast(String value) {
        if (Boolean.toString(true).equals(value)) bool = true;
        else if (Boolean.toString(false).equals(value)) bool = false;
        else bool = !"0".equals(value);
        return true;
    }

    @Override
    public void resetValue() {
        bool = false;
    }

    @NonNull
    @Override
    public String toString() {
        return String.valueOf(bool);
    }

    public boolean isBool() {
        return bool;
    }

    public void setBool(boolean bool) {
        this.bool = bool;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PinBoolean that = (PinBoolean) o;

        return bool == that.bool;
    }

    @Override
    public int hashCode() {
        return (bool ? 1 : 0);
    }
}
