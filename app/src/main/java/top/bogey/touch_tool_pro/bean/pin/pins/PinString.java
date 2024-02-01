package top.bogey.touch_tool_pro.bean.pin.pins;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.Objects;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.PinSubType;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class PinString extends PinValue {
    protected String value = "";

    public PinString() {
        super(PinType.STRING);
    }

    public PinString(String value) {
        this();
        this.value = value;
    }

    public PinString(PinType type) {
        super(type);
    }

    public PinString(PinType type, String value) {
        super(type);
        this.value = value;
    }

    public PinString(PinSubType subType) {
        super(PinType.STRING, subType);
    }

    public PinString(PinSubType subType, String value) {
        super(PinType.STRING, subType);
        this.value = value;
    }

    public PinString(JsonObject jsonObject) {
        super(jsonObject);
        value = GsonUtils.getAsString(jsonObject, "value", null);
    }

    @Override
    public void resetValue() {
        value = "";
    }

    @Override
    public boolean cast(String value) {
        this.value = value;
        return true;
    }

    @NonNull
    @Override
    public String toString() {
        return value;
    }

    @Override
    public int getPinColor(Context context) {
        return context.getColor(R.color.StringPinColor);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PinString pinString = (PinString) o;

        return Objects.equals(value, pinString.value);
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
