package top.bogey.touch_tool_pro.bean.pin.pins;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class PinValueArea extends PinValue {
    private int min = 10;
    private int max = 60000;
    private int step = 10;

    private int low = 10;
    private int high = 60000;

    public PinValueArea() {
        super(PinType.VALUE_AREA);
    }

    public PinValueArea(int min, int max, int step) {
        this();
        this.min = min;
        this.max = max;
        this.step = step;
        low = min;
        high = max;
    }

    public PinValueArea(int min, int max, int step, int low, int high) {
        this();
        this.min = min;
        this.max = max;
        this.step = step;
        this.low = low;
        this.high = high;
    }

    public PinValueArea(JsonObject jsonObject) {
        super(jsonObject);
        min = GsonUtils.getAsInt(jsonObject, "min", 10);
        max = GsonUtils.getAsInt(jsonObject, "max", 60000);
        step = GsonUtils.getAsInt(jsonObject, "step", 10);
        low = GsonUtils.getAsInt(jsonObject, "low", 10);
        high = GsonUtils.getAsInt(jsonObject, "high", 60000);
    }

    @Override
    public void resetValue() {
        low = min;
        high = max;
    }

    @NonNull
    @Override
    public String toString() {
        return low + "~" + high;
    }

    @Override
    public int getPinColor(Context context) {
        return context.getColor(R.color.ValueAreaPinColor);
    }

    public int getRandom() {
        return (int) (low + (high - low) * Math.random());
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getStep() {
        return step;
    }

    public void setArea(int min, int max, int step) {
        this.min = min;
        this.max = (max - min) / step * step + min;
        this.step = step;
        setLow(low);
        setHigh(high);
    }

    public int getLow() {
        return low;
    }

    public void setLow(int low) {
        low = Math.max(min, Math.min(max, low));
        low = (low - min) / step * step + min;
        this.low = low;
    }

    public int getHigh() {
        return high;
    }

    public void setHigh(int high) {
        high = Math.max(min, Math.min(max, high));
        high = (high - min) / step * step + min;
        this.high = high;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PinValueArea that = (PinValueArea) o;

        if (low != that.low) return false;
        return high == that.high;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + low;
        result = 31 * result + high;
        return result;
    }
}
