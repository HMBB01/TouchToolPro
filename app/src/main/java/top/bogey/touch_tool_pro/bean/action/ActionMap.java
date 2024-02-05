package top.bogey.touch_tool_pro.bean.action;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;

public enum ActionMap {
    CUSTOM,
    VARIABLE,
    EXIST_CARD,

    START,
    LOGIC,
    EXECUTE,

    STRING,
    IMAGE,
    NODE,
    COLOR,
    BOOL,
    NUMBER,
    POINT,
    ARRAY,
    OTHER,
    ;

    public String getTitle() {
        String[] array = MainApplication.getInstance().getResources().getStringArray(R.array.action_type);
        return array[ordinal()];
    }

    @NonNull
    public ArrayList<ActionType> getTypes() {
        return switch (this) {
            case START -> new ArrayList<>(Arrays.asList(
                    ActionType.MANUAL_START,
                    ActionType.ENTER_APP_START,
                    ActionType.TIME_START,
                    ActionType.NOTIFY_START,
                    ActionType.NETWORK_START,
                    ActionType.SCREEN_START,
                    ActionType.BATTERY_START,
                    ActionType.OUTER_START
            ));

            case LOGIC -> new ArrayList<>(Arrays.asList(
                    ActionType.LOGIC_IF,
                    ActionType.LOGIC_WAIT_IF,
                    ActionType.LOGIC_FOR,
                    ActionType.LOGIC_WHILE,
                    ActionType.LOGIC_SEQUENCE,
                    ActionType.LOGIC_RANDOM,
                    ActionType.LOGIC_PARALLEL,
                    ActionType.LOGIC_MANUAL_CHOICE
            ));

            case EXECUTE -> new ArrayList<>(Arrays.asList(
                    ActionType.DELAY,
                    ActionType.LOG,
                    ActionType.CLICK_POSITION,
                    ActionType.CLICK_NODE,
                    ActionType.CLICK_KEY,
                    ActionType.TOUCH,
                    ActionType.INPUT,
                    ActionType.SCREEN_SWITCH,
                    ActionType.CAPTURE_SWITCH,
                    ActionType.OPEN_APP,
                    ActionType.OPEN_URI,
                    ActionType.PLAY_RINGTONE,
                    ActionType.STOP_RINGTONE,
                    ActionType.COPY,
                    ActionType.SNI_PASTE,
                    ActionType.SHARE,
                    ActionType.RUN_TASK,
                    ActionType.SHELL,
                    ActionType.BREAK_TASK
            ));

            case STRING -> new ArrayList<>(Arrays.asList(
                    ActionType.CHECK_EXIST_TEXT,
                    ActionType.CHECK_EXIST_TEXTS,
                    ActionType.CHECK_EXIST_TEXT_OCR,
                    ActionType.OCR_TEXT_STATE,

                    ActionType.STRING_FROM_VALUE,
                    ActionType.STRING_TO_INT,
                    ActionType.STRING_ADD,
                    ActionType.STRING_EQUAL,
                    ActionType.STRING_REGEX
            ));

            case IMAGE -> new ArrayList<>(Arrays.asList(
                    ActionType.CHECK_EXIST_IMAGE,
                    ActionType.CHECK_IMAGE,
                    ActionType.IMAGE_STATE,
                    ActionType.IMAGE_SUB_IMAGE
            ));

            case NODE -> new ArrayList<>(Arrays.asList(
                    ActionType.CHECK_EXIST_NODE,
                    ActionType.CHECK_EXIST_NODES,
                    ActionType.NODE_INFO_STATE,
                    ActionType.NODE_IS_VALID,
                    ActionType.NODE_CHILDREN,
                    ActionType.NODE_PARENT,
                    ActionType.NODES_IN_POS,
                    ActionType.NODES_IN_WINDOW
            ));

            case COLOR -> new ArrayList<>(Arrays.asList(
                    ActionType.CHECK_EXIST_COLOR,
                    ActionType.CHECK_EXIST_COLORS,
                    ActionType.CHECK_COLOR,
                    ActionType.COLOR_STATE
            ));

            case BOOL -> new ArrayList<>(Arrays.asList(
                    ActionType.BOOL_OR,
                    ActionType.BOOL_AND,
                    ActionType.BOOL_NOT
            ));

            case NUMBER -> new ArrayList<>(Arrays.asList(
                    ActionType.INT_ADD,
                    ActionType.INT_REDUCE,
                    ActionType.INT_MULTI,
                    ActionType.INT_DIV,
                    ActionType.INT_MOD,
                    ActionType.INT_EQUAL,
                    ActionType.INT_LARGE,
                    ActionType.INT_SMALL,
                    ActionType.INT_IN_AREA,
                    ActionType.INT_RANDOM,

                    ActionType.INT_TO_VALUE_AREA
            ));

            case POINT -> new ArrayList<>(Arrays.asList(
                    ActionType.POS_FROM_INT,
                    ActionType.POS_TO_INT,
                    ActionType.POS_OFFSET,
                    ActionType.POS_IN_AREA,
                    ActionType.POS_TO_AREA,
                    ActionType.POS_TO_TOUCH,

                    ActionType.AREA_TO_INT,
                    ActionType.AREA_PICK
            ));

            case ARRAY -> new ArrayList<>(Arrays.asList(
                    ActionType.ARRAY_FOR,
                    ActionType.ARRAY_MAKE,
                    ActionType.ARRAY_ADD,
                    ActionType.ARRAY_INSERT,
                    ActionType.ARRAY_REMOVE,
                    ActionType.ARRAY_GET,
                    ActionType.ARRAY_SET,
                    ActionType.ARRAY_CLEAR,
                    ActionType.ARRAY_SIZE,
                    ActionType.ARRAY_VALID_INDEX,
                    ActionType.ARRAY_APPEND,
                    ActionType.ARRAY_INDEX_OF
            ));

            case OTHER -> new ArrayList<>(Arrays.asList(
                    ActionType.CHECK_IN_APP,
                    ActionType.CHECK_ON_BATTERY_STATE,
                    ActionType.CHECK_ON_SCREEN_STATE,
                    ActionType.CHECK_NETWORK,
                    ActionType.APP_STATE,
                    ActionType.BATTERY_STATE,
                    ActionType.SCREEN_STATE,
                    ActionType.NETWORK_STATE,
                    ActionType.CAPTURE_STATE,
                    ActionType.DATE_STATE,
                    ActionType.TIME_STATE
            ));

            default -> new ArrayList<>();
        };
    }
}
