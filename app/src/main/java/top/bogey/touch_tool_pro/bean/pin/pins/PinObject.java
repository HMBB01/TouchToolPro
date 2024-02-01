package top.bogey.touch_tool_pro.bean.pin.pins;

import android.content.Context;
import android.util.Log;

import androidx.annotation.ColorInt;

import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

import top.bogey.touch_tool_pro.bean.pin.PinSubType;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.utils.DisplayUtils;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public abstract class PinObject {
    private final PinType type;
    private final PinSubType subType;

    public PinObject() {
        this(PinType.OBJECT);
    }

    public PinObject(PinType type) {
        this(type, PinSubType.NORMAL);
    }

    public PinObject(PinType type, PinSubType subType) {
        this.type = type;
        this.subType = subType;
    }

    public PinObject(JsonObject jsonObject) {
        type = GsonUtils.getAsObject(jsonObject, "type", PinType.class, PinType.OBJECT);
        subType = GsonUtils.getAsObject(jsonObject, "subType", PinSubType.class, PinSubType.NORMAL);
    }

    public PinObject copy() {
        return GsonUtils.copy(this, PinObject.class);
    }

    public abstract void resetValue();

    // 针脚是否能够连接，看类型是否一致或检测对象继承于我
    public boolean match(PinObject pinObject) {
        return type == pinObject.type || getClass().isInstance(pinObject) || pinObject.getClass().isInstance(this);
    }

    public @ColorInt int getPinColor(Context context) {
        return DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorPrimaryInverse, 0);
    }

    public ShapeAppearanceModel getPinStyle(Context context) {
        float cornerSize = DisplayUtils.dp2px(context, 6);
        return ShapeAppearanceModel.builder()
                .setTopLeftCorner(CornerFamily.ROUNDED, cornerSize)
                .setTopRightCorner(CornerFamily.ROUNDED, cornerSize)
                .setBottomLeftCorner(CornerFamily.ROUNDED, cornerSize)
                .setBottomRightCorner(CornerFamily.ROUNDED, cornerSize)
                .build();
    }

    public PinType getType() {
        return type;
    }

    public PinSubType getSubType() {
        return subType;
    }

    public static class PinObjectDeserializer implements JsonDeserializer<PinObject> {

        @Override
        public PinObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String type = jsonObject.get("type").getAsString();
            try {
                PinType pinType = PinType.valueOf(type);
                Class<? extends PinObject> objectClass = pinType.getConfig().getPinClass();
                if (objectClass == null) return null;
                Constructor<? extends PinObject> constructor = objectClass.getConstructor(JsonObject.class);
                return constructor.newInstance(jsonObject);
            } catch (Exception e) {
                Log.d("TAG", "deserialize: " + type);
                e.printStackTrace();
                return null;
            }
        }
    }
}
