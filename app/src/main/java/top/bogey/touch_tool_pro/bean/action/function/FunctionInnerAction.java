package top.bogey.touch_tool_pro.bean.action.function;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionListener;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.Function;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinExecute;

public abstract class FunctionInnerAction extends Action implements ActionListener {
    protected transient Function owner;

    public FunctionInnerAction(ActionType type, Function owner) {
        super(type);
        // 添加初始执行针脚
        Pin pin = addPin(new Pin(new PinExecute(), R.string.pin_execute));
        pin.setRemoveAble(false);
        Pin copy = (Pin) pin.copy();
        copy.setOut(!copy.isOut());
        owner.getAction().addPin(copy);
    }

    public FunctionInnerAction(JsonObject jsonObject) {
        super(jsonObject);
        tmpPins.forEach(super::addPin);
        tmpPins.clear();
    }

    @Override
    public String getDescription() {
        if (owner != null) return owner.getTitle();
        return super.getDescription();
    }

    @Override
    public void setDescription(String description) {
        if (owner != null) owner.setTitle(description);
        super.setDescription(description);
    }

    @Override
    public Pin addPin(Pin pin) {
        pin.setRemoveAble(true);
        return super.addPin(pin);
    }

    @Override
    public void onPinRemoved(Pin pin) {
        Pin pinByUid = getPinByUid(pin.getUid());
        if (pinByUid == null) return;
        removePin(pinByUid);
    }

    @Override
    public void onPinChanged(Pin pin) {
        Pin pinByUid = getPinByUid(pin.getUid());
        if (pinByUid == null) return;
        pinByUid.setValue(pin.getValue().copy());
        pinByUid.setTitle(pin.getTitle());
    }

    public void setOwner(Function owner) {
        this.owner = owner;
        Action action = owner.getAction();
        action.addListener(this);
    }

    public Pin getDefaultExecutePin() {
        ArrayList<Pin> pins = getPins();
        if (pins != null && !pins.isEmpty()) return pins.get(0);
        return null;
    }

    @Override
    public void resetReturnValue(Pin pin) {

    }

}
