package top.bogey.touch_tool_pro.bean.pin.pins;

import android.content.Context;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class PinSpinner extends PinValue {
    private final ArrayList<String> arrays = new ArrayList<>();
    private int index;
    private transient @ArrayRes int array;

    public PinSpinner() {
        super(PinType.SPINNER);
    }

    public PinSpinner(@ArrayRes int array) {
        this();
        this.array = array;
    }

    public PinSpinner(ArrayList<String> arrays) {
        this.arrays.addAll(arrays);
    }

    public PinSpinner(JsonObject jsonObject) {
        super(jsonObject);
        index = GsonUtils.getAsInt(jsonObject, "index", 0);
        arrays.addAll(GsonUtils.getAsObject(jsonObject, "arrays", TypeToken.getParameterized(ArrayList.class, String.class).getType(), new ArrayList<>()));
    }

    @Override
    public void resetValue() {
        index = 0;
    }

    @Override
    public boolean contain(PinObject pinObject) {
        if (super.contain(pinObject)) {
            if (pinObject instanceof PinSpinner spinner) {
                if (array == spinner.array && array != 0) return true;
                return arrays.equals(spinner.arrays);
            }
        }
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        String[] arrays = getArray(MainApplication.getInstance());
        if (arrays.length > 0 && index >= 0 && index < arrays.length) return arrays[index];
        return super.toString();
    }

    @Override
    public int getPinColor(Context context) {
        return context.getColor(R.color.SpinnerPinColor);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getArray() {
        return array;
    }

    public void setArray(int array) {
        this.array = array;
    }

    public String[] getArray(Context context) {
        if (array == 0) {
            String[] arrays = new String[this.arrays.size()];
            this.arrays.toArray(arrays);
            return arrays;
        }
        return context.getResources().getStringArray(array);
    }

    public void setArrays(ArrayList<String> arrays) {
        this.arrays.clear();
        this.arrays.addAll(arrays);
        index = Math.max(0, Math.min(index, arrays.size() - 1));
    }

    public String getArrayString(Context context) {
        StringBuilder builder = new StringBuilder();
        for (String s : getArray(context)) {
            builder.append(s);
            builder.append(",");
        }
        if (builder.length() == 0) return "";
        return builder.substring(0, builder.length() - 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PinSpinner that = (PinSpinner) o;

        if (index != that.index) return false;
        if (array != that.array) return false;
        return arrays.equals(that.arrays);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + arrays.hashCode();
        result = 31 * result + index;
        result = 31 * result + array;
        return result;
    }
}
