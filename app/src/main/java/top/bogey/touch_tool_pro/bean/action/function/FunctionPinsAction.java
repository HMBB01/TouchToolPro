package top.bogey.touch_tool_pro.bean.action.function;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.Function;
import top.bogey.touch_tool_pro.bean.pin.Pin;

public class FunctionPinsAction extends Action {

    public FunctionPinsAction() {
        super(ActionType.CUSTOM_PIN);
    }

    public FunctionPinsAction(JsonObject jsonObject) {
        super(jsonObject);
        tmpPins.forEach(this::addPin);
        tmpPins.clear();
    }

    public void syncDefaultValue(Function function) {
        for (Action action : function.getActionsByClass(FunctionStartAction.class)) {
            FunctionStartAction startAction = (FunctionStartAction) action;
            startAction.getPins().forEach(pin -> {
                Pin pinByUid = getPinByUid(pin.getUid());
                pinByUid.setValue(pin.getValue().copy());
            });
            break;
        }
    }

    @Override
    public void resetReturnValue(Pin pin) {

    }
}
