package top.bogey.touch_tool_pro.ui.setting;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.databinding.ViewSettingBinding;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;
import top.bogey.touch_tool_pro.super_user.SuperUser;
import top.bogey.touch_tool_pro.super_user.root.RootSuperUser;
import top.bogey.touch_tool_pro.super_user.shizuku.ShizukuSuperUser;
import top.bogey.touch_tool_pro.ui.MainActivity;
import top.bogey.touch_tool_pro.utils.AppUtils;
import top.bogey.touch_tool_pro.utils.SettingSave;
import top.bogey.touch_tool_pro.utils.easy_float.EasyFloat;
import top.bogey.touch_tool_pro.utils.ocr.Predictor;

public class SettingView extends Fragment {
    private ViewSettingBinding binding;

    public static void resetSwitchState() {
        MainActivity activity = MainApplication.getInstance().getMainActivity();
        if (activity == null) return;
        SettingView currFragment = activity.getCurrFragment(SettingView.class);
        if (currFragment == null) return;
        currFragment.refreshSwitchState();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        resetSwitchState();
    }

    public void refreshSwitchState() {
        binding.playViewVisibleGroup.check(binding.playViewVisibleGroup.getChildAt(SettingSave.getInstance().getPlayViewVisibleType()).getId());
        binding.showPackageInfoSwitch.setChecked(EasyFloat.getView(PackageInfoFloatView.class.getName()) != null);
        binding.logSwitch.setChecked(EasyFloat.getView(LogFloatView.class.getName()) != null);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();
        binding = ViewSettingBinding.inflate(inflater, container, false);

        // 应用设置
        binding.hideBackgroundSwitch.setOnClickListener(v -> SettingSave.getInstance().setHideBackground(requireContext(), binding.hideBackgroundSwitch.isChecked()));
        binding.hideBackgroundSwitch.setChecked(SettingSave.getInstance().isHideBackground());

        binding.keepAliveSwitch.setOnClickListener(v -> SettingSave.getInstance().setKeepAlive(requireContext(), binding.keepAliveSwitch.isChecked()));
        binding.keepAliveSwitch.setChecked(SettingSave.getInstance().isKeepAlive());


        binding.cleanCache.setOnClickListener(v -> {
            AppUtils.deleteFile(requireContext().getCacheDir());
            binding.cleanCacheTips.setText(getString(R.string.app_setting_clean_cache_tips, AppUtils.getFileSizeString(requireContext().getCacheDir())));
        });
        binding.cleanCacheTips.setText(getString(R.string.app_setting_clean_cache_tips, AppUtils.getFileSizeString(requireContext().getCacheDir())));


        // 工具
        binding.showPackageInfoSwitch.setOnClickListener(v -> {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service == null || !service.isServiceEnabled()) {
                binding.showPackageInfoSwitch.setChecked(false);
                Toast.makeText(getContext(), R.string.accessibility_service_off_tips, Toast.LENGTH_SHORT).show();
                return;
            }
            View view = EasyFloat.getView(PackageInfoFloatView.class.getName());
            if (view == null) {
                new PackageInfoFloatView(requireContext()).show();
            } else {
                EasyFloat.dismiss(PackageInfoFloatView.class.getName());
            }
        });

        binding.logSwitch.setOnClickListener(v -> {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service == null || !service.isServiceEnabled()) {
                binding.showPackageInfoSwitch.setChecked(false);
                Toast.makeText(getContext(), R.string.accessibility_service_off_tips, Toast.LENGTH_SHORT).show();
                return;
            }
            View view = EasyFloat.getView(LogFloatView.class.getName());
            if (view == null) {
                new LogFloatView(requireContext()).show();
            } else {
                EasyFloat.dismiss(LogFloatView.class.getName());
            }
        });
        refreshSwitchState();


        // 权限
        binding.superUserGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                View view = group.findViewById(checkedId);
                int type = group.indexOfChild(view);
                switch (type) {
                    case 1 -> {
                        if (ShizukuSuperUser.existShizuku()) {
                            SettingSave.getInstance().setSuperUserType(type);
                            if (SuperUser.getSuperUserType() != type) {
                                SuperUser.exit();
                                if (!SuperUser.init()) {
                                    SettingSave.getInstance().setSuperUserType(0);
                                    binding.superUserGroup.check(R.id.noSuperUserButton);
                                }
                            }
                        } else {
                            binding.superUserGroup.check(R.id.noSuperUserButton);
                            Toast.makeText(requireContext(), R.string.permission_setting_super_user_no_shizuku, Toast.LENGTH_SHORT).show();
                        }
                    }
                    case 2 -> {
                        if (RootSuperUser.existRoot()) {
                            SettingSave.getInstance().setSuperUserType(type);
                            if (SuperUser.getSuperUserType() != type) SuperUser.tryInit();
                        } else {
                            binding.superUserGroup.check(R.id.noSuperUserButton);
                            Toast.makeText(requireContext(), R.string.permission_setting_super_user_no_root, Toast.LENGTH_SHORT).show();
                        }
                    }
                    default -> {
                        SuperUser.exit();
                        SettingSave.getInstance().setSuperUserType(type);
                    }
                }
            }
        });
        int superUserType = SettingSave.getInstance().getSuperUserType();
        switch (superUserType) {
            case 1 -> {
                if (!ShizukuSuperUser.existShizuku()) superUserType = 0;
            }
            case 2 -> {
                if (!RootSuperUser.existRoot()) superUserType = 0;
            }
        }
        binding.superUserGroup.check(binding.superUserGroup.getChildAt(superUserType).getId());
        binding.superUserGroup.setSaveEnabled(false);


        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        binding.useExactAlarmSwitch.setOnClickListener(v -> {
            if (binding.useExactAlarmSwitch.isChecked()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        SettingSave.getInstance().setUseExactAlarm(true);
                    } else {
                        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                        startActivity(intent);
                        binding.useExactAlarmSwitch.setChecked(false);
                    }
                } else {
                    SettingSave.getInstance().setUseExactAlarm(true);
                }
            } else {
                SettingSave.getInstance().setUseExactAlarm(false);
            }
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service != null && service.isServiceEnabled()) {
                service.resetAlarm();
            }
        });

        boolean useExactAlarm = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms();
        binding.useExactAlarmSwitch.setChecked(SettingSave.getInstance().isUseExactAlarm() && useExactAlarm);
        binding.useExactAlarmSwitch.setSaveEnabled(false);


        binding.useOcrSwitch.setOnClickListener(v -> {
            if (binding.useOcrSwitch.isChecked()) {
                if (Predictor.initOcr()) {
                    SettingSave.getInstance().setUseOcr(true);
                } else {
                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle(R.string.dialog_title)
                            .setMessage(R.string.permission_setting_ocr_open_tips)
                            .setPositiveButton(R.string.permission_setting_ocr_download, (dialog, which) -> {
                                dialog.dismiss();
                                AppUtils.gotoUrl(requireContext(), getString(R.string.permission_setting_ocr_download_url));
                            })
                            .setNegativeButton(R.string.permission_setting_ocr_import, (dialog, which) -> {
                                dialog.dismiss();
                                activity.launcherContent((code, intent) -> {
                                    if (code == Activity.RESULT_OK) {
                                        Uri uri = intent.getData();
                                        if (uri != null) {
                                            if (Predictor.importModel(uri)) {
                                                SettingSave.getInstance().setUseOcr(true);
                                                binding.useOcrSwitch.setChecked(true);
                                            } else {
                                                Toast.makeText(getContext(), R.string.permission_setting_ocr_import_error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                            })
                            .setNeutralButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                            .show();
                    binding.useOcrSwitch.setChecked(false);
                }
            } else {
                SettingSave.getInstance().setUseOcr(false);
                Predictor.destroy();
            }
        });
        binding.useOcrSwitch.setChecked(SettingSave.getInstance().isUseOcr());

        binding.useBluetoothSwitch.setOnClickListener(v -> {
            if (binding.useBluetoothSwitch.isChecked()) {
                binding.useBluetoothSwitch.setChecked(false);
                activity.launcherBluetooth((code, intent) -> {
                    SettingSave.getInstance().setUseBluetooth(activity, code == Activity.RESULT_OK);
                    binding.useBluetoothSwitch.setChecked(code == Activity.RESULT_OK);
                });
            } else {
                SettingSave.getInstance().setUseBluetooth(activity, false);
            }
        });
        binding.useBluetoothSwitch.setChecked(SettingSave.getInstance().getUseBluetooth(requireContext()));

        // 任务设置
        binding.taskBackupButton.setOnClickListener(v -> {
            HandleFunctionContextView view = new HandleFunctionContextView(requireContext());
            new MaterialAlertDialogBuilder(requireContext())
                    .setPositiveButton(R.string.enter, (dialog, which) -> {
                        ArrayList<FunctionContext> functionContexts = view.getSelectFunctionContext();
                        AppUtils.backupFunctionContexts(requireContext(), functionContexts);
                        dialog.dismiss();
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                    .setView(view)
                    .setTitle(R.string.task_setting_backup)
                    .show();
        });

        binding.taskImportButton.setOnClickListener(v -> activity.launcherContent((code, intent) -> {
            if (code == Activity.RESULT_OK) {
                Uri uri = intent.getData();
                if (uri != null) {
                    activity.saveTasks(uri);
                }
            }
        }));

        binding.playViewVisibleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                View view = group.findViewById(checkedId);
                int type = group.indexOfChild(view);
                SettingSave.getInstance().setPlayViewVisibleType(type);
            }
        });

        binding.resetPlayViewPos.setOnClickListener(v -> {
            SettingSave.getInstance().setPlayViewPosition(new Point());
            SettingSave.getInstance().setChoiceViewPosition(new Point());
            Toast.makeText(getContext(), R.string.task_setting_play_view_visible_reset, Toast.LENGTH_SHORT).show();
        });

        binding.showTouchSwitch.setOnClickListener(v -> SettingSave.getInstance().setShowTouch(binding.showTouchSwitch.isChecked()));
        binding.showTouchSwitch.setChecked(SettingSave.getInstance().isShowTouch());

        binding.showTaskSwitch.setOnClickListener(v -> SettingSave.getInstance().setFirstShowTask(binding.showTaskSwitch.isChecked()));
        binding.showTaskSwitch.setChecked(SettingSave.getInstance().isFirstShowTask());


        // 偏好设置
        binding.lookBlueprintSwitch.setOnClickListener(v -> SettingSave.getInstance().setFirstLookBlueprint(binding.lookBlueprintSwitch.isChecked()));
        binding.lookBlueprintSwitch.setChecked(SettingSave.getInstance().isFirstLookBlueprint());

        binding.startViewVisibleSwitch.setOnClickListener(v -> SettingSave.getInstance().setShowStart(binding.startViewVisibleSwitch.isChecked()));
        binding.startViewVisibleSwitch.setChecked(SettingSave.getInstance().isShowStart());

        binding.useTakeCaptureSwitch.setOnClickListener(v -> SettingSave.getInstance().setUseTakeCapture(binding.useTakeCaptureSwitch.isChecked()));
        binding.useTakeCaptureSwitch.setChecked(SettingSave.getInstance().isUseTakeCapture());


        // 界面外观
        binding.nightModeGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                View view = group.findViewById(checkedId);
                SettingSave.getInstance().setNightMode(group.indexOfChild(view));
            }
        });
        binding.nightModeGroup.check(binding.nightModeGroup.getChildAt(SettingSave.getInstance().getNightMode()).getId());

        binding.dynamicColorSwitch.setOnClickListener(v -> SettingSave.getInstance().setDynamicColor(requireContext(), binding.dynamicColorSwitch.isChecked()));
        binding.dynamicColorSwitch.setChecked(SettingSave.getInstance().isDynamicColor());


        PackageManager packageManager = requireContext().getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(requireContext().getPackageName(), 0);
            binding.versionText.setText(packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        binding.updateButton.setOnClickListener(v -> AppUtils.gotoUrl(getContext(), getString(R.string.app_info_join_qq_url)));
        binding.sourceCodeButton.setOnClickListener(v -> AppUtils.gotoUrl(getContext(), getString(R.string.app_info_join_github_url)));

        binding.thankButton.setOnClickListener(v -> new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.app_info_setting_thank_title)
                .setView(R.layout.dialog_thank)
                .show());

        return binding.getRoot();
    }


}
