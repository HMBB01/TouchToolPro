package top.bogey.touch_tool_pro.ui.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.bean.task.TaskRunningListener;
import top.bogey.touch_tool_pro.databinding.FloatKeepAliveBinding;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;
import top.bogey.touch_tool_pro.utils.DisplayUtils;
import top.bogey.touch_tool_pro.utils.SettingSave;
import top.bogey.touch_tool_pro.utils.easy_float.EasyFloat;
import top.bogey.touch_tool_pro.utils.easy_float.FloatGravity;
import top.bogey.touch_tool_pro.utils.easy_float.FloatViewInterface;

@SuppressLint("ViewConstructor")
public class KeepAliveFloatView extends FrameLayout implements FloatViewInterface, TaskRunningListener {
    private final FloatKeepAliveBinding binding;
    private final Handler handler;

    public KeepAliveFloatView(@NonNull Context context) {
        super(context);
        binding = FloatKeepAliveBinding.inflate(LayoutInflater.from(context), this, true);
        handler = new Handler();
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service != null) {
            service.addListener(this);
        }
    }

    public void showMe() {
        boolean showStart = SettingSave.getInstance().isShowStart();
        if (!showStart) return;
        post(() -> {
            binding.getRoot().animate().alpha(0.5f);
            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(this::hideMe, 1500);
        });
    }

    private void hideMe() {
        binding.getRoot().animate().alpha(0.01f);
    }

    @Override
    public void show() {
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service == null || !service.isServiceConnected()) {
            return;
        }

        EasyFloat.with(service)
                .setLayout(this)
                .setGravity(FloatGravity.TOP_CENTER, 0, Math.round(DisplayUtils.dp2px(getContext(), 2)))
                .setTag(KeepAliveFloatView.class.getCanonicalName())
                .setAlwaysShow(true)
                .setDragEnable(false)
                .setAnimator(null)
                .show();

        showMe();
        MainApplication.getInstance().setKeepView(this);
    }

    @Override
    public void dismiss() {
        EasyFloat.dismiss(KeepAliveFloatView.class.getCanonicalName());
    }

    @Override
    public void onStart(TaskRunnable runnable) {
        showMe();
    }

    @Override
    public void onEnd(TaskRunnable runnable) {

    }

    @Override
    public void onProgress(TaskRunnable runnable, Action action, int progress, boolean execute) {

    }

    @Override
    protected void onDetachedFromWindow() {
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service != null) {
            service.removeListener(this);
        }
        MainApplication.getInstance().setKeepView(null);
        super.onDetachedFromWindow();
    }
}
