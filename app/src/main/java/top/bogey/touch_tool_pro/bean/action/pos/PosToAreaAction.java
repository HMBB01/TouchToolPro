package top.bogey.touch_tool_pro.bean.action.pos;

import android.graphics.Rect;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinArea;
import top.bogey.touch_tool_pro.bean.pin.pins.PinPoint;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class PosToAreaAction extends Action {
    private transient Pin areaPin = new Pin(new PinArea(), R.string.pin_area, true);
    private transient Pin LTPin = new Pin(new PinPoint(), R.string.pin_point);
    private transient Pin RBPin = new Pin(new PinPoint(), R.string.pin_point);

    public PosToAreaAction() {
        super(ActionType.POS_TO_AREA);
        areaPin = addPin(areaPin);
        LTPin = addPin(LTPin);
        RBPin = addPin(RBPin);
    }

    public PosToAreaAction(JsonObject jsonObject) {
        super(jsonObject);
        areaPin = reAddPin(areaPin);
        LTPin = reAddPin(LTPin);
        RBPin = reAddPin(RBPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinArea area = areaPin.getValue(PinArea.class);

        PinPoint ltPos = (PinPoint) getPinValue(runnable, context, LTPin);
        PinPoint rbPos = (PinPoint) getPinValue(runnable, context, RBPin);

        MainApplication instance = MainApplication.getInstance();
        Rect rect = new Rect(ltPos.getX(instance), ltPos.getY(instance), rbPos.getX(instance), rbPos.getY(instance));
        rect.sort();
        area.setArea(instance, rect);
    }
}
