package top.bogey.touch_tool_pro.save;

import com.tencent.mmkv.MMKV;

import top.bogey.touch_tool_pro.bean.pin.pins.PinObject;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class VariableSaveReference extends SaveReference<PinValue> {

    public VariableSaveReference(MMKV mmkv, String saveId) {
        super(mmkv, saveId);
    }

    public VariableSaveReference(MMKV mmkv, String saveId, PinValue save) {
        super(mmkv, saveId, save);
    }

    @Override
    public PinValue getOrigin() {
        return (PinValue) GsonUtils.getAsObject(mmkv.decodeString(saveId), PinObject.class, null);
    }
}
