package top.bogey.touch_tool_pro.bean.pin.pins;

import android.content.Context;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.PinSubType;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class PinLong extends PinNumber<Long> {

    public PinLong() {
        super(PinType.LONG, 0L);
    }

    public PinLong(Long value) {
        super(PinType.LONG, value);
    }

    public PinLong(PinSubType subType, Long value) {
        super(PinType.LONG, subType, value);
    }

    public PinLong(JsonObject jsonObject) {
        super(jsonObject);
        value = GsonUtils.getAsLong(jsonObject, "value", 0L);
    }

    @Override
    public boolean contain(PinObject pinObject) {
        if (super.contain(pinObject)) return getSubType() == pinObject.getSubType();
        return false;
    }

    @Override
    public int getPinColor(Context context) {
        return context.getColor(R.color.LongPinColor);
    }

    @Override
    public boolean cast(String value) {
        try {
            this.value = Long.parseLong(value);
            return true;
        } catch (NumberFormatException ignored) {
        }
        return false;
    }
}
