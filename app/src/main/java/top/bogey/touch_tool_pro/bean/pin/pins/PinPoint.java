package top.bogey.touch_tool_pro.bean.pin.pins;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class PinPoint extends PinScreen {
    private int x;
    private int y;

    public PinPoint() {
        super(PinType.POINT);
    }

    public PinPoint(int x, int y) {
        this();
        this.x = x;
        this.y = y;
    }

    public PinPoint(Context context, int x, int y) {
        super(PinType.POINT, context);
        this.x = x;
        this.y = y;
    }

    public PinPoint(JsonObject jsonObject) {
        super(jsonObject);
        x = GsonUtils.getAsInt(jsonObject, "x", 0);
        y = GsonUtils.getAsInt(jsonObject, "y", 0);
    }

    @Override
    public void resetValue() {
        x = 0;
        y = 0;
    }

    @Override
    public boolean cast(String value) {
        Pattern pattern = Pattern.compile("\\((\\d+),(\\d+)\\)");
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()) {
            try {
                x = Integer.parseInt(Objects.requireNonNull(matcher.group(1)));
                y = Integer.parseInt(Objects.requireNonNull(matcher.group(2)));
                return true;
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString() + "(" + x + "," + y + ")";
    }

    @Override
    public int getPinColor(Context context) {
        return context.getColor(R.color.PointPinColor);
    }

    public void setPoint(Context context, int x, int y) {
        setScreen(context);
        this.x = x;
        this.y = y;
    }

    public boolean isEmpty() {
        return x == 0 && y == 0;
    }

    public int getX(Context context) {
        return (int) (x * getScale(context));
    }

    public int getX(Context context, int offsetPx) {
        return (int) Math.max(0, Math.random() * 2 * offsetPx - offsetPx + getX(context));
    }

    public int getY(Context context) {
        return (int) (y * getScale(context));
    }

    public int getY(Context context, int offsetPx) {
        return (int) Math.max(0, Math.random() * 2 * offsetPx - offsetPx + getY(context));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PinPoint pinPoint = (PinPoint) o;

        if (x != pinPoint.x) return false;
        return y == pinPoint.y;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + x;
        result = 31 * result + y;
        return result;
    }
}
