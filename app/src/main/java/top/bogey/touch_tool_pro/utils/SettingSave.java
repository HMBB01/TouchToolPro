package top.bogey.touch_tool_pro.utils;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.color.DynamicColors;
import com.google.android.material.color.DynamicColorsOptions;
import com.tencent.mmkv.MMKV;

import java.util.List;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.bean.action.other.ScreenStateAction;
import top.bogey.touch_tool_pro.service.KeepAliveService;

public class SettingSave {
    private static final String RUN_TIMES = "RUN_TIMES";                                        // 运行应用次数
    private static final String RUNNING_ERROR = "RUNNING_ERROR";                                // 崩溃时的错误日志

    private static final String SERVICE_ENABLED = "SERVICE_ENABLED";                            // 功能是否开启
    private static final String SERVICE_ENABLED_TIP = "SERVICE_ENABLED_TIP";                    // 功能开启首次提示
    private static final String CAPTURE_SERVICE_ENABLED_TIP = "CAPTURE_SERVICE_ENABLED_TIP";    // 录屏开启首次提示

    private static final String PLAY_VIEW_STATE = "PLAY_VIEW_STATE";                            // 手动执行悬浮窗收起展开状态
    private static final String PLAY_VIEW_POSITION = "PLAY_VIEW_POSITION";                      // 手动执行悬浮窗位置
    private static final String CHOICE_VIEW_POSITION = "CHOICE_VIEW_POSITION";                  // 选择执行弹窗位置
    private static final String SELECT_NODE_TYPE = "SELECT_NODE_TYPE";                          // 选择控件优先方式

    private static final String HIDE_BACKGROUND = "HIDE_BACKGROUND";                            // 隐藏后台
    private static final String KEEP_ALIVE = "KEEP_ALIVE";                                      // 前台服务保活

    private static final String SUPER_USER_TYPE = "SUPER_USER_TYPE";                            // 超级用户类型
    private static final String USE_EXACT_ALARM = "USE_EXACT_ALARM";                            // 使用精确计时
    private static final String USE_OCR = "USE_OCR";                                            // 使用Ocr
    private static final String USE_BLUETOOTH = "USE_BLUETOOTH";                                // 使用蓝牙

    private static final String PLAY_VIEW_VISIBLE_TYPE = "PLAY_VIEW_VISIBLE_TYPE";              // 显示手动执行悬浮窗
    private static final String SHOW_TOUCH = "SHOW_TOUCH";                                      // 显示手势轨迹
    private static final String SHOW_START = "SHOW_START";                                      // 显示任务开始提示

    private static final String FIRST_SHOW_TASK = "FIRST_SHOW_TASK";                            // 主页改为任务标签页
    private static final String FIRST_LOOK_BLUEPRINT = "FIRST_LOOK_BLUEPRINT";                  // 蓝图界面进去时默认查看蓝图
    private static final String USE_TAKE_CAPTURE = "USE_TAKE_CAPTURE";                          // 使用无障碍的TakeCapture

    private static final String NIGHT_MODE = "NIGHT_MODE";                                      // 夜间模式
    private static final String DYNAMIC_COLOR = "DYNAMIC_COLOR";                                // 动态颜色

    private static final MMKV settingMMKV = MMKV.defaultMMKV();
    private static SettingSave settingSave;

    private boolean isAppliedDynamicColor = false;
    private boolean isDynamicColor = true;
    private final DynamicColorsOptions options = new DynamicColorsOptions.Builder().setPrecondition((activity, theme) -> isDynamicColor).build();

    public static SettingSave getInstance() {
        if (settingSave == null) settingSave = new SettingSave();
        return settingSave;
    }

    public void init(Context context) {
        setHideBackground(context, isHideBackground());
        setNightMode(getNightMode());
        setKeepAlive(context, isKeepAlive());
    }

    public int getRunTimes() {
        return settingMMKV.decodeInt(RUN_TIMES, 0);
    }

    public void addRunTimes() {
        settingMMKV.encode(RUN_TIMES, getRunTimes() + 1);
    }

    public String getRunningError() {
        return settingMMKV.decodeString(RUNNING_ERROR);
    }

    public void setRunningError(String error) {
        settingMMKV.encode(RUNNING_ERROR, error);
    }


    public boolean isServiceEnabled() {
        return settingMMKV.decodeBool(SERVICE_ENABLED, false);
    }

    public void setServiceEnabled(boolean enabled) {
        settingMMKV.encode(SERVICE_ENABLED, enabled);
    }

    public boolean isServiceEnabledTip() {
        return settingMMKV.decodeBool(SERVICE_ENABLED_TIP, false);
    }

    public void setServiceEnabledTip(boolean enabled) {
        settingMMKV.encode(SERVICE_ENABLED_TIP, enabled);
    }

    public boolean isCaptureServiceEnabledTip() {
        return settingMMKV.decodeBool(CAPTURE_SERVICE_ENABLED_TIP, false);
    }

    public void setCaptureServiceEnabledTip(boolean enabled) {
        settingMMKV.encode(CAPTURE_SERVICE_ENABLED_TIP, enabled);
    }


    public boolean isPlayViewExpand() {
        return settingMMKV.decodeBool(PLAY_VIEW_STATE, false);
    }

    public void setPlayViewExpand(boolean expand) {
        settingMMKV.encode(PLAY_VIEW_STATE, expand);
    }

    public Point getPlayViewPosition() {
        return settingMMKV.decodeParcelable(PLAY_VIEW_POSITION, Point.class, new Point());
    }

    public void setPlayViewPosition(Point position) {
        settingMMKV.encode(PLAY_VIEW_POSITION, position);
    }

    public Point getChoiceViewPosition() {
        return settingMMKV.decodeParcelable(CHOICE_VIEW_POSITION, Point.class, new Point());
    }

    public void setChoiceViewPosition(Point position) {
        settingMMKV.encode(CHOICE_VIEW_POSITION, position);
    }

    public int getSelectNodeType() {
        return settingMMKV.decodeInt(SELECT_NODE_TYPE, 0);
    }

    public void setSelectNodeType(int type) {
        settingMMKV.encode(SELECT_NODE_TYPE, type);
    }


    public boolean isHideBackground() {
        return settingMMKV.decodeBool(HIDE_BACKGROUND, false);
    }

    public void setHideBackground(Context context, boolean hide) {
        settingMMKV.encode(HIDE_BACKGROUND, hide);
        int taskId = MainApplication.getInstance().getMainActivity().getTaskId();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.AppTask> tasks = activityManager.getAppTasks();
            if (tasks != null) tasks.forEach(task -> {
                if (task.getTaskInfo().id == taskId) task.setExcludeFromRecents(hide);
            });
        }
    }

    public boolean isKeepAlive() {
        return settingMMKV.decodeBool(KEEP_ALIVE, false);
    }

    public void setKeepAlive(Context context, boolean keepAlive) {
        KeepAliveService keepService = MainApplication.getInstance().getKeepService();
        if (keepAlive && keepService == null) {
            context.startService(new Intent(context, KeepAliveService.class));
        }
        if (!keepAlive && keepService != null) {
            context.stopService(new Intent(context, KeepAliveService.class));
        }
        settingMMKV.encode(KEEP_ALIVE, keepAlive);
    }


    public int getSuperUserType() {
        return settingMMKV.decodeInt(SUPER_USER_TYPE, 0);
    }

    public void setSuperUserType(int type) {
        settingMMKV.encode(SUPER_USER_TYPE, type);
    }

    public boolean isUseExactAlarm() {
        return settingMMKV.decodeBool(USE_EXACT_ALARM, false);
    }

    public void setUseExactAlarm(boolean useExactAlarm) {
        settingMMKV.encode(USE_EXACT_ALARM, useExactAlarm);
    }

    public boolean isUseOcr() {
        return settingMMKV.decodeBool(USE_OCR, false);
    }

    public void setUseOcr(boolean useOcr) {
        settingMMKV.encode(USE_OCR, useOcr);
    }

    public boolean getUseBluetooth(Context context) {
        boolean bluetooth;
        if (Build.VERSION_CODES.S > Build.VERSION.SDK_INT) {
            bluetooth = context.checkSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED;
        } else {
            bluetooth = context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
        }
        return bluetooth & settingMMKV.decodeBool(USE_BLUETOOTH, false);
    }

    public void setUseBluetooth(Context context, boolean useBluetooth) {
        boolean bluetooth;
        if (Build.VERSION_CODES.S > Build.VERSION.SDK_INT) {
            bluetooth = context.checkSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED;
        } else {
            bluetooth = context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
        }
        settingMMKV.encode(USE_BLUETOOTH, useBluetooth & bluetooth);
    }

    public int getPlayViewVisibleType() {
        return settingMMKV.decodeInt(PLAY_VIEW_VISIBLE_TYPE, 2);
    }

    public void setPlayViewVisibleType(int type) {
        settingMMKV.encode(PLAY_VIEW_VISIBLE_TYPE, type);
    }

    public boolean isPlayViewVisible(Context context) {
        int type = getPlayViewVisibleType();
        switch (type) {
            case 1 -> {
                ScreenStateAction.ScreenState state = AppUtils.getScreenState(context);
                return state == ScreenStateAction.ScreenState.ON;
            }
            case 2 -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    public boolean isShowTouch() {
        return settingMMKV.decodeBool(SHOW_TOUCH, false);
    }

    public void setShowTouch(boolean showTouch) {
        settingMMKV.encode(SHOW_TOUCH, showTouch);
    }

    public boolean isShowStart() {
        return settingMMKV.decodeBool(SHOW_START, true);
    }

    public void setShowStart(boolean showStart) {
        settingMMKV.encode(SHOW_START, showStart);
    }


    public boolean isFirstShowTask() {
        return settingMMKV.decodeBool(FIRST_SHOW_TASK, false);
    }

    public void setFirstShowTask(boolean firstShowTask) {
        settingMMKV.encode(FIRST_SHOW_TASK, firstShowTask);
    }

    public boolean isFirstLookBlueprint() {
        return settingMMKV.decodeBool(FIRST_LOOK_BLUEPRINT, false);
    }

    public void setFirstLookBlueprint(boolean firstLookBlueprint) {
        settingMMKV.encode(FIRST_LOOK_BLUEPRINT, firstLookBlueprint);
    }

    public boolean isUseTakeCapture() {
        return settingMMKV.decodeBool(USE_TAKE_CAPTURE, true);
    }

    public void setUseTakeCapture(boolean useTakeCapture) {
        settingMMKV.encode(USE_TAKE_CAPTURE, useTakeCapture);
    }



    public int getNightMode() {
        return settingMMKV.decodeInt(NIGHT_MODE, 0);
    }

    public void setNightMode(int nightMode) {
        settingMMKV.encode(NIGHT_MODE, nightMode);
        AppCompatDelegate.setDefaultNightMode(nightMode - 1);
    }

    public boolean isDynamicColor() {
        return settingMMKV.decodeBool(DYNAMIC_COLOR, isDynamicColor);
    }

    public void setDynamicColor(Context context, boolean enabled) {
        if (!isAppliedDynamicColor) {
            isAppliedDynamicColor = true;
            isDynamicColor = enabled;
            DynamicColors.applyToActivitiesIfAvailable((Application) context.getApplicationContext(), options);
        } else {
            if (isDynamicColor != enabled) {
                isDynamicColor = enabled;
                MainApplication.getInstance().getMainActivity().recreate();
            }
        }
        settingMMKV.encode(DYNAMIC_COLOR, enabled);
    }
}
