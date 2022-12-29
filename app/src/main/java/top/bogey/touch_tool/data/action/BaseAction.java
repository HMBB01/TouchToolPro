package top.bogey.touch_tool.data.action;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.pin.Pin;
import top.bogey.touch_tool.data.action.pin.PinDirection;
import top.bogey.touch_tool.data.action.pin.PinSlotType;
import top.bogey.touch_tool.data.action.pin.object.PinExecute;
import top.bogey.touch_tool.data.action.pin.object.PinObject;

public class BaseAction implements Parcelable {
    private final String id;
    private final String cls;
    private CharSequence title;
    protected transient int titleId;

    private boolean enable = true;

    private final ArrayList<Pin<? extends PinObject>> pins = new ArrayList<>();

    public int x;
    public int y;

    protected transient Pin<? extends PinObject> inPin;
    protected transient Pin<? extends PinObject> outPin;
    protected transient ArrayList<Pin<? extends PinObject>> pinsTmp = new ArrayList<>();

    public BaseAction() {
        id = UUID.randomUUID().toString();
        cls = getClass().getName();

        inPin = new Pin<>(new PinExecute(), PinSlotType.MULTI);
        outPin = new Pin<>(new PinExecute(), PinDirection.OUT, PinSlotType.SINGLE);
    }

    public BaseAction(Parcel in) {
        cls = getClass().getName();
        id = in.readString();
        title = in.readString();
        enable = in.readByte() == 1;
        in.readTypedList(pinsTmp, Pin.CREATOR);
        x = in.readInt();
        y = in.readInt();
    }

    public static final Creator<BaseAction> CREATOR = new Creator<BaseAction>() {
        @Override
        public BaseAction createFromParcel(Parcel in) {
            try {
                Class<?> aClass = Class.forName(in.readString());
                Constructor<?> constructor = aClass.getConstructor(Parcel.class);
                return (BaseAction) constructor.newInstance(in);
            } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
                     IllegalAccessException | InstantiationException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public BaseAction[] newArray(int size) {
            return new BaseAction[size];
        }
    };

    public boolean doAction(WorldState worldState, TaskRunnable runnable) {
        if (Thread.currentThread().isInterrupted()) return false;

        for (Map.Entry<String, String> entry : outPin.getLinks().entrySet()) {
            BaseAction action = runnable.getTask().getActionById(entry.getKey());
            if (action == null) continue;
            runnable.addProgress();
            return action.doAction(worldState, runnable);
        }
        return true;
    }

    // 获取针脚值之前先计算下针脚
    protected void calculatePinValue(WorldState worldState, Task task) {

    }

    protected boolean sleep(long time) {
        try {
            Thread.sleep(time);
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public <T extends PinObject> Pin<T> addPin(Pin<T> pin) {
        if (pin == null) throw new RuntimeException("空的插槽");
        for (Pin<? extends PinObject> oldPin : pins) {
            if (oldPin.getId().equals(pin.getId())) throw new RuntimeException("重复的插槽");
        }
        pins.add(pin);
        pin.setActionId(id);
        return pin;
    }

    public Pin<? extends PinObject> getPinById(String id) {
        for (Pin<? extends PinObject> pin : pins) {
            if (pin.getId().equals(id)) return pin;
        }
        return null;
    }

    // 获取针脚的值
    protected PinObject getPinValue(WorldState worldState, Task task, Pin<? extends PinObject> pin) {
        // 先看看自己是不是输出针脚，输出针脚直接返回针脚值
        if (pin.getDirection() == PinDirection.OUT) {
            calculatePinValue(worldState, task);
            return pin.getValue();
        }

        // 再看看针脚有没有连接，有连接就是连接的值
        if (pin.getLinks().size() > 0) {
            for (Map.Entry<String, String> entry : pin.getLinks().entrySet()) {
                BaseAction action = task.getActionById(entry.getKey());
                if (action == null) continue;
                Pin<? extends PinObject> pinById = action.getPinById(id);
                if (pinById == null) continue;
                return action.getPinValue(worldState, task, pinById);
            }
            throw new RuntimeException("针脚没有默认值");
        } else {
            // 否则，就是自己的默认值
            calculatePinValue(worldState, task);
            return pin.getValue();
        }
    }

    public String getId() {
        return id;
    }

    public String getCls() {
        return cls;
    }

    public CharSequence getTitle(Context context) {
        if (title == null || title.length() == 0) {
            if (titleId == 0) return null;
            return context.getString(titleId);
        } else return title;
    }

    public void setTitle(CharSequence title) {
        this.title = title;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public ArrayList<Pin<? extends PinObject>> getPins() {
        return pins;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(cls);
        dest.writeString(id);
        dest.writeString(title == null ? null : title.toString());
        dest.writeByte((byte) (enable ? 1 : 0));
        dest.writeTypedList(pins);
        dest.writeInt(x);
        dest.writeInt(y);
    }
}
