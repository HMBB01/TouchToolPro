package top.bogey.touch_tool_pro.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;
import top.bogey.touch_tool_pro.utils.ActivityResultCallback;
import top.bogey.touch_tool_pro.utils.AppUtils;
import top.bogey.touch_tool_pro.utils.SettingSave;

public class BaseActivity extends AppCompatActivity {
    static {
        System.loadLibrary("native");
    }

    private ActivityResultLauncher<Intent> intentLauncher;
    private ActivityResultLauncher<String> permissionLauncher;
    private ActivityResultLauncher<String> contentLauncher;
    private ActivityResultLauncher<String> createDocumentLauncher;

    private ActivityResultCallback resultCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("BaseActivity", "onCreate: " + this.getClass().getName());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(params);
        }

        intentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (resultCallback != null) {
                resultCallback.onResult(result.getResultCode(), result.getData());
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            if (result && resultCallback != null) resultCallback.onResult(RESULT_OK, null);
        });

        contentLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null && resultCallback != null) {
                Intent intent = new Intent();
                intent.setData(result);
                resultCallback.onResult(RESULT_OK, intent);
            }
        });

        createDocumentLauncher = registerForActivityResult(new ActivityResultContracts.CreateDocument("text/*"), result -> {
            if (result != null && resultCallback != null) {
                Intent intent = new Intent();
                intent.setData(result);
                resultCallback.onResult(RESULT_OK, intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("BaseActivity", "onStart: " + this.getClass().getName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("BaseActivity", "onResume: " + this.getClass().getName());
        restartAccessibilityServiceBySecurePermission();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("BaseActivity", "onPause: " + this.getClass().getName());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("BaseActivity", "onStop: " + this.getClass().getName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("BaseActivity", "onDestroy: " + this.getClass().getName());
        intentLauncher = null;
        permissionLauncher = null;
        contentLauncher = null;
        createDocumentLauncher = null;
        resultCallback = null;
    }

    public void launchCapture(ActivityResultCallback callback) {
        if (intentLauncher == null) {
            if (callback != null) callback.onResult(Activity.RESULT_CANCELED, null);
            return;
        }
        resultCallback = callback;
        MediaProjectionManager manager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        intentLauncher.launch(manager.createScreenCaptureIntent());
    }

    public void launchNotification(ActivityResultCallback callback) {
        if (permissionLauncher == null) {
            if (callback != null) callback.onResult(Activity.RESULT_CANCELED, null);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            String permission = Manifest.permission.POST_NOTIFICATIONS;
            if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                if (callback != null) callback.onResult(Activity.RESULT_OK, null);
            } else if (shouldShowRequestPermissionRationale(permission)) {
                AppUtils.showDialog(this, R.string.notification_on_tips, result -> {
                    if (result) {
                        resultCallback = callback;
                        permissionLauncher.launch(permission);
                    } else {
                        if (callback != null) callback.onResult(Activity.RESULT_CANCELED, null);
                    }
                });
            } else {
                resultCallback = callback;
                permissionLauncher.launch(permission);
            }
        } else {
            if (callback != null) callback.onResult(Activity.RESULT_OK, null);
        }
    }

    public void launcherContent(ActivityResultCallback callback) {
        if (contentLauncher == null) {
            if (callback != null) callback.onResult(Activity.RESULT_CANCELED, null);
            return;
        }
        resultCallback = callback;
        contentLauncher.launch("*/*");
    }

    public void launcherCreateDocument(String fileName, ActivityResultCallback callback) {
        if (createDocumentLauncher == null) {
            if (callback != null) callback.onResult(Activity.RESULT_CANCELED, null);
            return;
        }
        resultCallback = callback;
        try {
            createDocumentLauncher.launch(fileName);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.task_setting_backup_error, Toast.LENGTH_SHORT).show();
        }
    }

    public void launcherRingtone(String path, ActivityResultCallback callback) {
        if (intentLauncher == null) {
            if (callback != null) callback.onResult(Activity.RESULT_CANCELED, null);
            return;
        }
        resultCallback = callback;
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);
        if (path != null && !path.isEmpty()) {
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(path));
        }
        intentLauncher.launch(intent);
    }

    public void launcherBluetooth(ActivityResultCallback callback) {
        if (permissionLauncher == null) {
            if (callback != null) callback.onResult(Activity.RESULT_CANCELED, null);
            return;
        }

        String permission;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            permission = Manifest.permission.BLUETOOTH;
        } else {
            permission = Manifest.permission.BLUETOOTH_CONNECT;
        }
        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            if (callback != null) callback.onResult(Activity.RESULT_OK, null);
        } else if (shouldShowRequestPermissionRationale(permission)) {
            AppUtils.showDialog(this, R.string.permission_setting_bluetooth_tips, result -> {
                if (result) {
                    resultCallback = callback;
                    permissionLauncher.launch(permission);
                } else {
                    if (callback != null) callback.onResult(Activity.RESULT_CANCELED, null);
                }
            });
        } else {
            resultCallback = callback;
            permissionLauncher.launch(permission);
        }
    }

    public void restartAccessibilityServiceBySecurePermission() {
        // 界面打开时尝试恢复无障碍服务
        // 如果应用服务设置关闭了，就啥都不管
        if (!SettingSave.getInstance().isServiceEnabled()) return;

        // 是否有权限去重启无障碍服务
        if (checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) != PackageManager.PERMISSION_GRANTED) return;

        // 看一下服务有没有开启
        if (AppUtils.isAccessibilityServiceEnabled(this)) return;

        // 没有开启去开启
        String enabledService = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        Settings.Secure.putString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, String.format("%s:%s/%s", enabledService, getPackageName(), MainAccessibilityService.class.getName()));
        Settings.Secure.putInt(getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, 1);
    }

    public boolean stopAccessibilityServiceBySecurePermission() {
        // 是否有权限去重启无障碍服务
        if (checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) != PackageManager.PERMISSION_GRANTED) return false;

        // 看一下服务有没有开启
        if (AppUtils.isAccessibilityServiceEnabled(this)) {
            // 开启去关闭
            String enabledService = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            String replace = enabledService.replaceFirst(String.format(":?%s/%s", getPackageName(), MainAccessibilityService.class.getName()), "");
            Settings.Secure.putString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, replace);
            Settings.Secure.putInt(getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, 1);
        }
        return true;
    }
}
