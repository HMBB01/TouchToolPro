package top.bogey.touch_tool_pro.ui.home;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.databinding.ViewHomeBinding;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;
import top.bogey.touch_tool_pro.utils.super_user.SuperUser;
import top.bogey.touch_tool_pro.ui.BaseActivity;
import top.bogey.touch_tool_pro.ui.MainActivity;
import top.bogey.touch_tool_pro.utils.AppUtils;
import top.bogey.touch_tool_pro.utils.DisplayUtils;
import top.bogey.touch_tool_pro.utils.SettingSave;


public class HomeView extends Fragment {
    private ViewHomeBinding binding;

    private final MenuProvider menuProvider = new MenuProvider() {
        @Override
        public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
            menuInflater.inflate(R.menu.menu_home, menu);
        }

        @Override
        public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.restartService) {
                MainActivity activity = MainApplication.getInstance().getMainActivity();
                boolean result = activity.stopAccessibilityServiceBySecurePermission();
                if (!result) {
                    Toast.makeText(activity, R.string.restart_accessibility_service_error, Toast.LENGTH_SHORT).show();
                } else {
                    binding.getRoot().postDelayed(() -> {
                        SettingSave.getInstance().setServiceEnabled(true);
                        activity.restartAccessibilityServiceBySecurePermission();
                        Toast.makeText(activity, R.string.restart_accessibility_service_complete, Toast.LENGTH_SHORT).show();
                    }, 1000);
                }
                return true;
            }
            return false;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        binding.ignoreBatteryBox.setVisibility(AppUtils.isIgnoredBattery(requireContext()) ? View.GONE : View.VISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ViewHomeBinding.inflate(inflater, container, false);
        requireActivity().addMenuProvider(menuProvider, getViewLifecycleOwner());

        binding.serviceButton.setOnClickListener(v -> {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service == null || !service.isServiceConnected()) {
                AppUtils.showDialog(getContext(), getString(R.string.accessibility_service_on_tips, getString(R.string.app_name)), result -> {
                    if (result) {
                        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        startActivity(intent);
                    }
                });
            } else {
                if (service.isServiceEnabled()) {
                    service.setServiceEnabled(false);
                } else {
                    if (SettingSave.getInstance().isServiceEnabledTip()) {
                        service.setServiceEnabled(true);
                    } else {
                        AppUtils.showDialog(getContext(), R.string.service_des, result -> {
                            if (result) {
                                service.setServiceEnabled(true);
                                SettingSave.getInstance().setServiceEnabledTip(true);
                            }
                        });
                    }
                }
            }
        });
        MainAccessibilityService.serviceConnected.observe(getViewLifecycleOwner(), aBoolean -> binding.accessibilityServiceTitle.setText(aBoolean ? R.string.accessibility_service_on : R.string.accessibility_service_off));

        MainAccessibilityService.serviceEnabled.observe(getViewLifecycleOwner(), aBoolean -> {
            if (MainApplication.getInstance().getService() == null) return;
            binding.serviceTitle.setText(aBoolean ? R.string.service_on : R.string.service_off);
            setServiceChecked(aBoolean);
        });

        binding.captureServiceButton.setOnClickListener(v -> ((BaseActivity) requireActivity()).launchNotification((code, intent) -> {
            if (code == Activity.RESULT_OK) {
                MainAccessibilityService service = MainApplication.getInstance().getService();
                if (service != null && service.isServiceConnected()) {
                    if (SettingSave.getInstance().isUseTakeCapture() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        Toast.makeText(getContext(), R.string.capture_service_open_tips, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (service.isCaptureEnabled()) {
                        service.stopCaptureService();
                    } else {
                        if (SettingSave.getInstance().isCaptureServiceEnabledTip()) {
                            service.startCaptureService(false, null);
                        } else {
                            AppUtils.showDialog(getContext(), R.string.capture_service_des, result -> {
                                if (result) {
                                    service.startCaptureService(false, null);
                                    SettingSave.getInstance().setCaptureServiceEnabledTip(true);
                                }
                            });
                        }
                    }
                } else {
                    Toast.makeText(getContext(), R.string.accessibility_service_off, Toast.LENGTH_SHORT).show();
                }
            }
        }));
        MainAccessibilityService.captureEnabled.observe(getViewLifecycleOwner(), aBoolean -> {
            binding.captureServiceTitle.setText(aBoolean ? R.string.capture_service_on : R.string.capture_service_off);
            setCaptureChecked(aBoolean);
        });

        binding.ignoreBatteryBox.setVisibility(AppUtils.isIgnoredBattery(requireContext()) ? View.GONE : View.VISIBLE);
        binding.ignoreBatteryButton.setOnClickListener(v -> AppUtils.gotoBatterySetting(getContext()));
        binding.autoStartButton.setOnClickListener(v -> AppUtils.gotoAppDetailSetting(getContext()));
        binding.lockBackgroundButton.setOnClickListener(v -> {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service != null && service.isServiceConnected()) {
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
            } else {
                Toast.makeText(getContext(), R.string.accessibility_service_off_tips, Toast.LENGTH_SHORT).show();
            }
        });
        binding.tutorialButton.setOnClickListener(v -> AppUtils.gotoUrl(getContext(), getString(R.string.app_info_goto_doc_url)));

        binding.restartButton.setOnClickListener(v -> {
            ClipboardManager manager = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText(getString(R.string.app_name), String.format("pm grant %s %s", requireActivity().getPackageName(), Manifest.permission.WRITE_SECURE_SETTINGS));
            manager.setPrimaryClip(clipData);
            Toast.makeText(requireContext(), R.string.report_running_error_copied, Toast.LENGTH_SHORT).show();
        });

        binding.execRestartButton.setOnClickListener(v -> {
            SuperUser.runCommand(String.format("pm grant %s %s", requireActivity().getPackageName(), Manifest.permission.WRITE_SECURE_SETTINGS));
            Toast.makeText(requireContext(), R.string.shell_code_executed, Toast.LENGTH_SHORT).show();
        });

        binding.autoAllowButton.setOnClickListener(v -> {
            ClipboardManager manager = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText(getString(R.string.app_name), String.format("appops set %s PROJECT_MEDIA allow", requireActivity().getPackageName()));
            manager.setPrimaryClip(clipData);
            Toast.makeText(requireContext(), R.string.report_running_error_copied, Toast.LENGTH_SHORT).show();
        });

        binding.execAutoAllowButton.setOnClickListener(v -> {
            SuperUser.runCommand(String.format("appops set %s PROJECT_MEDIA allow", requireActivity().getPackageName()));
            Toast.makeText(requireContext(), R.string.shell_code_executed, Toast.LENGTH_SHORT).show();
        });

        setSuperShow();

        return binding.getRoot();
    }

    private void setServiceChecked(boolean checked) {
        if (checked) {
            binding.serviceButton.setCardBackgroundColor(DisplayUtils.getAttrColor(requireContext(), com.google.android.material.R.attr.colorPrimary, 0));
            binding.serviceIcon.setImageTintList(ColorStateList.valueOf(DisplayUtils.getAttrColor(requireContext(), com.google.android.material.R.attr.colorOnPrimary, 0)));
            binding.serviceTitle.setTextColor(DisplayUtils.getAttrColor(requireContext(), com.google.android.material.R.attr.colorOnPrimary, 0));
            binding.accessibilityServiceTitle.setTextColor(DisplayUtils.getAttrColor(requireContext(), com.google.android.material.R.attr.colorOnPrimary, 0));
        } else {
            binding.serviceButton.setCardBackgroundColor(DisplayUtils.getAttrColor(requireContext(), com.google.android.material.R.attr.colorSurfaceVariant, 0));
            binding.serviceIcon.setImageTintList(ColorStateList.valueOf(DisplayUtils.getAttrColor(requireContext(), com.google.android.material.R.attr.colorPrimary, 0)));
            binding.serviceTitle.setTextColor(DisplayUtils.getAttrColor(requireContext(), com.google.android.material.R.attr.colorPrimary, 0));
            binding.accessibilityServiceTitle.setTextColor(DisplayUtils.getAttrColor(requireContext(), com.google.android.material.R.attr.colorPrimary, 0));
        }
    }

    private void setCaptureChecked(boolean checked) {
        if (checked) {
            binding.captureServiceButton.setCardBackgroundColor(DisplayUtils.getAttrColor(requireContext(), com.google.android.material.R.attr.colorPrimary, 0));
            binding.captureServiceIcon.setImageTintList(ColorStateList.valueOf(DisplayUtils.getAttrColor(requireContext(), com.google.android.material.R.attr.colorOnPrimary, 0)));
            binding.captureServiceTitle.setTextColor(DisplayUtils.getAttrColor(requireContext(), com.google.android.material.R.attr.colorOnPrimary, 0));
        } else {
            binding.captureServiceButton.setCardBackgroundColor(DisplayUtils.getAttrColor(requireContext(), com.google.android.material.R.attr.colorSurfaceVariant, 0));
            binding.captureServiceIcon.setImageTintList(ColorStateList.valueOf(DisplayUtils.getAttrColor(requireContext(), com.google.android.material.R.attr.colorPrimary, 0)));
            binding.captureServiceTitle.setTextColor(DisplayUtils.getAttrColor(requireContext(), com.google.android.material.R.attr.colorPrimary, 0));
        }
    }

    private void setSuperShow() {
        if (SuperUser.isSuperUser()) {
            binding.execRestartButton.setVisibility(View.VISIBLE);
            binding.execAutoAllowButton.setVisibility(View.VISIBLE);
        } else {
            binding.execRestartButton.setVisibility(View.GONE);
            binding.execAutoAllowButton.setVisibility(View.GONE);
        }
    }
}
