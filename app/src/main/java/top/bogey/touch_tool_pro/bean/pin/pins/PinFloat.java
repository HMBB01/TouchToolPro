package top.bogey.touch_tool_pro.bean.pin.pins;

import android.content.Context;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class PinFloat extends PinNumber<Float> {

    public PinFloat() {
        super(PinType.FLOAT, 0f);
    }

    public PinFloat(float value) {
        super(PinType.FLOAT, value);
    }

    public PinFloat(JsonObject jsonObject) {
        super(jsonObject);
        value = GsonUtils.getAsFloat(jsonObject, "value", 0f);
    }

    @Override
    public boolean contain(PinObject pinObject) {
        if (super.contain(pinObject)) return true;
        return pinObject.getType() == PinType.INT;
    }

    @Override
    public boolean cast(String value) {
        try {
            this.value = Float.parseFloat(value);
            return true;
        } catch (NumberFormatException ignored) {
        }
        return false;
    }

    @Override
    public int getPinColor(Context context) {
        return context.getColor(R.color.FloatPinColor);
    }

    public static float getFloatValue(PinObject pinObject) {
        if (pinObject instanceof PinFloat pinFloat) return pinFloat.value;
        else if (pinObject instanceof PinInteger pinInteger) return pinInteger.value;
        else return 0;
    }
}
