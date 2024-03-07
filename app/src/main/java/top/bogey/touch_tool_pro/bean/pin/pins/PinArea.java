package top.bogey.touch_tool_pro.bean.pin.pins;

import android.content.Context;
import android.graphics.Rect;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.PinSubType;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.utils.DisplayUtils;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class PinArea extends PinScreen {
    private Rect area = new Rect();

    public PinArea() {
        super(PinType.AREA);
    }

    public PinArea(PinSubType subType) {
        super(PinType.AREA, subType);
    }

    public PinArea(Rect area) {
        this();
        this.area = area;
    }

    public PinArea(Context context, Rect area) {
        super(PinType.AREA, context);
        this.area = area;
    }

    public PinArea(JsonObject jsonObject) {
        super(jsonObject);
        area = GsonUtils.getAsObject(jsonObject, "area", Rect.class, new Rect());
    }

    @Override
    public void resetValue() {
        super.resetValue();
        area = new Rect();
    }

    @Override
    public boolean cast(String value) {
        Pattern pattern = Pattern.compile("\\((\\d+),(\\d+),(\\d+),(\\d+)\\)");
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()) {
            try {
                area.left = Integer.parseInt(Objects.requireNonNull(matcher.group(1)));
                area.top = Integer.parseInt(Objects.requireNonNull(matcher.group(2)));
                area.right = Integer.parseInt(Objects.requireNonNull(matcher.group(3)));
                area.bottom = Integer.parseInt(Objects.requireNonNull(matcher.group(4)));
                return true;
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString() + "(" + area.left + "," + area.top + "," + area.right + "," + area.bottom + ")";
    }

    @Override
    public int getPinColor(Context context) {
        return context.getColor(R.color.AreaPinColor);
    }

    public Rect getArea(Context context) {
        if (area.isEmpty()) return DisplayUtils.getScreenArea(context);
        float scale = getScale(context);
        return new Rect((int) (area.left * scale), (int) (area.top * scale), (int) (area.right * scale), (int) (area.bottom * scale));
    }

    public Rect getArea() {
        return new Rect(area);
    }

    public void setArea(Context context, Rect area) {
        setScreen(context);
        this.area.set(area);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PinArea pinArea = (PinArea) o;

        return area.equals(pinArea.area);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + area.hashCode();
        return result;
    }
}
