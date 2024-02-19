package top.bogey.touch_tool_pro.service;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.start.AppStartAction;
import top.bogey.touch_tool_pro.bean.action.start.BatteryStartAction;
import top.bogey.touch_tool_pro.bean.action.start.ManualStartAction;
import top.bogey.touch_tool_pro.bean.action.start.NetworkStartAction;
import top.bogey.touch_tool_pro.bean.action.start.NormalStartAction;
import top.bogey.touch_tool_pro.bean.action.start.NotifyStartAction;
import top.bogey.touch_tool_pro.bean.action.start.StartAction;
import top.bogey.touch_tool_pro.bean.task.Task;
import top.bogey.touch_tool_pro.save.SaveRepository;
import top.bogey.touch_tool_pro.ui.MainActivity;
import top.bogey.touch_tool_pro.ui.custom.KeepAliveFloatView;
import top.bogey.touch_tool_pro.ui.play.PlayFloatView;
import top.bogey.touch_tool_pro.utils.SettingSave;
import top.bogey.touch_tool_pro.utils.easy_float.EasyFloat;

// 黑板类，记录着当前系统的一些属性
public class WorldState {
    private static WorldState helper;

    private final ConcurrentHashMap<String, PackageInfo> appMap = new ConcurrentHashMap<>();
    private final LinkedHashMap<ManualStartAction, Task> manualStartActions = new LinkedHashMap<>();
    private String packageName;
    private String activityName;
    private String notificationPackage;
    private String notificationText;
    private int batteryPercent;
    private int batteryState;
    private int networkType;

    public static WorldState getInstance() {
        if (helper == null) helper = new WorldState();
        return helper;
    }

    public void resetAppMap(Context context) {
        LinkedHashMap<String, PackageInfo> map = new LinkedHashMap<>();
        PackageManager manager = context.getPackageManager();

        List<ApplicationInfo> applications = manager.getInstalledApplications(PackageManager.MATCH_UNINSTALLED_PACKAGES | PackageManager.MATCH_DISABLED_COMPONENTS);
        for (ApplicationInfo info : applications) {
            try {
                PackageInfo packageInfo = manager.getPackageInfo(info.packageName, PackageManager.GET_ACTIVITIES);
                map.put(packageInfo.packageName, packageInfo);
            } catch (Exception ignored) {
            }
        }
        appMap.clear();
        appMap.putAll(map);
    }

    public boolean isActivityClass(String packageName, String className) {
        if (packageName == null) return false;
        PackageInfo packageInfo = appMap.get(packageName);
        if (packageInfo == null || packageInfo.activities == null) return false;
        for (ActivityInfo activityInfo : packageInfo.activities) {
            if (TextUtils.equals(className, activityInfo.name)) return true;
        }
        return false;
    }

    public PackageInfo getPackage(String pkgName) {
        return appMap.get(pkgName);
    }

    public String getAppName(String pkgName) {
        PackageInfo info = getPackage(pkgName);
        CharSequence title = info.applicationInfo.loadLabel(MainApplication.getInstance().getPackageManager());
        return title.toString();
    }

    public ArrayList<PackageInfo> findPackageList(Context context, boolean system, CharSequence find, boolean single) {
        ArrayList<PackageInfo> packages = new ArrayList<>();

        if ((!single) && (find == null || find.length() == 0)) {
            PackageInfo info = new PackageInfo();
            info.packageName = context.getString(R.string.common_package_name);
            packages.add(info);
        }

        PackageManager manager = context.getPackageManager();
        Pattern pattern = null;
        if (!(find == null || find.length() == 0)) {
            try {
                pattern = Pattern.compile(find.toString().toLowerCase());
            } catch (PatternSyntaxException ignored) {
            }
        }

        for (PackageInfo value : appMap.values()) {
            if (value.packageName.equals(context.getPackageName())) continue;
            if (system || (value.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != ApplicationInfo.FLAG_SYSTEM) {
                CharSequence title = value.applicationInfo.loadLabel(manager);
                // 包名和应用名一致的基本上都是无效应用，跳过
//                if (value.packageName.equalsIgnoreCase(title.toString())) continue;
                if (pattern == null || pattern.matcher(title.toString().toLowerCase()).find() || pattern.matcher(value.packageName.toLowerCase()).find()) {
                    packages.add(value);
                }
            }
        }
        return packages;
    }

    public ArrayList<PackageInfo> findSharePackageList(Context context, boolean system, CharSequence find) {
        ArrayList<PackageInfo> packages = new ArrayList<>();

        if (find == null || find.length() == 0) {
            PackageInfo info = new PackageInfo();
            info.packageName = context.getString(R.string.common_package_name);
            packages.add(info);
        }

        PackageManager manager = context.getPackageManager();
        Pattern pattern = null;
        if (!(find == null || find.length() == 0)) {
            try {
                pattern = Pattern.compile(find.toString().toLowerCase());
            } catch (PatternSyntaxException ignored) {
            }
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        List<ResolveInfo> infoList = manager.queryIntentActivities(intent, PackageManager.MATCH_ALL);
        for (ResolveInfo info : infoList) {
            if (info.activityInfo.packageName.equals(context.getPackageName())) continue;
            PackageInfo packageInfo = appMap.get(info.activityInfo.packageName);
            if (packageInfo == null) continue;
            if (system || (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != ApplicationInfo.FLAG_SYSTEM) {
                CharSequence title = packageInfo.applicationInfo.loadLabel(manager);
                if (pattern == null || pattern.matcher(title.toString().toLowerCase()).find() || pattern.matcher(packageInfo.packageName.toLowerCase()).find()) {
                    packages.add(packageInfo);
                }
            }
        }

        return packages;
    }

    public void checkAutoStartAction(Class<? extends StartAction> actionType) {
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service == null || !service.isServiceEnabled()) return;

        // 特有的开始项
        ArrayList<Task> tasks = SaveRepository.getInstance().getTasksByStart(actionType);
        for (Task task : tasks) {
            for (Action action : task.getActionsByClass(actionType)) {
                StartAction startAction = (StartAction) action;
                if (startAction.isEnable()) service.runTask(task, startAction);
            }
        }

        // 通用的开始项
        tasks = SaveRepository.getInstance().getTasksByStart(NormalStartAction.class);
        for (Task task : tasks) {
            for (Action action : task.getActionsByClass(NormalStartAction.class)) {
                StartAction startAction = (StartAction) action;
                if (startAction.isEnable()) service.runTask(task, startAction);
            }
        }
    }

    public void showManualActionDialog(boolean show) {
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service == null || !service.isServiceEnabled()) return;

        boolean existView = manualStartActions.size() > 0;
        manualStartActions.clear();
        if (show) {
            ArrayList<Task> tasks = SaveRepository.getInstance().getTasksByStart(ManualStartAction.class);
            for (Task task : tasks) {
                for (Action action : task.getActionsByClass(ManualStartAction.class)) {
                    StartAction startAction = (StartAction) action;
                    if (startAction.isEnable() && startAction.checkReady(null, task))
                        manualStartActions.put((ManualStartAction) startAction, task);
                }
            }
        }

        KeepAliveFloatView keepView = MainApplication.getInstance().getKeepView();
        if (keepView != null) {
            new Handler(Looper.getMainLooper()).post(() -> {
                if (manualStartActions.size() > 0 || existView) {
                    PlayFloatView view = (PlayFloatView) EasyFloat.getView(PlayFloatView.class.getName());
                    if (manualStartActions.size() == 0 || !SettingSave.getInstance().isPlayViewVisible(keepView.getContext())) {
                        if (view != null) view.setNeedRemove(true);
                    } else {
                        if (view == null) {
                            view = new PlayFloatView(keepView.getContext());
                            view.show();
                        }
                        view.onNewActions();
                    }
                }
            });
        }
    }

    public boolean enterActivity(String packageName, String className) {
        if (isActivityClass(packageName, className)) {
            if (packageName.equals(MainApplication.getInstance().getPackageName())) {
                if (setActivityInfo(packageName, className)) {
                    showManualActionDialog(!className.equals(MainActivity.class.getName()));
                }
                return true;
            } else {
                if (setActivityInfo(packageName, className)) {
                    checkAutoStartAction(AppStartAction.class);
                    showManualActionDialog(true);
                    return true;
                }
            }
        }
        return false;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getActivityName() {
        return activityName;
    }

    public boolean setActivityInfo(String packageName, String activityName) {
        if (packageName == null || activityName == null) return false;
        if (packageName.equals(this.packageName) && activityName.equals(this.activityName)) return false;
        this.packageName = packageName;
        this.activityName = activityName;
        return true;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public String getNotificationPackage() {
        return notificationPackage;
    }

    public void setNotification(String notificationPackage, String notificationText) {
        this.notificationPackage = notificationPackage;
        this.notificationText = notificationText;
        checkAutoStartAction(NotifyStartAction.class);
    }

    public int getBatteryPercent() {
        return batteryPercent;
    }

    public int getBatteryState() {
        return batteryState;
    }

    public void setBatteryState(int batteryPercent, int batteryState) {
        if (batteryPercent == this.batteryPercent && batteryState == this.batteryState) return;
        this.batteryPercent = batteryPercent;
        this.batteryState = batteryState;
        checkAutoStartAction(BatteryStartAction.class);
    }

    public int getNetworkType() {
        return networkType;
    }

    public void setNetworkType(int networkType) {
        if (networkType == this.networkType) return;
        this.networkType = networkType;
        checkAutoStartAction(NetworkStartAction.class);
    }

    public LinkedHashMap<ManualStartAction, Task> getManualStartActions() {
        return manualStartActions;
    }
}
