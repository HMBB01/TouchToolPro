package top.bogey.touch_tool_pro.bean.pin.pins;

import android.content.Context;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.utils.DisplayUtils;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class PinAdd extends PinObject {
    private final Pin pin;
    private int offset = 1;

    public PinAdd(Pin pin) {
        super(PinType.ADD);
        this.pin = pin;
    }

    public PinAdd(Pin pin, int offset) {
        this(pin);
        this.offset = offset;
    }

    public PinAdd(JsonObject jsonObject) {
        super(jsonObject);
        pin = GsonUtils.getAsObject(jsonObject, "pin", Pin.class, null);
        offset = GsonUtils.getAsInt(jsonObject, "offset", 1);
    }

    @Override
    public void resetValue() {

    }

    @Override
    public int getPinColor(Context context) {
        return DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorSurfaceVariant, 0);
    }

    public Pin getPin() {
        return pin;
    }

    public int getOffset() {
        return offset;
    }


}
