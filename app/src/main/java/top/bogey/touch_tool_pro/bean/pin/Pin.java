package top.bogey.touch_tool_pro.bean.pin;

import androidx.annotation.StringRes;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionInterface;
import top.bogey.touch_tool_pro.bean.base.IdentityInfo;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.pins.PinObject;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArray;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class Pin extends IdentityInfo {
    private final HashMap<String, String> links = new HashMap<>();
    private final transient HashSet<PinListener> listeners = new HashSet<>();
    private PinObject value;
    private boolean out;
    private boolean removeAble;
    private final boolean hide;
    private transient String actionId;
    private transient int titleId;

    public Pin(PinObject value) {
        this(value, 0);
    }

    public Pin(PinObject value, @StringRes int titleId) {
        this(value, titleId, false);
    }

    public Pin(PinObject value, boolean out) {
        this(value, 0, out);
    }

    public Pin(PinObject value, @StringRes int titleId, boolean out) {
        this(value, titleId, out, false, false);
    }

    public Pin(PinObject value, @StringRes int titleId, boolean out, boolean removeAble, boolean hide) {
        super();
        this.value = value;
        this.titleId = titleId;
        this.out = out;
        this.removeAble = removeAble;
        this.hide = hide;
    }

    public Pin(JsonObject jsonObject) {
        super(jsonObject);
        value = GsonUtils.getAsObject(jsonObject, "value", PinObject.class, null);
        out = GsonUtils.getAsBoolean(jsonObject, "out", false);
        removeAble = GsonUtils.getAsBoolean(jsonObject, "removeAble", false);
        hide = GsonUtils.getAsBoolean(jsonObject, "hide", false);
        links.putAll(GsonUtils.getAsObject(jsonObject, "links", TypeToken.getParameterized(HashMap.class, String.class, String.class).getType(), new HashMap<>()));
    }

    @Override
    public IdentityInfo copy() {
        return GsonUtils.copy(this, Pin.class);
    }

    @Override
    public void newInfo() {
        setId(UUID.randomUUID().toString());
    }

    public Pin getLinkedPin(FunctionContext context) {
        for (Map.Entry<String, String> entry : links.entrySet()) {
            ActionInterface action = context.getActionById(entry.getValue());
            if (action == null) continue;
            Pin pin = action.getPinById(entry.getKey());
            if (pin == null) continue;
            return pin;
        }
        return null;
    }

    public Pin getLinkedPin(ArrayList<Action> actions) {
        for (Map.Entry<String, String> entry : links.entrySet()) {
            Action action = null;
            for (Action baseAction : actions) {
                if (baseAction.getId().equals(entry.getValue())) {
                    action = baseAction;
                    break;
                }
            }
            if (action == null) continue;
            Pin pin = action.getPinById(entry.getKey());
            if (pin == null) continue;
            return pin;
        }
        return null;
    }

    public void addLink(Pin pin) {
        links.put(pin.getId(), pin.getActionId());
        listeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onLinked(pin));
    }

    public void addLink(Pin pin, FunctionContext context) {
        if (isSingleLink()) cleanLinks(context);
        addLink(pin);
    }

    public boolean addLinks(HashMap<String, String> links, FunctionContext context) {
        boolean flag = false;
        for (Map.Entry<String, String> entry : links.entrySet()) {
            ActionInterface action = (ActionInterface) context.getActionById(entry.getValue());
            if (action == null) continue;
            Pin pin = action.getPinById(entry.getKey());
            if (pin == null) continue;
            if (!isCanLink(pin)) continue;

            addLink(pin, context);
            pin.addLink(this, context);
            flag = true;
        }
        return flag;
    }

    public void removeLink(Pin pin) {
        if (links.remove(pin.getId()) != null) {
            listeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onUnlink(pin));
        }
    }

    public void cleanLinks() {
        links.clear();
    }

    public void cleanLinks(FunctionContext context) {
        HashMap<String, String> map = new HashMap<>(links);
        map.forEach((pinId, actionId) -> {
            ActionInterface action = (ActionInterface) context.getActionById(actionId);
            if (action == null) return;
            Pin pin = action.getPinById(pinId);
            if (pin == null) return;
            pin.removeLink(this);
            removeLink(pin);
        });
        cleanLinks();
    }

    public Class<? extends PinObject> getPinClass() {
        return value.getClass();
    }

    public boolean isVertical() {
        return value.getType() == PinType.EXECUTE;
    }

    public boolean isSingleLink() {
        if (isVertical()) return out;
        return !out;
    }

    public boolean isSameValueType(Pin pin) {
        return isSameValueType(pin.getPinClass());
    }

    public boolean isSameValueType(Class<? extends PinObject> pinClass) {
        return getPinClass().equals(pinClass);
    }

    public boolean isValueMatched(Pin pin) {
        return isValueMatched(pin.value);
    }

    public boolean isValueMatched(PinObject pinObject) {
        if (out) return pinObject.contain(value);
        return value.contain(pinObject);
    }

    public boolean isCanLink(Pin pin) {
        if (out == pin.out) return false;
        if (actionId.equals(pin.actionId)) return false;
        return isValueMatched(pin);
    }

    public void addPinListener(PinListener listener) {
        listeners.add(listener);
    }

    public void removePinListener(PinListener listener) {
        listeners.remove(listener);
    }

    public PinObject getValue() {
        return value;
    }

    public void setValue(PinObject value) {
        if (value == null) return;
        this.value = value;
        listeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onValueChanged(value));
    }

    public void innerSetValue(PinObject value) {
        if (value == null) return;
        this.value = value;
    }

    public <T extends PinObject> T getValue(Class<T> tClass) {
        return tClass.cast(value);
    }

    public boolean isOut() {
        return out;
    }

    public void setOut(boolean out) {
        this.out = out;
    }

    public boolean isRemoveAble() {
        return removeAble;
    }

    public void setRemoveAble(boolean removeAble) {
        this.removeAble = removeAble;
    }

    public boolean isHide() {
        return hide;
    }

    public HashMap<String, String> getLinks() {
        return links;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public int getTitleId() {
        return titleId;
    }

    public void setTitleId(int titleId) {
        this.titleId = titleId;
    }

    @Override
    public String getTitle() {
        String title = super.getTitle();
        if (title != null && !title.isEmpty()) return title;
        if (titleId == 0) return "";
        if (value instanceof PinValueArray array) {
            return array.getPinType().getConfig().getTitle() + MainApplication.getInstance().getString(titleId);
        }
        return MainApplication.getInstance().getString(titleId);
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);
        listeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onTitleChanged(title));
    }

    public static class PinDeserialize implements JsonDeserializer<Pin> {
        @Override
        public Pin deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            return new Pin(jsonObject);
        }
    }
}
