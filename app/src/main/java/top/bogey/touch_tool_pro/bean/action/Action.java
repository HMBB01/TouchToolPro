package top.bogey.touch_tool_pro.bean.action;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import top.bogey.touch_tool_pro.bean.base.IdentityInfo;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinListener;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinAdd;
import top.bogey.touch_tool_pro.bean.pin.pins.PinObject;
import top.bogey.touch_tool_pro.bean.pin.pins.PinSpinner;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class Action extends IdentityInfo implements ActionInterface, ActionExecuteInterface {
    protected final transient ArrayList<Pin> tmpPins = new ArrayList<>();
    protected final transient ArrayList<ActionListener> listeners = new ArrayList<>();
    private final ActionType type;
    private final ArrayList<Pin> pins = new ArrayList<>();
    public transient boolean needCapture = false;
    private int x;
    private int y;
    private boolean expand = true;

    public Action() {
        this(ActionType.BASE);
    }

    public Action(ActionType type) {
        super();
        this.type = type;
    }

    public Action(JsonObject jsonObject) {
        super(jsonObject);
        type = GsonUtils.getAsObject(jsonObject, "type", ActionType.class, ActionType.BASE);

        tmpPins.addAll(GsonUtils.getAsObject(jsonObject, "pins", TypeToken.getParameterized(ArrayList.class, Pin.class).getType(), new ArrayList<>()));

        x = GsonUtils.getAsInt(jsonObject, "x", 0);
        y = GsonUtils.getAsInt(jsonObject, "y", 0);
        expand = GsonUtils.getAsBoolean(jsonObject, "expand", true);
    }

    @Override
    public IdentityInfo copy() {
        return GsonUtils.copy(this, Action.class);
    }

    @Override
    public void newInfo() {
        setId(UUID.randomUUID().toString());
        pins.forEach(pin -> {
            pin.setId(UUID.randomUUID().toString());
            pin.setActionId(getId());
            pin.cleanLinks();
        });
        x++;
        y++;
    }

    @Override
    public String getTitle() {
        return type.getConfig().getTitle();
    }

    @Override
    public Pin addPin(Pin pin) {
        return addPin(pin, pins.size());
    }

    @Override
    public Pin addPin(Pin pin, int index) {
        if (pin == null) return null;
        if (getPinById(pin.getId()) != null) return null;
        pins.add(index, pin);
        pin.setActionId(getId());
        listeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onPinAdded(pin));
        pin.addPinListener(new ActionPinListener(pin));
        return pin;
    }

    @Override
    public Pin reAddPin(Pin def) {
        Pin pin = null;
        if (tmpPins.size() > 0) {
            Pin tmpPin = tmpPins.get(0);
            if (tmpPin.isSameValueType(def)) pin = tmpPins.remove(0);
        }
        if (pin == null) return addPin(def);

        // 设置标题
        pin.setTitleId(def.getTitleId());
        if (pin.isSameValueType(PinAdd.class)) {
            pin.getValue(PinAdd.class).getPin().setTitleId(def.getValue(PinAdd.class).getPin().getTitleId());
        }

        if (pin.isSameValueType(PinSpinner.class)) {
            pin.getValue(PinSpinner.class).setArray(def.getValue(PinSpinner.class).getArray());
        }
        return addPin(pin);
    }

    @Override
    public Pin reAddPin(Pin def, PinType type) {
        Pin pin = null;
        if (tmpPins.size() > 0) {
            Pin tmpPin = tmpPins.get(0);
            if (tmpPin.isSameValueType(type.getConfig().getPinClass())) pin = tmpPins.remove(0);
        }
        if (pin == null) return addPin(def);

        // 设置标题
        pin.setTitleId(def.getTitleId());
        if (pin.isSameValueType(PinAdd.class)) {
            pin.getValue(PinAdd.class).getPin().setTitleId(def.getValue(PinAdd.class).getPin().getTitleId());
        }

        if (pin.isSameValueType(PinSpinner.class)) {
            pin.getValue(PinSpinner.class).setArray(def.getValue(PinSpinner.class).getArray());
        }
        return addPin(pin);
    }

    @Override
    public ArrayList<Pin> reAddPin(Pin def, int remain) {
        ArrayList<Pin> pins = new ArrayList<>();
        int count = 0;
        while (tmpPins.size() > remain && count <= 50) {
            Pin copy = (Pin) def.copy();
            copy.newInfo();
            copy.setTitleId(def.getTitleId());
            pins.add(reAddPin(copy));
            count++;
        }
        return pins;
    }


    @Override
    public ArrayList<Pin> reAddPin(Pin def, int remain, PinType type) {
        ArrayList<Pin> pins = new ArrayList<>();
        int count = 0;
        while (tmpPins.size() > remain && count <= 50) {
            Pin copy = (Pin) def.copy();
            copy.newInfo();
            copy.setTitleId(def.getTitleId());
            pins.add(reAddPin(copy, type));
            count++;
        }
        return pins;
    }

    @Override
    public boolean removePin(Pin pin) {
        if (pins.remove(pin)) {
            listeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onPinRemoved(pin));
            return true;
        }
        return false;
    }

    @Override
    public boolean removePin(Pin pin, FunctionContext context) {
        if (context != null) pin.cleanLinks(context);
        return removePin(pin);
    }

    @Override
    public Pin getPinById(String id) {
        for (Pin pin : pins) {
            if (pin.getId().equals(id)) return pin;
        }
        return null;
    }

    @Override
    public Pin getPinByUid(String uid) {
        for (Pin pin : pins) {
            if (pin.getUid().equals(uid)) return pin;
        }
        return null;
    }

    @Override
    public Pin getFirstPinByClass(Class<? extends PinObject> pinClass, boolean out) {
        for (Pin pin : pins) {
            if (pin.getPinClass().isAssignableFrom(pinClass) || pinClass.isAssignableFrom(pin.getPinClass())) {
                if (pin.isOut() == out) {
                    return pin;
                }
            }
        }
        return null;
    }

    @Override
    public ArrayList<Pin> getPins() {
        return pins;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean isExpand() {
        return expand;
    }

    @Override
    public void setExpand(boolean expand) {
        this.expand = expand;
    }

    @Override
    public void addListener(ActionListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(ActionListener listener) {
        listeners.remove(listener);
    }

    @Override
    public ActionCheckResult check(FunctionContext context) {
        return new ActionCheckResult(ActionCheckResult.ActionResultType.NORMAL, 0);
    }

    @Override
    public boolean isError(FunctionContext context) {
        return check(context).type == ActionCheckResult.ActionResultType.ERROR;
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {

    }

    @Override
    public void executeNext(TaskRunnable runnable, FunctionContext context, Pin pin) {
        runnable.addExecuteProgress(this);

        if (runnable.isInterrupt() || context.isEnd()) return;
        if (!pin.isOut()) return;

        Pin linkedPin = pin.getLinkedPin(context);
        if (linkedPin == null) return;
        Action action = context.getActionById(linkedPin.getActionId());
        Log.d("TAG", "executeNext: " + action.getClass().getName());
        action.execute(runnable, context, linkedPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
    }

    @Override
    public void resetReturnValue(Pin pin) {
        for (Pin p : getPins()) {
            if (p.isOut()) {
                p.getValue().resetValue();
            }
        }
    }

    @Override
    public PinObject getPinValue(TaskRunnable runnable, FunctionContext context, Pin pin) {
        if (pin.isOut()) {
            resetReturnValue(pin);
            calculate(runnable, context, pin);
            runnable.addCalculateProgress(this);
            return pin.getValue();
        }

        if (pin.getLinks().size() > 0) {
            Pin linkedPin = pin.getLinkedPin(context);
            if (linkedPin != null) {
                ActionExecuteInterface action = context.getActionById(linkedPin.getActionId());
                if (action != null) {
                    PinObject value = action.getPinValue(runnable, context, linkedPin);
                    pin.innerSetValue(value);
                    return value;
                }
            }
        }
        return pin.getValue();
    }

    public ActionType getType() {
        return type;
    }

    public static class ActionDeserializer implements JsonDeserializer<Action> {
        @Override
        public Action deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            ActionType type = GsonUtils.getAsObject(jsonObject, "type", ActionType.class, ActionType.BASE);
            ActionConfigInfo config = type.getConfig();
            if (config == null) return null;
            Class<? extends Action> actionClass = config.getActionClass();
            try {
                Constructor<? extends Action> constructor = actionClass.getConstructor(JsonObject.class);
                return constructor.newInstance(jsonObject);
            } catch (Exception e) {
                Log.d("TAG", "deserialize: " + type);
                throw new RuntimeException(e);
            }
        }
    }

    private class ActionPinListener implements PinListener {
        private final Pin pin;

        public ActionPinListener(Pin pin) {
            this.pin = pin;
        }

        @Override
        public void onLinked(Pin linkedPin) {
            listeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onPinChanged(pin));
        }

        @Override
        public void onUnlink(Pin unlinkedPin) {
            listeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onPinChanged(pin));
        }

        @Override
        public void onValueChanged(PinObject value) {
            listeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onPinChanged(pin));
        }

        @Override
        public void onTitleChanged(String title) {
            listeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onPinChanged(pin));
        }
    }
}
