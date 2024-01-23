package top.bogey.touch_tool_pro.utils;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityManager;

import androidx.annotation.StringRes;
import androidx.core.content.FileProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.other.ScreenStateAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.pins.PinImage;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.task.Task;

public class AppUtils {

    public static boolean isRelease(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        return (applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) == 0;
    }

    public static void showDialog(Context context, int msg, BooleanResultCallback callback) {
        showDialog(context, context.getString(msg), callback);
    }

    public static void showDialog(Context context, String msg, BooleanResultCallback callback) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.dialog_title)
                .setMessage(msg)
                .setPositiveButton(R.string.enter, (dialog, which) -> {
                    dialog.dismiss();
                    if (callback != null) callback.onResult(true);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    dialog.dismiss();
                    if (callback != null) callback.onResult(false);
                })
                .show();
    }

    public static void showEditDialog(Context context, @StringRes int title, CharSequence defaultValue, EditResultCallback callback) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_text_input, null);
        TextInputEditText editText = view.findViewById(R.id.titleEdit);
        editText.setText(defaultValue);

        new MaterialAlertDialogBuilder(context)
                .setPositiveButton(R.string.enter, (dialog, which) -> {
                    if (callback != null && editText.getText() != null)
                        callback.onResult(editText.getText().toString());
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    if (callback != null) callback.onResult(defaultValue);
                    dialog.dismiss();
                })
                .setView(view)
                .setTitle(title)
                .show();
    }

    public static void gotoAppDetailSetting(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        } catch (Exception ignored) {
        }
    }

    public static void gotoApp(Context context, String pkgName) {
        try {
            PackageManager manager = context.getPackageManager();
            Intent intent = manager.getLaunchIntentForPackage(pkgName);
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                context.getApplicationContext().startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void gotoActivity(Context context, String pkgName, String activity) {
        try {
            Intent intent = new Intent();
            intent.setClassName(pkgName, activity);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            context.getApplicationContext().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void gotoScheme(Context context, String scheme) {
        try {
            Intent intent = Intent.parseUri(scheme, Intent.URI_INTENT_SCHEME | Intent.URI_ANDROID_APP_SCHEME);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void gotoUrl(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void gotoBatterySetting(Context context) {
        try {
            @SuppressLint("BatteryLife") Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isIgnoredBattery(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
    }

    public static ScreenStateAction.ScreenState getScreenState(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean screenOn = powerManager.isInteractive();
        if (screenOn) {
            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            boolean locked = keyguardManager.isKeyguardLocked();
            if (locked) {
                return ScreenStateAction.ScreenState.LOCKED;
            } else {
                return ScreenStateAction.ScreenState.ON;
            }
        }
        return ScreenStateAction.ScreenState.OFF;
    }

    public static void wakeScreen(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, context.getString(R.string.common_package_name));
        wakeLock.acquire(100);
        wakeLock.release();
    }

    public static boolean isAccessibilityServiceEnabled(Context context) {
        AccessibilityManager manager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        for (AccessibilityServiceInfo info : manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)) {
            if (info.getId().contains(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static String formatDateLocalDate(Context context, long dateTime) {
        Calendar timeCalendar = Calendar.getInstance();
        timeCalendar.setTimeInMillis(dateTime);

        Calendar currCalendar = Calendar.getInstance();
        currCalendar.setTimeInMillis(System.currentTimeMillis());

        StringBuilder builder = new StringBuilder();
        if (timeCalendar.get(Calendar.YEAR) != currCalendar.get(Calendar.YEAR))
            builder.append(context.getString(R.string.year, timeCalendar.get(Calendar.YEAR)));
        builder.append(context.getString(R.string.month, timeCalendar.get(Calendar.MONTH) + 1));
        builder.append(context.getString(R.string.day, timeCalendar.get(Calendar.DAY_OF_MONTH)));
        return builder.toString();
    }

    public static String formatDateLocalTime(Context context, long dateTime) {
        Calendar timeCalendar = Calendar.getInstance();
        timeCalendar.setTimeInMillis(dateTime);

        StringBuilder builder = new StringBuilder();
        builder.append(context.getString(R.string.hour, timeCalendar.get(Calendar.HOUR_OF_DAY)));
        if (timeCalendar.get(Calendar.MINUTE) != 0)
            builder.append(context.getString(R.string.minute, timeCalendar.get(Calendar.MINUTE)));
        return builder.toString();
    }

    public static String formatDateLocalMillisecond(Context context, long dateTime) {
        Calendar timeCalendar = Calendar.getInstance();
        timeCalendar.setTimeInMillis(dateTime);

        return context.getString(R.string.hour, timeCalendar.get(Calendar.HOUR_OF_DAY)) +
                context.getString(R.string.minute, timeCalendar.get(Calendar.MINUTE)) +
                context.getString(R.string.second, timeCalendar.get(Calendar.SECOND)) +
                context.getString(R.string.millisecond, timeCalendar.get(Calendar.MILLISECOND));
    }

    public static String formatDateLocalDuration(Context context, long dateTime) {
        int hours = (int) (dateTime / 1000 / 60 / 60);
        int minute = (int) (dateTime / 1000 / 60 % 60);

        StringBuilder builder = new StringBuilder();
        if (hours != 0) builder.append(context.getString(R.string.hours, hours));
        if (minute != 0) builder.append(context.getString(R.string.minutes, minute));
        return builder.toString();
    }

    public static long mergeDateTime(long date, long time) {
        Calendar baseCalendar = Calendar.getInstance();
        baseCalendar.setTimeInMillis(time);
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTimeInMillis(date);
        Calendar calendar = Calendar.getInstance();
        calendar.set(dateCalendar.get(Calendar.YEAR), dateCalendar.get(Calendar.MONTH), dateCalendar.get(Calendar.DATE), baseCalendar.get(Calendar.HOUR_OF_DAY), baseCalendar.get(Calendar.MINUTE), 0);
        return calendar.getTimeInMillis();
    }

    @SuppressLint("DefaultLocale")
    private static String getFunctionContextsFileName(Context context, ArrayList<FunctionContext> functionContexts) {
        String name = context.getString(R.string.app_name);
        ArrayList<Task> tasks = new ArrayList<>();
        for (FunctionContext functionContext : functionContexts) {
            if (functionContext instanceof Task task) tasks.add(task);
        }

        if (tasks.size() == 1) {
            name = tasks.get(0).getTitle();
        } else if (functionContexts.size() == 1) {
            FunctionContext functionContext = functionContexts.get(0);
            name = functionContext.getTitle();
        }
        Calendar timeCalendar = Calendar.getInstance();
        timeCalendar.setTimeInMillis(System.currentTimeMillis());

        return String.format("%s_%d%02d%02d.%02d%02d.ttp",
                name,
                timeCalendar.get(Calendar.YEAR) - 2000,
                timeCalendar.get(Calendar.MONTH) + 1,
                timeCalendar.get(Calendar.DAY_OF_MONTH),
                timeCalendar.get(Calendar.HOUR_OF_DAY),
                timeCalendar.get(Calendar.MINUTE));
    }

    public static void backupFunctionContexts(Context context, ArrayList<FunctionContext> functionContexts) {
        String fileName = getFunctionContextsFileName(context, functionContexts);
        MainApplication.getInstance().getMainActivity().launcherCreateDocument(fileName, (code, intent) -> {
            if (code == Activity.RESULT_OK) {
                Uri uri = intent.getData();
                if (uri == null) return;
                try (OutputStream outputStream = context.getContentResolver().openOutputStream(uri)) {
                    if (outputStream == null) return;
                    String json = GsonUtils.gson.toJson(functionContexts);
                    outputStream.write(json.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static void exportFunctionContexts(Context context, ArrayList<FunctionContext> functionContexts) {
        if (functionContexts == null || functionContexts.isEmpty()) return;

        String fileName = getFunctionContextsFileName(context, functionContexts);
        File file = new File(context.getCacheDir(), fileName);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            String json = GsonUtils.gson.toJson(functionContexts);
            fileOutputStream.write(json.getBytes());
            fileOutputStream.flush();

            Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".file_provider", file);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, fileUri);
            intent.setType("text/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.export_task_tips)));

        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void exportString(Context context, String string) {
        String fileName = "export_" + formatDateLocalDate(context, System.currentTimeMillis()) + formatDateLocalTime(context, System.currentTimeMillis()) + ".txt";
        MainApplication.getInstance().getMainActivity().launcherCreateDocument(fileName, (code, intent) -> {
            if (code == Activity.RESULT_OK) {
                Uri uri = intent.getData();
                if (uri == null) return;
                try (OutputStream outputStream = context.getContentResolver().openOutputStream(uri)) {
                    if (outputStream == null) return;
                    outputStream.write(string.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static void exportImage(Context context, Bitmap image) {
        String fileName = "export_" + formatDateLocalDate(context, System.currentTimeMillis()) + formatDateLocalTime(context, System.currentTimeMillis()) + ".jpg";
        MainApplication.getInstance().getMainActivity().launcherCreateDocument(fileName, (code, intent) -> {
            if (code == Activity.RESULT_OK) {
                Uri uri = intent.getData();
                if (uri == null) return;
                try (OutputStream outputStream = context.getContentResolver().openOutputStream(uri)) {
                    if (outputStream == null) return;
                    image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static ArrayList<FunctionContext> importFunctionContexts(Context context, Uri uri) {
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            if (inputStream != null) {
                byte[] bytes = new byte[inputStream.available()];
                int read = inputStream.read(bytes);
                if (read > 0) {
                    return GsonUtils.getAsObject(new String(bytes), TypeToken.getParameterized(ArrayList.class, FunctionContext.class).getType(), new ArrayList<>());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static void installApk(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static void copyFileFromAssets(Context context, String from, String to) {
        if (from == null || from.isEmpty() || to == null || to.isEmpty()) return;

        try (InputStream inputStream = new BufferedInputStream(context.getAssets().open(from));
             OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(to))) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void copyDirFromAssets(Context context, String from, String to) {
        if (from == null || from.isEmpty() || to == null || to.isEmpty()) return;

        if (!new File(to).exists()) {
            new File(to).mkdirs();
        }

        try {
            String[] files = context.getAssets().list(from);
            if (files == null) return;
            for (String fileName : files) {
                String fromSubPath = from + File.separator + fileName;
                String toSubPath = to + File.separator + fileName;
                if (new File(fromSubPath).isDirectory()) {
                    copyDirFromAssets(context, fromSubPath, toSubPath);
                } else {
                    copyFileFromAssets(context, fromSubPath, toSubPath);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static long getFileSize(File file) {
        long size = 0;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File listFile : files) {
                    size += getFileSize(listFile);
                }
            }
        } else {
            size = file.length();
        }
        return size;
    }

    @SuppressLint("DefaultLocale")
    public static String getFileSizeString(File file) {
        long fileSize = getFileSize(file);
        double kb = fileSize / 1024.0;
        if (kb < 1024) return String.format("%.1fK", kb);
        double m = kb / 1024;
        return String.format("%.1fM", m);
    }

    public static boolean deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File listFile : files) {
                    boolean result = deleteFile(listFile);
                    if (!result) return false;
                }
            }
        }
        return file.delete();
    }

    public static Uri valueToCacheFile(Context context, PinValue value) {
        File file;
        if (value instanceof PinImage) {
            file = new File(context.getCacheDir(), System.currentTimeMillis() + ".jpg");
        } else {
            file = new File(context.getCacheDir(), System.currentTimeMillis() + "text.txt");
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            if (value instanceof PinImage image) {
                Bitmap bitmap = image.getImage(context);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            } else {
                fileOutputStream.write(value.toString().getBytes());
            }
            fileOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return FileProvider.getUriForFile(context, context.getPackageName() + ".file_provider", file);
    }
}
