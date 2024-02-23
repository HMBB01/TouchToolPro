package top.bogey.touch_tool_pro.service;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import top.bogey.touch_tool_pro.bean.action.start.InnerStartAction;
import top.bogey.touch_tool_pro.bean.action.start.ScreenStartAction;
import top.bogey.touch_tool_pro.utils.SettingSave;

public class SystemEventReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        if (intentAction == null) return;
        switch (intentAction) {
            // 电量变更
            case Intent.ACTION_BATTERY_CHANGED -> {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int state = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
                WorldState.getInstance().setBatteryState(level * 100 / scale, state);
            }

            // 这个是为了停止任务做的
            case Intent.ACTION_TIME_TICK -> WorldState.getInstance().checkAutoStartAction(InnerStartAction.class);

            case Intent.ACTION_SCREEN_ON, Intent.ACTION_SCREEN_OFF, Intent.ACTION_USER_PRESENT -> {
                WorldState.getInstance().checkAutoStartAction(InnerStartAction.class);
                WorldState.getInstance().checkAutoStartAction(ScreenStartAction.class);
                WorldState.getInstance().showManualActionDialog(SettingSave.getInstance().isPlayViewVisible(context));
            }

            // 更新应用列表
            case Intent.ACTION_PACKAGE_ADDED, Intent.ACTION_PACKAGE_REMOVED, Intent.ACTION_PACKAGE_CHANGED -> {
                String packageName = intent.getDataString();
                if (packageName == null) return;
                WorldState.getInstance().resetAppMap(context);
            }

            case BluetoothDevice.ACTION_ACL_CONNECTED -> {
                if (SettingSave.getInstance().getUseBluetooth(context)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device == null) return;
                    @SuppressLint("MissingPermission") String name = device.getName();
                    WorldState.getInstance().addBluetoothDevices(device.getAddress(), name);
                }
            }

            case BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                if (SettingSave.getInstance().getUseBluetooth(context)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device == null) return;
                    WorldState.getInstance().removeBluetoothDevices(device.getAddress());
                }
            }
        }
    }

    public IntentFilter getFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        // 这些过滤是为了停止任务
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_TIME_TICK);
        // 过滤应用安装卸载更新
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);

        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        return filter;
    }
}
