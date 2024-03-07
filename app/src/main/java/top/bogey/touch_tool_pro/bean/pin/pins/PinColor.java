package top.bogey.touch_tool_pro.bean.pin.pins;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.Arrays;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class PinColor extends PinScreen {
    private int[] color;
    private int min;
    private int max;

    public PinColor() {
        super(PinType.COLOR);
        color = new int[]{0, 0, 0};
    }

    public PinColor(int[] color, int min, int max) {
        this();
        this.color = color;
        this.min = min;
        this.max = max;
    }

    public PinColor(Context context, int[] color, int min, int max) {
        super(PinType.COLOR, context);
        this.color = color;
        this.min = min;
        this.max = max;
    }

    public PinColor(JsonObject jsonObject) {
        super(jsonObject);
        color = GsonUtils.getAsObject(jsonObject, "color", int[].class, new int[]{0, 0, 0});
        min = GsonUtils.getAsInt(jsonObject, "min", 0);
        max = GsonUtils.getAsInt(jsonObject, "max", 0);
    }

    @Override
    public void resetValue() {
        super.resetValue();
        color = new int[]{0, 0, 0};
        min = 0;
        max = 0;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString() + Arrays.toString(color) + "(" + min + "," + max + ")";
    }

    @Override
    public int getPinColor(Context context) {
        return context.getColor(R.color.ColorPinColor);
    }

    public int[] getColor() {
        if (color == null || color.length == 0) return new int[]{0, 0, 0};
        return color;
    }

    public void setColor(int[] color) {
        this.color = color;
    }

    public int getMin(Context context) {
        return (int) (min * getScale(context));
    }

    public int getMax(Context context) {
        return (int) (max * getScale(context));
    }

    public void setArea(Context context, int min, int max) {
        setScreen(context);
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PinColor pinColor = (PinColor) o;

        if (min != pinColor.min) return false;
        if (max != pinColor.max) return false;
        return Arrays.equals(color, pinColor.color);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(color);
        result = 31 * result + min;
        result = 31 * result + max;
        return result;
    }
}
