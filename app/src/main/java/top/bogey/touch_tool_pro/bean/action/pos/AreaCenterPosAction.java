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

public class AreaCenterPosAction extends Action {
    private transient Pin posPin = new Pin(new PinPoint(), R.string.pin_point, true);
    private transient Pin areaPin = new Pin(new PinArea(), R.string.pin_area);

    public AreaCenterPosAction() {
        super(ActionType.AREA_CENTER_POS);
        posPin = addPin(posPin);
        areaPin = addPin(areaPin);
    }

    public AreaCenterPosAction(JsonObject jsonObject) {
        super(jsonObject);
        posPin = reAddPin(posPin);
        areaPin = reAddPin(areaPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinArea area = (PinArea) getPinValue(runnable, context, areaPin);
        Rect rect = area.getArea(MainApplication.getInstance());
        posPin.getValue(PinPoint.class).setPoint(MainApplication.getInstance(), rect.centerX(), rect.centerY());
    }
}
