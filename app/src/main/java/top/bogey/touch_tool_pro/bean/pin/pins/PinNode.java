package top.bogey.touch_tool_pro.bean.pin.pins;

import android.content.Context;
import android.os.Parcel;
import android.util.Base64;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Objects;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class PinNode extends PinValue {
    private AccessibilityNodeInfo node;

    public PinNode() {
        super(PinType.NODE);
    }

    public PinNode(AccessibilityNodeInfo node) {
        this();
        this.node = node;
    }

    public PinNode(JsonObject jsonObject) {
        super(jsonObject);
        node = parseNode(GsonUtils.getAsString(jsonObject, "node", null));
    }

    @Override
    public void newInfo() {
        node = null;
    }

    private AccessibilityNodeInfo parseNode(String s) {
        if (s == null || s.isEmpty()) return null;
        byte[] bytes = Base64.decode(s, Base64.NO_WRAP);
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0);
        AccessibilityNodeInfo nodeInfo = AccessibilityNodeInfo.CREATOR.createFromParcel(parcel);
        parcel.recycle();
        return nodeInfo;
    }

    @NonNull
    @Override
    public String toString() {
        if (node != null) {
            CharSequence text = node.getText();
            if (text != null) {
                return text.toString();
            }
        }
        return "";
    }

    @Override
    public int getPinColor(Context context) {
        return context.getColor(R.color.NodePinColor);
    }

    public AccessibilityNodeInfo getNode() {
        return node;
    }

    public void setNode(AccessibilityNodeInfo node) {
        this.node = node;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PinNode pinNode = (PinNode) o;

        return Objects.equals(node, pinNode.node);
    }

    @Override
    public int hashCode() {
        return node != null ? node.hashCode() : 0;
    }

    public static class PinNodeSerializer implements JsonSerializer<PinNode> {
        @Override
        public JsonElement serialize(PinNode src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("type", src.getType().name());
            jsonObject.addProperty("subType", src.getSubType().name());
            if (src.node != null) {
                Parcel parcel = Parcel.obtain();
                src.node.writeToParcel(parcel, 0);
                byte[] bytes = parcel.marshall();
                String s = Base64.encodeToString(bytes, Base64.NO_WRAP);
                jsonObject.addProperty("node", s);
                parcel.recycle();
            }
            return jsonObject;
        }
    }
}
