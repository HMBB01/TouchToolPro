package top.bogey.touch_tool_pro.bean.pin.pins;

import android.content.Context;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.Objects;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.PinType;

public class PinNode extends PinValue {
    private transient AccessibilityNodeInfo node;

    public PinNode() {
        super(PinType.NODE);
    }

    public PinNode(AccessibilityNodeInfo node) {
        this();
        this.node = node;
    }

    public PinNode(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public void resetValue() {
        node = null;
    }

    @Override
    public PinObject copy() {
        PinNode copy = (PinNode) super.copy();
        copy.node = node;
        return copy;
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
}
