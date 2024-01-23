package top.bogey.touch_tool_pro.bean.pin;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.pins.PinAdd;
import top.bogey.touch_tool_pro.bean.pin.pins.PinApplication;
import top.bogey.touch_tool_pro.bean.pin.pins.PinArea;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinColor;
import top.bogey.touch_tool_pro.bean.pin.pins.PinExecute;
import top.bogey.touch_tool_pro.bean.pin.pins.PinFloat;
import top.bogey.touch_tool_pro.bean.pin.pins.PinImage;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.pin.pins.PinLong;
import top.bogey.touch_tool_pro.bean.pin.pins.PinNode;
import top.bogey.touch_tool_pro.bean.pin.pins.PinNodePath;
import top.bogey.touch_tool_pro.bean.pin.pins.PinPoint;
import top.bogey.touch_tool_pro.bean.pin.pins.PinSpinner;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.pin.pins.PinTask;
import top.bogey.touch_tool_pro.bean.pin.pins.PinTouch;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArea;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArray;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetAdd;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetApp;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetArea;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetArray;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetBoolean;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetColor;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetExecute;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetFloat;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetImage;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetInteger;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetLong;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetNodePath;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetPoint;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetSpinner;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetString;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetTask;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetTouch;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetValueArea;

public enum PinType {
    OBJECT,

    EXECUTE,

    ADD,

    VALUE,
    VALUE_ARRAY,

    BOOLEAN,

    INT,
    VALUE_AREA,

    POINT,
    AREA,
    TOUCH,

    LONG,
    FLOAT,

    STRING,
    SPINNER,

    NODE,
    NODE_PATH,

    TASK,
    APP,

    IMAGE,
    COLOR;


    private final static PinConfigInfo EXECUTE_CONFIG = new PinConfigInfo(R.string.pin_execute, PinExecute.class, PinWidgetExecute.class, false);
    private final static PinConfigInfo ADD_CONFIG = new PinConfigInfo(R.string.pin_value, PinAdd.class, PinWidgetAdd.class, false);
    private final static PinConfigInfo VALUE_CONFIG = new PinConfigInfo(R.string.pin_value, PinValue.class, null);
    private final static PinConfigInfo VALUE_ARRAY_CONFIG = new PinConfigInfo(R.string.pin_value_array, PinValueArray.class, PinWidgetArray.class);
    private final static PinConfigInfo BOOLEAN_CONFIG = new PinConfigInfo(R.string.pin_boolean, PinBoolean.class, PinWidgetBoolean.class);
    private final static PinConfigInfo INT_CONFIG = new PinConfigInfo(R.string.pin_int, PinInteger.class, PinWidgetInteger.class);
    private final static PinConfigInfo VALUE_AREA_CONFIG = new PinConfigInfo(R.string.pin_value_area, PinValueArea.class, PinWidgetValueArea.class);
    private final static PinConfigInfo POINT_CONFIG = new PinConfigInfo(R.string.pin_point, PinPoint.class, PinWidgetPoint.class);
    private final static PinConfigInfo AREA_CONFIG = new PinConfigInfo(R.string.pin_area, PinArea.class, PinWidgetArea.class);
    private final static PinConfigInfo TOUCH_CONFIG = new PinConfigInfo(R.string.pin_touch, PinTouch.class, PinWidgetTouch.class);
    private final static PinConfigInfo LONG_CONFIG = new PinConfigInfo(R.string.pin_long, PinLong.class, PinWidgetLong.class);
    private final static PinConfigInfo FLOAT_CONFIG = new PinConfigInfo(R.string.pin_float, PinFloat.class, PinWidgetFloat.class);
    private final static PinConfigInfo STRING_CONFIG = new PinConfigInfo(R.string.pin_string, PinString.class, PinWidgetString.class);
    private final static PinConfigInfo SPINNER_CONFIG = new PinConfigInfo(R.string.pin_spinner, PinSpinner.class, PinWidgetSpinner.class);
    private final static PinConfigInfo NODE_CONFIG = new PinConfigInfo(R.string.pin_node, PinNode.class, null);
    private final static PinConfigInfo NODE_PATH_CONFIG = new PinConfigInfo(R.string.pin_node_path, PinNodePath.class, PinWidgetNodePath.class);
    private final static PinConfigInfo TASK_CONFIG = new PinConfigInfo(R.string.pin_task, PinTask.class, PinWidgetTask.class);
    private final static PinConfigInfo APP_CONFIG = new PinConfigInfo(R.string.pin_app, PinApplication.class, PinWidgetApp.class);
    private final static PinConfigInfo IMAGE_CONFIG = new PinConfigInfo(R.string.pin_image, PinImage.class, PinWidgetImage.class);
    private final static PinConfigInfo COLOR_CONFIG = new PinConfigInfo(R.string.pin_color, PinColor.class, PinWidgetColor.class);

    public PinConfigInfo getConfig() {
        return switch (this) {
            case EXECUTE -> EXECUTE_CONFIG;
            case ADD -> ADD_CONFIG;
            case VALUE -> VALUE_CONFIG;
            case VALUE_ARRAY -> VALUE_ARRAY_CONFIG;
            case BOOLEAN -> BOOLEAN_CONFIG;
            case INT -> INT_CONFIG;
            case VALUE_AREA -> VALUE_AREA_CONFIG;
            case POINT -> POINT_CONFIG;
            case AREA -> AREA_CONFIG;
            case TOUCH -> TOUCH_CONFIG;
            case LONG -> LONG_CONFIG;
            case FLOAT -> FLOAT_CONFIG;
            case STRING -> STRING_CONFIG;
            case SPINNER -> SPINNER_CONFIG;
            case NODE -> NODE_CONFIG;
            case NODE_PATH -> NODE_PATH_CONFIG;
            case TASK -> TASK_CONFIG;
            case APP -> APP_CONFIG;
            case IMAGE -> IMAGE_CONFIG;
            case COLOR -> COLOR_CONFIG;
            default -> new PinConfigInfo();
        };
    }
}
