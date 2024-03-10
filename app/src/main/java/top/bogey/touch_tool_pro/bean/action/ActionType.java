package top.bogey.touch_tool_pro.bean.action;

import android.os.Build;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.array.ArrayAddAction;
import top.bogey.touch_tool_pro.bean.action.array.ArrayAppendAction;
import top.bogey.touch_tool_pro.bean.action.array.ArrayClearAction;
import top.bogey.touch_tool_pro.bean.action.array.ArrayForLogicAction;
import top.bogey.touch_tool_pro.bean.action.array.ArrayGetAction;
import top.bogey.touch_tool_pro.bean.action.array.ArrayIndexOfAction;
import top.bogey.touch_tool_pro.bean.action.array.ArrayInsertAction;
import top.bogey.touch_tool_pro.bean.action.array.ArrayMakeAction;
import top.bogey.touch_tool_pro.bean.action.array.ArrayRemoveAction;
import top.bogey.touch_tool_pro.bean.action.array.ArraySetAction;
import top.bogey.touch_tool_pro.bean.action.array.ArraySizeAction;
import top.bogey.touch_tool_pro.bean.action.array.ArrayValidIndexAction;
import top.bogey.touch_tool_pro.bean.action.bool.BoolAndAction;
import top.bogey.touch_tool_pro.bean.action.bool.BoolNotAction;
import top.bogey.touch_tool_pro.bean.action.bool.BoolOrAction;
import top.bogey.touch_tool_pro.bean.action.color.ColorEqualAction;
import top.bogey.touch_tool_pro.bean.action.color.ColorStateAction;
import top.bogey.touch_tool_pro.bean.action.color.ExistColorAction;
import top.bogey.touch_tool_pro.bean.action.color.ExistColorsAction;
import top.bogey.touch_tool_pro.bean.action.function.FunctionEndAction;
import top.bogey.touch_tool_pro.bean.action.function.FunctionPinsAction;
import top.bogey.touch_tool_pro.bean.action.function.FunctionReferenceAction;
import top.bogey.touch_tool_pro.bean.action.function.FunctionStartAction;
import top.bogey.touch_tool_pro.bean.action.image.ExistImageAction;
import top.bogey.touch_tool_pro.bean.action.image.ExistImagesAction;
import top.bogey.touch_tool_pro.bean.action.image.ImageContainAction;
import top.bogey.touch_tool_pro.bean.action.image.ImageStateAction;
import top.bogey.touch_tool_pro.bean.action.image.SaveImageAction;
import top.bogey.touch_tool_pro.bean.action.image.SubImageAction;
import top.bogey.touch_tool_pro.bean.action.logic.ForLogicAction;
import top.bogey.touch_tool_pro.bean.action.logic.IfLogicAction;
import top.bogey.touch_tool_pro.bean.action.logic.ManualChoiceAction;
import top.bogey.touch_tool_pro.bean.action.logic.ParallelAction;
import top.bogey.touch_tool_pro.bean.action.logic.RandomAction;
import top.bogey.touch_tool_pro.bean.action.logic.SequenceAction;
import top.bogey.touch_tool_pro.bean.action.logic.WaitIfLogicAction;
import top.bogey.touch_tool_pro.bean.action.logic.WhileLogicAction;
import top.bogey.touch_tool_pro.bean.action.node.ExistNodeAction;
import top.bogey.touch_tool_pro.bean.action.node.ExistNodesAction;
import top.bogey.touch_tool_pro.bean.action.node.GetNodeChildrenAction;
import top.bogey.touch_tool_pro.bean.action.node.GetNodeInfoStateAction;
import top.bogey.touch_tool_pro.bean.action.node.GetNodeParentAction;
import top.bogey.touch_tool_pro.bean.action.node.GetNodesInPosAction;
import top.bogey.touch_tool_pro.bean.action.node.GetNodesInWindowAction;
import top.bogey.touch_tool_pro.bean.action.node.IsNodeValidAction;
import top.bogey.touch_tool_pro.bean.action.normal.BreakTaskAction;
import top.bogey.touch_tool_pro.bean.action.normal.CaptureSwitchAction;
import top.bogey.touch_tool_pro.bean.action.normal.ClickKeyAction;
import top.bogey.touch_tool_pro.bean.action.normal.ClickNodeAction;
import top.bogey.touch_tool_pro.bean.action.normal.ClickPositionAction;
import top.bogey.touch_tool_pro.bean.action.normal.CopyToClipboardAction;
import top.bogey.touch_tool_pro.bean.action.normal.DelayAction;
import top.bogey.touch_tool_pro.bean.action.normal.InputAction;
import top.bogey.touch_tool_pro.bean.action.normal.LogAction;
import top.bogey.touch_tool_pro.bean.action.normal.OpenAppAction;
import top.bogey.touch_tool_pro.bean.action.normal.OpenUriAction;
import top.bogey.touch_tool_pro.bean.action.normal.PlayRingtoneAction;
import top.bogey.touch_tool_pro.bean.action.normal.RunTaskAction;
import top.bogey.touch_tool_pro.bean.action.normal.ScreenSwitchAction;
import top.bogey.touch_tool_pro.bean.action.normal.ShareAction;
import top.bogey.touch_tool_pro.bean.action.normal.ShellAction;
import top.bogey.touch_tool_pro.bean.action.normal.SniPasteAction;
import top.bogey.touch_tool_pro.bean.action.normal.StopRingtoneAction;
import top.bogey.touch_tool_pro.bean.action.normal.TouchAction;
import top.bogey.touch_tool_pro.bean.action.number.FloatToIntAction;
import top.bogey.touch_tool_pro.bean.action.number.IntAddAction;
import top.bogey.touch_tool_pro.bean.action.number.IntDivAction;
import top.bogey.touch_tool_pro.bean.action.number.IntEqualAction;
import top.bogey.touch_tool_pro.bean.action.number.IntInAreaAction;
import top.bogey.touch_tool_pro.bean.action.number.IntLargeAction;
import top.bogey.touch_tool_pro.bean.action.number.IntModAction;
import top.bogey.touch_tool_pro.bean.action.number.IntMultiAction;
import top.bogey.touch_tool_pro.bean.action.number.IntRandomAction;
import top.bogey.touch_tool_pro.bean.action.number.IntReduceAction;
import top.bogey.touch_tool_pro.bean.action.number.IntSmallAction;
import top.bogey.touch_tool_pro.bean.action.number.IntToValueAreaAction;
import top.bogey.touch_tool_pro.bean.action.number.MathExpressionAction;
import top.bogey.touch_tool_pro.bean.action.other.AppStateAction;
import top.bogey.touch_tool_pro.bean.action.other.BatteryStateAction;
import top.bogey.touch_tool_pro.bean.action.other.BluetoothStateAction;
import top.bogey.touch_tool_pro.bean.action.other.CaptureStateAction;
import top.bogey.touch_tool_pro.bean.action.other.DateStateAction;
import top.bogey.touch_tool_pro.bean.action.other.InAppCheckAction;
import top.bogey.touch_tool_pro.bean.action.other.NetworkCheckAction;
import top.bogey.touch_tool_pro.bean.action.other.NetworkStateAction;
import top.bogey.touch_tool_pro.bean.action.other.OnBatteryStateAction;
import top.bogey.touch_tool_pro.bean.action.other.OnScreenStateAction;
import top.bogey.touch_tool_pro.bean.action.other.ScreenStateAction;
import top.bogey.touch_tool_pro.bean.action.other.TimeStateAction;
import top.bogey.touch_tool_pro.bean.action.pos.AreaCenterPosAction;
import top.bogey.touch_tool_pro.bean.action.pos.AreaPickAction;
import top.bogey.touch_tool_pro.bean.action.pos.AreaToIntAction;
import top.bogey.touch_tool_pro.bean.action.pos.PosFromIntAction;
import top.bogey.touch_tool_pro.bean.action.pos.PosInAreaAction;
import top.bogey.touch_tool_pro.bean.action.pos.PosOffsetAction;
import top.bogey.touch_tool_pro.bean.action.pos.PosToAreaAction;
import top.bogey.touch_tool_pro.bean.action.pos.PosToIntAction;
import top.bogey.touch_tool_pro.bean.action.pos.PosToTouchAction;
import top.bogey.touch_tool_pro.bean.action.start.AppStartAction;
import top.bogey.touch_tool_pro.bean.action.start.BatteryStartAction;
import top.bogey.touch_tool_pro.bean.action.start.BluetoothStartAction;
import top.bogey.touch_tool_pro.bean.action.start.ManualStartAction;
import top.bogey.touch_tool_pro.bean.action.start.NetworkStartAction;
import top.bogey.touch_tool_pro.bean.action.start.NormalStartAction;
import top.bogey.touch_tool_pro.bean.action.start.NotifyStartAction;
import top.bogey.touch_tool_pro.bean.action.start.OuterStartAction;
import top.bogey.touch_tool_pro.bean.action.start.ScreenStartAction;
import top.bogey.touch_tool_pro.bean.action.start.TimeStartAction;
import top.bogey.touch_tool_pro.bean.action.string.ExistTextAction;
import top.bogey.touch_tool_pro.bean.action.string.ExistTextOcrAction;
import top.bogey.touch_tool_pro.bean.action.string.ExistTextsAction;
import top.bogey.touch_tool_pro.bean.action.string.OcrTextStateAction;
import top.bogey.touch_tool_pro.bean.action.string.StringAddAction;
import top.bogey.touch_tool_pro.bean.action.string.StringEqualAction;
import top.bogey.touch_tool_pro.bean.action.string.StringFromValueAction;
import top.bogey.touch_tool_pro.bean.action.string.StringRegexAction;
import top.bogey.touch_tool_pro.bean.action.string.StringToIntAction;
import top.bogey.touch_tool_pro.bean.action.var.GetCommonVariableValue;
import top.bogey.touch_tool_pro.bean.action.var.GetLocalVariableValue;
import top.bogey.touch_tool_pro.bean.action.var.SetCommonVariableValue;
import top.bogey.touch_tool_pro.bean.action.var.SetLocalVariableValue;
import top.bogey.touch_tool_pro.utils.super_user.SuperUser;
import top.bogey.touch_tool_pro.utils.SettingSave;
import top.bogey.touch_tool_pro.utils.ocr.Predictor;

public enum ActionType {
    BASE,

    CUSTOM,
    CUSTOM_START,
    CUSTOM_END,
    CUSTOM_PIN,

    VAR_GET,
    VAR_SET,
    COMMON_VAR_GET,
    COMMON_VAR_SET,

    MANUAL_START,
    ENTER_APP_START,
    TIME_START,
    NOTIFY_START,
    NETWORK_START,
    BATTERY_START,
    SCREEN_START,
    BLUETOOTH_START,
    OUTER_START,
    NORMAL_START,
    INNER_START,

    LOGIC_IF,
    LOGIC_WAIT_IF,
    LOGIC_FOR,
    LOGIC_WHILE,
    LOGIC_SEQUENCE,
    LOGIC_RANDOM,
    LOGIC_PARALLEL,
    LOGIC_MANUAL_CHOICE,

    DELAY,
    LOG,
    CLICK_POSITION,
    CLICK_NODE,
    CLICK_KEY,
    TOUCH,
    INPUT,
    SCREEN_SWITCH,
    CAPTURE_SWITCH,
    OPEN_APP,
    OPEN_URI,
    PLAY_RINGTONE,
    STOP_RINGTONE,
    COPY,
    SNI_PASTE,
    SHARE,
    RUN_TASK,
    SHELL,
    BREAK_TASK,

    CHECK_EXIST_TEXT,
    CHECK_EXIST_TEXTS,
    CHECK_EXIST_TEXT_OCR,
    OCR_TEXT_STATE,

    STRING_FROM_VALUE,
    STRING_TO_INT,
    STRING_ADD,
    STRING_EQUAL,
    STRING_REGEX,

    CHECK_EXIST_IMAGE,
    CHECK_EXIST_IMAGES,
    CHECK_IMAGE,
    IMAGE_STATE,
    IMAGE_SUB_IMAGE,
    SAVE_IMAGE,

    CHECK_EXIST_NODE,
    CHECK_EXIST_NODES,
    NODE_INFO_STATE,
    NODE_IS_VALID,
    NODE_CHILDREN,
    NODE_PARENT,
    NODES_IN_POS,
    NODES_IN_WINDOW,

    CHECK_EXIST_COLOR,
    CHECK_EXIST_COLORS,
    CHECK_COLOR,
    COLOR_STATE,

    BOOL_OR,
    BOOL_AND,
    BOOL_NOT,

    INT_ADD,
    INT_REDUCE,
    INT_MULTI,
    INT_DIV,
    INT_MOD,
    INT_EQUAL,
    INT_LARGE,
    INT_SMALL,
    INT_IN_AREA,
    INT_RANDOM,
    INT_TO_VALUE_AREA,

    MATH_EXPRESSION,
    FLOAT_TO_INT,


    POS_FROM_INT,
    POS_TO_INT,
    POS_OFFSET,
    POS_IN_AREA,
    POS_TO_AREA,
    POS_TO_TOUCH,

    AREA_TO_INT,
    AREA_PICK,
    AREA_CENTER_POS,

    ARRAY_GET,
    ARRAY_SET,
    ARRAY_MAKE,
    ARRAY_ADD,
    ARRAY_INSERT,
    ARRAY_REMOVE,
    ARRAY_CLEAR,
    ARRAY_SIZE,
    ARRAY_VALID_INDEX,
    ARRAY_APPEND,
    ARRAY_INDEX_OF,
    ARRAY_FOR,

    CHECK_IN_APP,
    CHECK_ON_BATTERY_STATE,
    CHECK_ON_SCREEN_STATE,
    CHECK_NETWORK,
    APP_STATE,
    BATTERY_STATE,
    SCREEN_STATE,
    NETWORK_STATE,
    BLUETOOTH_STATE,
    CAPTURE_STATE,
    DATE_STATE,
    TIME_STATE,
    ;

    private final static ActionConfigInfo CUSTOM_CONFIG = new ActionConfigInfo(CUSTOM, 0, 0, FunctionReferenceAction.class);
    private final static ActionConfigInfo CUSTOM_PIN_CONFIG = new ActionConfigInfo(CUSTOM_PIN, 0, 0, FunctionPinsAction.class);
    private final static ActionConfigInfo CUSTOM_START_CONFIG = new ActionConfigInfo(CUSTOM_START, R.string.function_start, 0, FunctionStartAction.class);
    private final static ActionConfigInfo CUSTOM_END_CONFIG = new ActionConfigInfo(CUSTOM_END, R.string.function_end, 0, FunctionEndAction.class);

    private final static ActionConfigInfo VAR_GET_CONFIG = new ActionConfigInfo(VAR_GET, R.string.action_get_value_action_title, R.drawable.icon_get_value, GetLocalVariableValue.class);
    private final static ActionConfigInfo VAR_SET_CONFIG = new ActionConfigInfo(VAR_SET, R.string.action_set_value_action_title, R.drawable.icon_set_value, SetLocalVariableValue.class);
    private final static ActionConfigInfo COMMON_VAR_GET_CONFIG = new ActionConfigInfo(COMMON_VAR_GET, R.string.action_get_common_value_action_title, R.drawable.icon_get_value, GetCommonVariableValue.class);
    private final static ActionConfigInfo COMMON_VAR_SET_CONFIG = new ActionConfigInfo(COMMON_VAR_SET, R.string.action_set_common_value_action_title, R.drawable.icon_set_value, SetCommonVariableValue.class);

    private final static ActionConfigInfo MANUAL_START_CONFIG = new ActionConfigInfo(MANUAL_START, R.string.action_manual_start_title, R.string.MANUAL_START, R.drawable.icon_hand, ManualStartAction.class);
    private final static ActionConfigInfo ENTER_APP_START_CONFIG = new ActionConfigInfo(ENTER_APP_START, R.string.action_app_start_title, R.string.ENTER_APP_START, R.drawable.icon_package_info, AppStartAction.class);
    private final static ActionConfigInfo TIME_START_CONFIG = new ActionConfigInfo(TIME_START, R.string.action_time_start_title, R.string.TIME_START, R.drawable.icon_time, TimeStartAction.class) {
        @Override
        public boolean isValid() {
            return SettingSave.getInstance().isUseExactAlarm();
        }
    };
    private final static ActionConfigInfo NOTIFY_START_CONFIG = new ActionConfigInfo(NOTIFY_START, R.string.action_notification_start_title, R.string.NOTIFY_START, R.drawable.icon_notification, NotifyStartAction.class);
    private final static ActionConfigInfo NETWORK_START_CONFIG = new ActionConfigInfo(NETWORK_START, R.string.action_network_start_title, R.string.NETWORK_START, R.drawable.icon_network, NetworkStartAction.class);
    private final static ActionConfigInfo SCREEN_START_CONFIG = new ActionConfigInfo(SCREEN_START, R.string.action_screen_start_title, R.string.SCREEN_START, R.drawable.icon_screen, ScreenStartAction.class);
    private final static ActionConfigInfo BATTERY_START_CONFIG = new ActionConfigInfo(BATTERY_START, R.string.action_battery_start_title, R.string.BATTERY_START, R.drawable.icon_battery, BatteryStartAction.class);
    private final static ActionConfigInfo BLUETOOTH_START_CONFIG = new ActionConfigInfo(BLUETOOTH_START, R.string.action_bluetooth_start_title, R.drawable.icon_bluetooth, BluetoothStartAction.class) {
        @Override
        public boolean isValid() {
            return SettingSave.getInstance().getUseBluetooth(MainApplication.getInstance());
        }
    };
    private final static ActionConfigInfo OUTER_START_CONFIG = new ActionConfigInfo(OUTER_START, R.string.action_outer_start_title, R.string.OUTER_START, R.drawable.icon_auto_start, OuterStartAction.class);
    private final static ActionConfigInfo NORMAL_START_CONFIG = new ActionConfigInfo(NORMAL_START, R.string.action_normal_start_title, 0, NormalStartAction.class);

    private final static ActionConfigInfo LOGIC_IF_CONFIG = new ActionConfigInfo(LOGIC_IF, R.string.action_condition_logic_title, R.string.LOGIC_IF, R.drawable.icon_condition, IfLogicAction.class);
    private final static ActionConfigInfo LOGIC_WAIT_IF_CONFIG = new ActionConfigInfo(LOGIC_WAIT_IF, R.string.action_wait_condition_logic_title, R.drawable.icon_wait_condition, WaitIfLogicAction.class);
    private final static ActionConfigInfo LOGIC_FOR_CONFIG = new ActionConfigInfo(LOGIC_FOR, R.string.action_for_loop_logic_title, R.drawable.icon_for_loop, ForLogicAction.class);
    private final static ActionConfigInfo LOGIC_WHILE_CONFIG = new ActionConfigInfo(LOGIC_WHILE, R.string.action_condition_while_logic_title, R.drawable.icon_condition_while, WhileLogicAction.class);
    private final static ActionConfigInfo LOGIC_SEQUENCE_CONFIG = new ActionConfigInfo(LOGIC_SEQUENCE, R.string.action_sequence_logic_title, R.drawable.icon_sequence, SequenceAction.class);
    private final static ActionConfigInfo LOGIC_RANDOM_CONFIG = new ActionConfigInfo(LOGIC_RANDOM, R.string.action_random_logic_title, R.drawable.icon_random, RandomAction.class);
    private final static ActionConfigInfo LOGIC_PARALLEL_CONFIG = new ActionConfigInfo(LOGIC_PARALLEL, R.string.action_parallel_logic_title, R.drawable.icon_parallel, ParallelAction.class);
    private final static ActionConfigInfo LOGIC_MANUAL_CHOICE_CONFIG = new ActionConfigInfo(LOGIC_MANUAL_CHOICE, R.string.action_manual_choice_logic_title, R.drawable.icon_condition, ManualChoiceAction.class);

    private final static ActionConfigInfo DELAY_CONFIG = new ActionConfigInfo(DELAY, R.string.action_delay_action_title, R.drawable.icon_delay, DelayAction.class);
    private final static ActionConfigInfo LOG_CONFIG = new ActionConfigInfo(LOG, R.string.action_log_action_title, R.drawable.icon_log, LogAction.class);
    private final static ActionConfigInfo CLICK_POSITION_CONFIG = new ActionConfigInfo(CLICK_POSITION, R.string.action_touch_pos_action_title, R.drawable.icon_position, ClickPositionAction.class);
    private final static ActionConfigInfo CLICK_NODE_CONFIG = new ActionConfigInfo(CLICK_NODE, R.string.action_touch_node_action_title, R.drawable.icon_widget, ClickNodeAction.class);
    private final static ActionConfigInfo CLICK_KEY_CONFIG = new ActionConfigInfo(CLICK_KEY, R.string.action_system_ability_action_title, R.drawable.icon_screen, ClickKeyAction.class);
    private final static ActionConfigInfo TOUCH_CONFIG = new ActionConfigInfo(TOUCH, R.string.action_touch_path_action_title, R.drawable.icon_path, TouchAction.class);
    private final static ActionConfigInfo INPUT_CONFIG = new ActionConfigInfo(INPUT, R.string.action_input_node_action_title, R.drawable.icon_input, InputAction.class);
    private final static ActionConfigInfo SCREEN_SWITCH_CONFIG = new ActionConfigInfo(SCREEN_SWITCH, R.string.action_screen_action_title, R.drawable.icon_screen, ScreenSwitchAction.class);
    private final static ActionConfigInfo CAPTURE_SWITCH_CONFIG = new ActionConfigInfo(CAPTURE_SWITCH, R.string.action_open_capture_action_title, R.drawable.icon_capture, CaptureSwitchAction.class);
    private final static ActionConfigInfo OPEN_APP_CONFIG = new ActionConfigInfo(OPEN_APP, R.string.action_open_app_action_title, R.drawable.icon_package_info, OpenAppAction.class);
    private final static ActionConfigInfo OPEN_URI_CONFIG = new ActionConfigInfo(OPEN_URI, R.string.action_open_url_action_title, R.drawable.icon_uri, OpenUriAction.class);
    private final static ActionConfigInfo PLAY_RINGTONE_CONFIG = new ActionConfigInfo(PLAY_RINGTONE, R.string.action_play_ringtone_action_title, R.drawable.icon_notification, PlayRingtoneAction.class);
    private final static ActionConfigInfo STOP_RINGTONE_CONFIG = new ActionConfigInfo(STOP_RINGTONE, R.string.action_stop_ringtone_action_title, R.drawable.icon_notification, StopRingtoneAction.class);
    private final static ActionConfigInfo COPY_CONFIG = new ActionConfigInfo(COPY, R.string.action_copy_action_title, R.drawable.icon_copy, CopyToClipboardAction.class);
    private final static ActionConfigInfo SNI_PASTE_CONFIG = new ActionConfigInfo(SNI_PASTE, R.string.action_sni_paste_action_title, R.drawable.icon_home, SniPasteAction.class);
    private final static ActionConfigInfo SHARE_CONFIG = new ActionConfigInfo(SHARE, R.string.action_share_action_title, R.drawable.icon_export, ShareAction.class);
    private final static ActionConfigInfo RUN_TASK_CONFIG = new ActionConfigInfo(RUN_TASK, R.string.action_do_task_action_title, R.drawable.icon_task, RunTaskAction.class);
    private final static ActionConfigInfo SHELL_CONFIG = new ActionConfigInfo(SHELL, R.string.action_shell_action_title, R.drawable.icon_adb, ShellAction.class) {
        @Override
        public boolean isValid() {
            return SuperUser.isSuperUser();
        }
    };
    private final static ActionConfigInfo BREAK_TASK_CONFIG = new ActionConfigInfo(BREAK_TASK, R.string.action_break_task_action_title, R.drawable.icon_stop, BreakTaskAction.class);

    private final static ActionConfigInfo CHECK_EXIST_TEXT_CONFIG = new ActionConfigInfo(CHECK_EXIST_TEXT, R.string.action_exist_text_check_title, R.drawable.icon_text, ExistTextAction.class);
    private final static ActionConfigInfo CHECK_EXIST_TEXTS_CONFIG = new ActionConfigInfo(CHECK_EXIST_TEXTS, R.string.action_exist_texts_check_title, R.drawable.icon_text, ExistTextsAction.class);
    private final static ActionConfigInfo CHECK_EXIST_TEXT_OCR_CONFIG = new ActionConfigInfo(CHECK_EXIST_TEXT_OCR, R.string.action_exist_text_ocr_check_title, R.drawable.icon_text, ExistTextOcrAction.class) {
        @Override
        public boolean isValid() {
            return Predictor.ocrReady();
        }
    };
    private final static ActionConfigInfo OCR_TEXT_STATE_CONFIG = new ActionConfigInfo(OCR_TEXT_STATE, R.string.action_ocr_text_state_title, R.drawable.icon_text, OcrTextStateAction.class) {
        @Override
        public boolean isValid() {
            return Predictor.ocrReady();
        }
    };

    private final static ActionConfigInfo STRING_FROM_VALUE_CONFIG = new ActionConfigInfo(STRING_FROM_VALUE, R.string.action_string_from_value_title, R.drawable.icon_text, StringFromValueAction.class);
    private final static ActionConfigInfo STRING_TO_INT_CONFIG = new ActionConfigInfo(STRING_TO_INT, R.string.action_string_to_int_title, R.drawable.icon_text, StringToIntAction.class);
    private final static ActionConfigInfo STRING_ADD_CONFIG = new ActionConfigInfo(STRING_ADD, R.string.action_string_add_title, R.drawable.icon_text, StringAddAction.class);
    private final static ActionConfigInfo STRING_EQUAL_CONFIG = new ActionConfigInfo(STRING_EQUAL, R.string.action_string_equal_title, R.drawable.icon_text, StringEqualAction.class);
    private final static ActionConfigInfo STRING_REGEX_CONFIG = new ActionConfigInfo(STRING_REGEX, R.string.action_string_regex_title, R.drawable.icon_text, StringRegexAction.class);

    private final static ActionConfigInfo CHECK_EXIST_IMAGE_CONFIG = new ActionConfigInfo(CHECK_EXIST_IMAGE, R.string.action_exist_image_check_title, R.drawable.icon_image, ExistImageAction.class);
    private final static ActionConfigInfo CHECK_EXIST_IMAGES_CONFIG = new ActionConfigInfo(CHECK_EXIST_IMAGES, R.string.action_exist_images_check_title, R.drawable.icon_image, ExistImagesAction.class);
    private final static ActionConfigInfo CHECK_IMAGE_CONFIG = new ActionConfigInfo(CHECK_IMAGE, R.string.action_image_check_title, R.drawable.icon_image, ImageContainAction.class);
    private final static ActionConfigInfo IMAGE_STATE_CONFIG = new ActionConfigInfo(IMAGE_STATE, R.string.action_image_state_title, R.drawable.icon_image, ImageStateAction.class);
    private final static ActionConfigInfo IMAGE_SUB_IMAGE_CONFIG = new ActionConfigInfo(IMAGE_SUB_IMAGE, R.string.action_image_sub_image_title, R.drawable.icon_image, SubImageAction.class);
    private final static ActionConfigInfo SAVE_IMAGE_CONFIG = new ActionConfigInfo(SAVE_IMAGE, R.string.action_save_image_title, R.drawable.icon_image, SaveImageAction.class) {
        @Override
        public boolean isValid() {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
        }
    };

    private final static ActionConfigInfo CHECK_EXIST_NODE_CONFIG = new ActionConfigInfo(CHECK_EXIST_NODE, R.string.action_exist_node_check_title, R.drawable.icon_widget, ExistNodeAction.class);
    private final static ActionConfigInfo CHECK_EXIST_NODES_CONFIG = new ActionConfigInfo(CHECK_EXIST_NODES, R.string.action_exist_nodes_check_title, R.drawable.icon_widget, ExistNodesAction.class);
    private final static ActionConfigInfo NODE_INFO_STATE_CONFIG = new ActionConfigInfo(NODE_INFO_STATE, R.string.action_get_node_info_title, R.drawable.icon_widget, GetNodeInfoStateAction.class);
    private final static ActionConfigInfo NODE_IS_VALID_CONFIG = new ActionConfigInfo(NODE_IS_VALID, R.string.action_is_node_valid_title, R.drawable.icon_widget, IsNodeValidAction.class);
    private final static ActionConfigInfo NODE_CHILDREN_CONFIG = new ActionConfigInfo(NODE_CHILDREN, R.string.action_get_node_children_title, R.drawable.icon_widget, GetNodeChildrenAction.class);
    private final static ActionConfigInfo NODE_PARENT_CONFIG = new ActionConfigInfo(NODE_PARENT, R.string.action_get_node_parent_title, R.drawable.icon_widget, GetNodeParentAction.class);
    private final static ActionConfigInfo NODES_IN_POS_CONFIG = new ActionConfigInfo(NODES_IN_POS, R.string.action_get_nodes_in_pos_title, R.drawable.icon_widget, GetNodesInPosAction.class);
    private final static ActionConfigInfo NODES_IN_WINDOW_CONFIG = new ActionConfigInfo(NODES_IN_WINDOW, R.string.action_get_nodes_in_window_title, R.drawable.icon_widget, GetNodesInWindowAction.class);

    private final static ActionConfigInfo CHECK_EXIST_COLOR_CONFIG = new ActionConfigInfo(CHECK_EXIST_COLOR, R.string.action_exist_color_check_title, R.drawable.icon_color, ExistColorAction.class);
    private final static ActionConfigInfo CHECK_EXIST_COLORS_CONFIG = new ActionConfigInfo(CHECK_EXIST_COLORS, R.string.action_exist_colors_check_title, R.drawable.icon_color, ExistColorsAction.class);
    private final static ActionConfigInfo CHECK_COLOR_CONFIG = new ActionConfigInfo(CHECK_COLOR, R.string.action_color_check_title, R.drawable.icon_color, ColorEqualAction.class);
    private final static ActionConfigInfo COLOR_STATE_CONFIG = new ActionConfigInfo(COLOR_STATE, R.string.action_color_state_title, R.drawable.icon_color, ColorStateAction.class);

    private final static ActionConfigInfo BOOL_OR_CONFIG = new ActionConfigInfo(BOOL_OR, R.string.action_bool_convert_or_title, R.drawable.icon_condition, BoolOrAction.class);
    private final static ActionConfigInfo BOOL_AND_CONFIG = new ActionConfigInfo(BOOL_AND, R.string.action_bool_convert_and_title, R.drawable.icon_condition, BoolAndAction.class);
    private final static ActionConfigInfo BOOL_NOT_CONFIG = new ActionConfigInfo(BOOL_NOT, R.string.action_bool_convert_not_title, R.drawable.icon_condition, BoolNotAction.class);

    private final static ActionConfigInfo INT_ADD_CONFIG = new ActionConfigInfo(INT_ADD, R.string.action_int_add_title, R.drawable.icon_number, IntAddAction.class);
    private final static ActionConfigInfo INT_REDUCE_CONFIG = new ActionConfigInfo(INT_REDUCE, R.string.action_int_reduce_title, R.drawable.icon_number, IntReduceAction.class);
    private final static ActionConfigInfo INT_MULTI_CONFIG = new ActionConfigInfo(INT_MULTI, R.string.action_int_multi_title, R.drawable.icon_number, IntMultiAction.class);
    private final static ActionConfigInfo INT_DIV_CONFIG = new ActionConfigInfo(INT_DIV, R.string.action_int_div_title, R.drawable.icon_number, IntDivAction.class);
    private final static ActionConfigInfo INT_MOD_CONFIG = new ActionConfigInfo(INT_MOD, R.string.action_int_mod_title, R.drawable.icon_number, IntModAction.class);
    private final static ActionConfigInfo INT_EQUAL_CONFIG = new ActionConfigInfo(INT_EQUAL, R.string.action_int_equal_title, R.drawable.icon_number, IntEqualAction.class);
    private final static ActionConfigInfo INT_LARGE_CONFIG = new ActionConfigInfo(INT_LARGE, R.string.action_int_large_title, R.drawable.icon_number, IntLargeAction.class);
    private final static ActionConfigInfo INT_SMALL_CONFIG = new ActionConfigInfo(INT_SMALL, R.string.action_int_small_title, R.drawable.icon_number, IntSmallAction.class);
    private final static ActionConfigInfo INT_IN_AREA_CONFIG = new ActionConfigInfo(INT_IN_AREA, R.string.action_int_in_area_title, R.drawable.icon_number, IntInAreaAction.class);
    private final static ActionConfigInfo INT_RANDOM_CONFIG = new ActionConfigInfo(INT_RANDOM, R.string.action_int_random_title, R.drawable.icon_number, IntRandomAction.class);
    private final static ActionConfigInfo INT_TO_VALUE_AREA_CONFIG = new ActionConfigInfo(INT_TO_VALUE_AREA, R.string.action_int_to_value_area_title, R.drawable.icon_number, IntToValueAreaAction.class);

    private final static ActionConfigInfo MATH_EXPRESSION_CONFIG = new ActionConfigInfo(MATH_EXPRESSION, R.string.action_math_expression_title, R.drawable.icon_number, MathExpressionAction.class);
    private final static ActionConfigInfo FLOAT_TO_INT_CONFIG = new ActionConfigInfo(FLOAT_TO_INT, R.string.action_float_to_int_title, R.drawable.icon_number, FloatToIntAction.class);

    private final static ActionConfigInfo POS_FROM_INT_CONFIG = new ActionConfigInfo(POS_FROM_INT, R.string.action_position_from_int_title, R.drawable.icon_position, PosFromIntAction.class);
    private final static ActionConfigInfo POS_TO_INT_CONFIG = new ActionConfigInfo(POS_TO_INT, R.string.action_position_to_int_title, R.drawable.icon_position, PosToIntAction.class);
    private final static ActionConfigInfo POS_OFFSET_CONFIG = new ActionConfigInfo(POS_OFFSET, R.string.action_position_offset_title, R.drawable.icon_position, PosOffsetAction.class);
    private final static ActionConfigInfo POS_IN_AREA_CONFIG = new ActionConfigInfo(POS_IN_AREA, R.string.action_position_in_area_title, R.drawable.icon_position, PosInAreaAction.class);
    private final static ActionConfigInfo POS_TO_AREA_CONFIG = new ActionConfigInfo(POS_TO_AREA, R.string.action_position_to_area_title, R.drawable.icon_position, PosToAreaAction.class);
    private final static ActionConfigInfo POS_TO_TOUCH_CONFIG = new ActionConfigInfo(POS_TO_TOUCH, R.string.action_position_to_touch_title, R.drawable.icon_position, PosToTouchAction.class);

    private final static ActionConfigInfo AREA_TO_INT_CONFIG = new ActionConfigInfo(AREA_TO_INT, R.string.action_area_to_int_title, R.drawable.icon_position, AreaToIntAction.class);
    private final static ActionConfigInfo AREA_PICK_CONFIG = new ActionConfigInfo(AREA_PICK, R.string.action_area_pick_title, R.drawable.icon_position, AreaPickAction.class);
    private final static ActionConfigInfo AREA_CENTER_POS_CONFIG = new ActionConfigInfo(AREA_CENTER_POS, R.string.action_area_center_pos_title, R.drawable.icon_position, AreaCenterPosAction.class);

    private final static ActionConfigInfo ARRAY_GET_CONFIG = new ActionConfigInfo(ARRAY_GET, R.string.action_array_get_title, R.drawable.icon_array, ArrayGetAction.class);
    private final static ActionConfigInfo ARRAY_SET_CONFIG = new ActionConfigInfo(ARRAY_SET, R.string.action_array_set_title, R.drawable.icon_array, ArraySetAction.class);
    private final static ActionConfigInfo ARRAY_MAKE_CONFIG = new ActionConfigInfo(ARRAY_MAKE, R.string.action_array_make_title, R.drawable.icon_array, ArrayMakeAction.class);
    private final static ActionConfigInfo ARRAY_ADD_CONFIG = new ActionConfigInfo(ARRAY_ADD, R.string.action_array_add_title, R.drawable.icon_array, ArrayAddAction.class);
    private final static ActionConfigInfo ARRAY_INSERT_CONFIG = new ActionConfigInfo(ARRAY_INSERT, R.string.action_array_insert_title, R.drawable.icon_array, ArrayInsertAction.class);
    private final static ActionConfigInfo ARRAY_REMOVE_CONFIG = new ActionConfigInfo(ARRAY_REMOVE, R.string.action_array_remove_title, R.drawable.icon_array, ArrayRemoveAction.class);
    private final static ActionConfigInfo ARRAY_CLEAR_CONFIG = new ActionConfigInfo(ARRAY_CLEAR, R.string.action_array_clear_title, R.drawable.icon_array, ArrayClearAction.class);
    private final static ActionConfigInfo ARRAY_SIZE_CONFIG = new ActionConfigInfo(ARRAY_SIZE, R.string.action_array_size_title, R.drawable.icon_array, ArraySizeAction.class);
    private final static ActionConfigInfo ARRAY_VALID_INDEX_CONFIG = new ActionConfigInfo(ARRAY_VALID_INDEX, R.string.action_array_valid_index_title, R.drawable.icon_array, ArrayValidIndexAction.class);
    private final static ActionConfigInfo ARRAY_APPEND_CONFIG = new ActionConfigInfo(ARRAY_APPEND, R.string.action_array_append_title, R.drawable.icon_array, ArrayAppendAction.class);
    private final static ActionConfigInfo ARRAY_INDEX_OF_CONFIG = new ActionConfigInfo(ARRAY_INDEX_OF, R.string.action_array_index_of_title, R.drawable.icon_array, ArrayIndexOfAction.class);
    private final static ActionConfigInfo ARRAY_FOR_CONFIG = new ActionConfigInfo(ARRAY_FOR, R.string.action_array_for_title, R.drawable.icon_array, ArrayForLogicAction.class);

    private final static ActionConfigInfo CHECK_IN_APP_CONFIG = new ActionConfigInfo(CHECK_IN_APP, R.string.action_in_app_check_title, R.drawable.icon_package_info, InAppCheckAction.class);
    private final static ActionConfigInfo CHECK_ON_BATTERY_STATE_CONFIG = new ActionConfigInfo(CHECK_ON_BATTERY_STATE, R.string.action_battery_state_check_title, R.drawable.icon_battery, OnBatteryStateAction.class);
    private final static ActionConfigInfo CHECK_ON_SCREEN_STATE_CONFIG = new ActionConfigInfo(CHECK_ON_SCREEN_STATE, R.string.action_screen_state_check_title, R.drawable.icon_screen, OnScreenStateAction.class);
    private final static ActionConfigInfo CHECK_NETWORK_CONFIG = new ActionConfigInfo(CHECK_NETWORK, R.string.action_network_check_title, R.drawable.icon_network, NetworkCheckAction.class);
    private final static ActionConfigInfo APP_STATE_CONFIG = new ActionConfigInfo(APP_STATE, R.string.action_app_state_title, R.drawable.icon_package_info, AppStateAction.class);
    private final static ActionConfigInfo BATTERY_STATE_CONFIG = new ActionConfigInfo(BATTERY_STATE, R.string.action_battery_state_title, R.drawable.icon_battery, BatteryStateAction.class);
    private final static ActionConfigInfo SCREEN_STATE_CONFIG = new ActionConfigInfo(SCREEN_STATE, R.string.action_screen_state_title, R.drawable.icon_screen, ScreenStateAction.class);
    private final static ActionConfigInfo NETWORK_STATE_CONFIG = new ActionConfigInfo(NETWORK_STATE, R.string.action_network_state_title, R.drawable.icon_network, NetworkStateAction.class);
    private final static ActionConfigInfo BLUETOOTH_STATE_CONFIG = new ActionConfigInfo(BLUETOOTH_STATE, R.string.action_bluetooth_state_title, R.drawable.icon_bluetooth, BluetoothStateAction.class);
    private final static ActionConfigInfo CAPTURE_STATE_CONFIG = new ActionConfigInfo(CAPTURE_STATE, R.string.action_capture_state_title, R.drawable.icon_capture, CaptureStateAction.class);
    private final static ActionConfigInfo DATE_STATE_CONFIG = new ActionConfigInfo(DATE_STATE, R.string.action_date_state_title, R.drawable.icon_date, DateStateAction.class);
    private final static ActionConfigInfo TIME_STATE_CONFIG = new ActionConfigInfo(TIME_STATE, R.string.action_time_state_title, R.drawable.icon_time, TimeStateAction.class);


    public ActionConfigInfo getConfig() {
        return switch (this) {
            case CUSTOM -> CUSTOM_CONFIG;
            case CUSTOM_PIN -> CUSTOM_PIN_CONFIG;
            case CUSTOM_START -> CUSTOM_START_CONFIG;
            case CUSTOM_END -> CUSTOM_END_CONFIG;

            case VAR_GET -> VAR_GET_CONFIG;
            case VAR_SET -> VAR_SET_CONFIG;
            case COMMON_VAR_GET -> COMMON_VAR_GET_CONFIG;
            case COMMON_VAR_SET -> COMMON_VAR_SET_CONFIG;

            case MANUAL_START -> MANUAL_START_CONFIG;
            case ENTER_APP_START -> ENTER_APP_START_CONFIG;
            case TIME_START -> TIME_START_CONFIG;
            case NOTIFY_START -> NOTIFY_START_CONFIG;
            case NETWORK_START -> NETWORK_START_CONFIG;
            case BATTERY_START -> BATTERY_START_CONFIG;
            case SCREEN_START -> SCREEN_START_CONFIG;
            case BLUETOOTH_START -> BLUETOOTH_START_CONFIG;
            case OUTER_START -> OUTER_START_CONFIG;
            case NORMAL_START -> NORMAL_START_CONFIG;

            case LOGIC_IF -> LOGIC_IF_CONFIG;
            case LOGIC_WAIT_IF -> LOGIC_WAIT_IF_CONFIG;
            case LOGIC_FOR -> LOGIC_FOR_CONFIG;
            case LOGIC_WHILE -> LOGIC_WHILE_CONFIG;
            case LOGIC_SEQUENCE -> LOGIC_SEQUENCE_CONFIG;
            case LOGIC_RANDOM -> LOGIC_RANDOM_CONFIG;
            case LOGIC_PARALLEL -> LOGIC_PARALLEL_CONFIG;
            case LOGIC_MANUAL_CHOICE -> LOGIC_MANUAL_CHOICE_CONFIG;

            case DELAY -> DELAY_CONFIG;
            case LOG -> LOG_CONFIG;
            case CLICK_POSITION -> CLICK_POSITION_CONFIG;
            case CLICK_NODE -> CLICK_NODE_CONFIG;
            case CLICK_KEY -> CLICK_KEY_CONFIG;
            case TOUCH -> TOUCH_CONFIG;
            case INPUT -> INPUT_CONFIG;
            case SCREEN_SWITCH -> SCREEN_SWITCH_CONFIG;
            case CAPTURE_SWITCH -> CAPTURE_SWITCH_CONFIG;
            case OPEN_APP -> OPEN_APP_CONFIG;
            case OPEN_URI -> OPEN_URI_CONFIG;
            case PLAY_RINGTONE -> PLAY_RINGTONE_CONFIG;
            case STOP_RINGTONE -> STOP_RINGTONE_CONFIG;
            case COPY -> COPY_CONFIG;
            case SNI_PASTE -> SNI_PASTE_CONFIG;
            case SHARE -> SHARE_CONFIG;
            case RUN_TASK -> RUN_TASK_CONFIG;
            case SHELL -> SHELL_CONFIG;
            case BREAK_TASK -> BREAK_TASK_CONFIG;

            case CHECK_EXIST_TEXT -> CHECK_EXIST_TEXT_CONFIG;
            case CHECK_EXIST_TEXTS -> CHECK_EXIST_TEXTS_CONFIG;
            case CHECK_EXIST_TEXT_OCR -> CHECK_EXIST_TEXT_OCR_CONFIG;
            case OCR_TEXT_STATE -> OCR_TEXT_STATE_CONFIG;

            case STRING_FROM_VALUE -> STRING_FROM_VALUE_CONFIG;
            case STRING_TO_INT -> STRING_TO_INT_CONFIG;
            case STRING_ADD -> STRING_ADD_CONFIG;
            case STRING_EQUAL -> STRING_EQUAL_CONFIG;
            case STRING_REGEX -> STRING_REGEX_CONFIG;

            case CHECK_EXIST_IMAGE -> CHECK_EXIST_IMAGE_CONFIG;
            case CHECK_EXIST_IMAGES -> CHECK_EXIST_IMAGES_CONFIG;
            case CHECK_IMAGE -> CHECK_IMAGE_CONFIG;
            case IMAGE_STATE -> IMAGE_STATE_CONFIG;
            case IMAGE_SUB_IMAGE -> IMAGE_SUB_IMAGE_CONFIG;
            case SAVE_IMAGE -> SAVE_IMAGE_CONFIG;

            case CHECK_EXIST_NODE -> CHECK_EXIST_NODE_CONFIG;
            case CHECK_EXIST_NODES -> CHECK_EXIST_NODES_CONFIG;
            case NODE_INFO_STATE -> NODE_INFO_STATE_CONFIG;
            case NODE_IS_VALID -> NODE_IS_VALID_CONFIG;
            case NODE_CHILDREN -> NODE_CHILDREN_CONFIG;
            case NODE_PARENT -> NODE_PARENT_CONFIG;
            case NODES_IN_POS -> NODES_IN_POS_CONFIG;
            case NODES_IN_WINDOW -> NODES_IN_WINDOW_CONFIG;

            case CHECK_EXIST_COLOR -> CHECK_EXIST_COLOR_CONFIG;
            case CHECK_EXIST_COLORS -> CHECK_EXIST_COLORS_CONFIG;
            case CHECK_COLOR -> CHECK_COLOR_CONFIG;
            case COLOR_STATE -> COLOR_STATE_CONFIG;

            case BOOL_OR -> BOOL_OR_CONFIG;
            case BOOL_AND -> BOOL_AND_CONFIG;
            case BOOL_NOT -> BOOL_NOT_CONFIG;

            case INT_ADD -> INT_ADD_CONFIG;
            case INT_REDUCE -> INT_REDUCE_CONFIG;
            case INT_MULTI -> INT_MULTI_CONFIG;
            case INT_DIV -> INT_DIV_CONFIG;
            case INT_MOD -> INT_MOD_CONFIG;
            case INT_EQUAL -> INT_EQUAL_CONFIG;
            case INT_LARGE -> INT_LARGE_CONFIG;
            case INT_SMALL -> INT_SMALL_CONFIG;
            case INT_IN_AREA -> INT_IN_AREA_CONFIG;
            case INT_RANDOM -> INT_RANDOM_CONFIG;
            case INT_TO_VALUE_AREA -> INT_TO_VALUE_AREA_CONFIG;

            case MATH_EXPRESSION -> MATH_EXPRESSION_CONFIG;
            case FLOAT_TO_INT -> FLOAT_TO_INT_CONFIG;

            case POS_FROM_INT -> POS_FROM_INT_CONFIG;
            case POS_TO_INT -> POS_TO_INT_CONFIG;
            case POS_OFFSET -> POS_OFFSET_CONFIG;
            case POS_IN_AREA -> POS_IN_AREA_CONFIG;
            case POS_TO_AREA -> POS_TO_AREA_CONFIG;
            case POS_TO_TOUCH -> POS_TO_TOUCH_CONFIG;

            case AREA_TO_INT -> AREA_TO_INT_CONFIG;
            case AREA_PICK -> AREA_PICK_CONFIG;
            case AREA_CENTER_POS -> AREA_CENTER_POS_CONFIG;

            case ARRAY_GET -> ARRAY_GET_CONFIG;
            case ARRAY_SET -> ARRAY_SET_CONFIG;
            case ARRAY_MAKE -> ARRAY_MAKE_CONFIG;
            case ARRAY_ADD -> ARRAY_ADD_CONFIG;
            case ARRAY_INSERT -> ARRAY_INSERT_CONFIG;
            case ARRAY_REMOVE -> ARRAY_REMOVE_CONFIG;
            case ARRAY_CLEAR -> ARRAY_CLEAR_CONFIG;
            case ARRAY_SIZE -> ARRAY_SIZE_CONFIG;
            case ARRAY_VALID_INDEX -> ARRAY_VALID_INDEX_CONFIG;
            case ARRAY_APPEND -> ARRAY_APPEND_CONFIG;
            case ARRAY_INDEX_OF -> ARRAY_INDEX_OF_CONFIG;
            case ARRAY_FOR -> ARRAY_FOR_CONFIG;

            case CHECK_IN_APP -> CHECK_IN_APP_CONFIG;
            case CHECK_ON_BATTERY_STATE -> CHECK_ON_BATTERY_STATE_CONFIG;
            case CHECK_ON_SCREEN_STATE -> CHECK_ON_SCREEN_STATE_CONFIG;
            case CHECK_NETWORK -> CHECK_NETWORK_CONFIG;
            case APP_STATE -> APP_STATE_CONFIG;
            case BATTERY_STATE -> BATTERY_STATE_CONFIG;
            case SCREEN_STATE -> SCREEN_STATE_CONFIG;
            case NETWORK_STATE -> NETWORK_STATE_CONFIG;
            case BLUETOOTH_STATE -> BLUETOOTH_STATE_CONFIG;
            case CAPTURE_STATE -> CAPTURE_STATE_CONFIG;
            case DATE_STATE -> DATE_STATE_CONFIG;
            case TIME_STATE -> TIME_STATE_CONFIG;
            default -> new ActionConfigInfo();
        };
    }
}
