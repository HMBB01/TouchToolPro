package top.bogey.touch_tool_pro.bean.pin.pins;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.utils.DisplayUtils;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class PinValueArray extends PinValue {
    private PinType pinType = PinType.STRING;
    private boolean canChange;
    private ArrayList<PinValue> values = new ArrayList<>();

    public PinValueArray() {
        super(PinType.VALUE_ARRAY);
        canChange = true;
    }

    public PinValueArray(PinType pinType) {
        this();
        this.pinType = pinType;
    }

    public PinValueArray(PinType pinType, boolean canChange) {
        super(PinType.VALUE_ARRAY);
        this.pinType = pinType;
        this.canChange = canChange;
    }

    public PinValueArray(JsonObject jsonObject) {
        super(jsonObject);
        pinType = GsonUtils.getAsObject(jsonObject, "pinType", PinType.class, PinType.STRING);
        canChange = GsonUtils.getAsBoolean(jsonObject, "canChange", true);
        values = GsonUtils.getAsObject(jsonObject, "values", TypeToken.getParameterized(ArrayList.class, PinObject.class).getType(), new ArrayList<>());
    }

    @Override
    public void newInfo() {
        values.clear();
    }

    public PinType getPinType() {
        return pinType;
    }

    public void setPinType(PinType pinType) {
        this.pinType = pinType;
    }

    public boolean isCanChange() {
        return canChange;
    }

    public void setCanChange(boolean canChange) {
        this.canChange = canChange;
    }

    public ArrayList<PinValue> getValues() {
        return values;
    }

    public void setValues(ArrayList<PinValue> values) {
        this.values = values;
    }


    @Override
    public PinObject copy() {
        PinValueArray array = new PinValueArray(pinType, canChange);
        for (PinValue value : values) {
            array.values.add((PinValue) value.copy());
        }
        return array;
    }

    @NonNull
    @Override
    public String toString() {
        return values.toString();
    }

    @Override
    public boolean match(PinObject pinObject) {
        if (super.match(pinObject)) {
            if (pinObject instanceof PinValueArray array) {
                return array.pinType == pinType;
            }
            return true;
        }
        return false;
    }

    @Override
    public int getPinColor(Context context) {
        try {
            Class<? extends PinObject> objectClass = pinType.getConfig().getPinClass();
            if (objectClass == null || objectClass.equals(PinValue.class)) return context.getColor(R.color.ArrayPinColor);
            Constructor<? extends PinObject> constructor = objectClass.getConstructor();
            PinObject pinObject = constructor.newInstance();
            return pinObject.getPinColor(context);
        } catch (Exception e) {
            return context.getColor(R.color.ArrayPinColor);
        }
    }

    @Override
    public ShapeAppearanceModel getPinStyle(Context context) {
        float cornerSize = DisplayUtils.dp2px(context, 2);
        return ShapeAppearanceModel.builder()
                .setTopLeftCorner(CornerFamily.ROUNDED, cornerSize)
                .setTopRightCorner(CornerFamily.ROUNDED, cornerSize)
                .setBottomLeftCorner(CornerFamily.ROUNDED, cornerSize)
                .setBottomRightCorner(CornerFamily.ROUNDED, cornerSize)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PinValueArray array = (PinValueArray) o;

        if (pinType != array.pinType) return false;
        if (canChange != array.canChange) return false;
        return values.equals(array.values);
    }

    @Override
    public int hashCode() {
        int result = pinType.hashCode();
        result = 31 * result + (canChange ? 1 : 0);
        result = 31 * result + values.hashCode();
        return result;
    }
}
