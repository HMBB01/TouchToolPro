package top.bogey.touch_tool_pro.bean.action;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinObject;

public interface ActionInterface {
    Pin addPin(Pin pin);

    Pin addPin(Pin pin, int index);

    Pin reAddPin(Pin def);

    Pin reAddPin(Pin def, Class<? extends PinObject>... classes);

    Pin reAddPin(Pin def, PinType type);

    ArrayList<Pin> reAddPin(Pin def, int remain);

    ArrayList<Pin> reAddPin(Pin def, int remain, PinType type);

    boolean removePin(Pin pin);

    boolean removePin(Pin pin, FunctionContext context);

    Pin getPinById(String id);

    Pin getPinByUid(String uid);

    Pin getFirstMatchedPin(PinObject pinObject, boolean out);

    ArrayList<Pin> getPins();

    int getX();

    void setX(int x);

    int getY();

    void setY(int y);

    boolean isExpand();

    void setExpand(boolean expand);

    boolean isShowHide();

    void setShowHide(boolean showHide);

    void addListener(ActionListener listener);

    void removeListener(ActionListener listener);

    ActionCheckResult check(FunctionContext context);

    boolean isError(FunctionContext context);

}
