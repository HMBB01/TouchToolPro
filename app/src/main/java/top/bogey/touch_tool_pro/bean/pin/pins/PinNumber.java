package top.bogey.touch_tool_pro.bean.pin.pins;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.bean.pin.PinSubType;
import top.bogey.touch_tool_pro.bean.pin.PinType;

public abstract class PinNumber<T extends Number> extends PinValue {
    protected T value;

    public PinNumber(PinType type, T value) {
        super(type);
        this.value = value;
    }

    public PinNumber(PinType type, PinSubType subType, T value) {
        super(type, subType);
        this.value = value;
    }

    public PinNumber(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public void newInfo() {
        cast("0");
    }

    @Override
    public abstract int getPinColor(Context context);

    @NonNull
    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PinNumber<?> pinNumber = (PinNumber<?>) o;

        return value.equals(pinNumber.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
