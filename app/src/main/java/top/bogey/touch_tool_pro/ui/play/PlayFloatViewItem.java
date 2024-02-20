package top.bogey.touch_tool_pro.ui.play;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.start.ManualStartAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.task.Task;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.bean.task.TaskRunningListener;
import top.bogey.touch_tool_pro.databinding.FloatPlayItemBinding;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;


@SuppressLint("ViewConstructor")
public class PlayFloatViewItem extends FrameLayout implements TaskRunningListener {
    private final FloatPlayItemBinding binding;

    private final Task task;
    private final ManualStartAction startAction;
    private final String title;
    private TaskRunnable runnable;
    private boolean playing = false;
    private boolean needRemove = false;

    private long lastRefreshTime;

    public PlayFloatViewItem(@NonNull Context context, Task task, ManualStartAction startAction) {
        super(context);
        this.task = task;
        this.startAction = startAction;

        binding = FloatPlayItemBinding.inflate(LayoutInflater.from(context), this, true);

        String title = startAction.getDescription();
        if (title == null || title.isEmpty()) {
            title = task.getTitle();
        }
        this.title = getPivotalTitle(title);
        binding.percent.setText(this.title);

        binding.playButton.setOnClickListener(v -> {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            // 录屏服务没开启，需要检查涉及图片或颜色的动作
            if (service != null && !service.isCaptureEnabled()) {
                if (task.needCaptureService()) {
                    service.showToast(context.getString(R.string.capture_service_on_tips));
                    service.startCaptureService(true, null);
                } else {
                    startPlay();
                }
            } else {
                startPlay();
            }
        });

        refreshProgress(0);
    }

    private void startPlay() {
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service != null && service.isServiceConnected()) {
            if (playing) {
                if (runnable != null) {
                    if (runnable.isInterrupt()) {
                        onEnd(runnable);
                    } else {
                        runnable.stop();
                    }
                }
            } else {
                runnable = service.runTask(task, startAction, (FunctionContext) task.copy(), this);
            }
        }
    }

    public ManualStartAction getStartAction() {
        return startAction;
    }

    public boolean isFree() {
        return !playing;
    }

    public void setNeedRemove(boolean needRemove) {
        this.needRemove = needRemove;
    }

    private String getPivotalTitle(String title) {
        if (title == null || title.isEmpty()) return "?";
        Pattern pattern = Pattern.compile("[\"|“](.*)[\"|”]");
        Matcher matcher = pattern.matcher(title);
        if (matcher.find()) {
            String group = matcher.group(1);
            if (group != null) return group.substring(0, 1);
        }
        return title.substring(0, 1);
    }

    private void refreshProgress(int progress) {
        if (System.currentTimeMillis() - lastRefreshTime < 100) return;
        lastRefreshTime = System.currentTimeMillis();
        post(() -> {
            binding.playButton.setIndeterminate(playing);
            binding.percent.setText(progress == 0 ? title : String.valueOf(progress));
        });
    }

    @Override
    public void onStart(TaskRunnable runnable) {
        playing = true;
        lastRefreshTime = 0;
        refreshProgress(0);
    }

    @Override
    public void onEnd(TaskRunnable runnable) {
        playing = false;
        lastRefreshTime = 0;
        refreshProgress(0);
        if (needRemove) {
            post(() -> {
                ViewParent parent = getParent();
                if (parent != null) ((ViewGroup) parent).removeView(this);
            });
        }
    }

    @Override
    public void onProgress(TaskRunnable runnable, Action action, int progress, boolean execute) {
        playing = true;
        refreshProgress(progress);
    }
}
