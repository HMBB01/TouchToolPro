package top.bogey.touch_tool_pro.ui.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.databinding.FloatPickerChangeAreaBinding;
import top.bogey.touch_tool_pro.utils.easy_float.EasyFloat;
import top.bogey.touch_tool_pro.utils.easy_float.FloatGravity;
import top.bogey.touch_tool_pro.utils.easy_float.FloatViewInterface;

@SuppressLint("ViewConstructor")
public class ChangeAreaFloatView extends FrameLayout implements FloatViewInterface {
    private final FloatPickerChangeAreaBinding binding;

    private int offset = 1;
    private int xScale = 0;
    private int yScale = 0;

    private ChangeAreaFloatView(@NonNull Context context) {
        super(context);
        binding = FloatPickerChangeAreaBinding.inflate(LayoutInflater.from(context), this, true);

        binding.countButton1.setOnClickListener(v -> setOffset(1));
        binding.countButton2.setOnClickListener(v -> setOffset(2));
        binding.countButton5.setOnClickListener(v -> setOffset(5));
        binding.countButton10.setOnClickListener(v -> setOffset(10));
        binding.countButton10.setChecked(true);

        binding.topLeftButton.setOnClickListener(v -> setScale(-1, -1));
        binding.topRightButton.setOnClickListener(v -> setScale(1, -1));
        binding.bottomLeftButton.setOnClickListener(v -> setScale(-1, 1));
        binding.bottomRightButton.setOnClickListener(v -> setScale(1, 1));
        binding.middleButton.setOnClickListener(v -> setScale(0, 0));
        binding.middleButton.setChecked(true);

        binding.closeButton.setOnClickListener(v -> dismiss());
    }

    public ChangeAreaFloatView(@NonNull Context context, Point point, PointChangeCallback callback) {
        this(context);

        binding.topLeftButton.setEnabled(false);
        binding.topRightButton.setEnabled(false);
        binding.bottomLeftButton.setEnabled(false);
        binding.bottomRightButton.setEnabled(false);
        binding.middleButton.setEnabled(false);

        setTouchListener(binding.topButton, () -> {
            point.offset(0, -offset);
            callback.onChanged(point);
        });

        setTouchListener(binding.bottomButton, () -> {
            point.offset(0, offset);
            callback.onChanged(point);
        });

        setTouchListener(binding.leftButton, () -> {
            point.offset(-offset, 0);
            callback.onChanged(point);
        });

        setTouchListener(binding.rightButton, () -> {
            point.offset(offset, 0);
            callback.onChanged(point);
        });
    }

    public ChangeAreaFloatView(@NonNull Context context, Rect area, AreaChangeCallback callback) {
        this(context);

        setTouchListener(binding.topButton, () -> {
            switch (yScale) {
                case -1 -> area.top -= offset;
                case 0 -> area.offset(0, -offset);
                case 1 -> area.bottom -= offset;
            }
            callback.onChanged(area);
        });

        setTouchListener(binding.bottomButton, () -> {
            switch (yScale) {
                case -1 -> area.top += offset;
                case 0 -> area.offset(0, offset);
                case 1 -> area.bottom += offset;
            }
            callback.onChanged(area);
        });

        setTouchListener(binding.leftButton, () -> {
            switch (xScale) {
                case -1 -> area.left -= offset;
                case 0 -> area.offset(-offset, 0);
                case 1 -> area.right -= offset;
            }
            callback.onChanged(area);
        });

        setTouchListener(binding.rightButton, () -> {
            switch (xScale) {
                case -1 -> area.left += offset;
                case 0 -> area.offset(offset, 0);
                case 1 -> area.right += offset;
            }
            callback.onChanged(area);
        });
    }

    private void setOffset(int offset) {
        this.offset = offset;
        binding.countButton1.setChecked(false);
        binding.countButton2.setChecked(false);
        binding.countButton5.setChecked(false);
        binding.countButton10.setChecked(false);
        switch (offset) {
            case 1 -> binding.countButton1.setChecked(true);
            case 2 -> binding.countButton2.setChecked(true);
            case 5 -> binding.countButton5.setChecked(true);
            case 10 -> binding.countButton10.setChecked(true);
        }
    }

    private void setScale(int xScale, int yScale) {
        this.xScale = xScale;
        this.yScale = yScale;
        binding.topLeftButton.setChecked(false);
        binding.topRightButton.setChecked(false);
        binding.bottomLeftButton.setChecked(false);
        binding.bottomRightButton.setChecked(false);
        binding.middleButton.setChecked(false);

        if (xScale == 0 && yScale == 0) {
            binding.middleButton.setChecked(true);
        } else if (xScale == 1) {
            (yScale == 1 ? binding.bottomRightButton : binding.topRightButton).setChecked(true);
        } else if (xScale == -1) {
            (yScale == 1 ? binding.bottomLeftButton : binding.topLeftButton).setChecked(true);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchListener(Button button, OnTouchListener listener) {
        Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                listener.onTick();
                handler.postDelayed(this, 50);
            }
        };

        button.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN -> {
                    listener.onTick();
                    handler.postDelayed(runnable, 300);
                }
                case MotionEvent.ACTION_UP, MotionEvent.ACTION_MOVE -> handler.removeCallbacksAndMessages(null);
            }
            return true;
        });
    }


    @Override
    public void show() {
        EasyFloat.with(MainApplication.getInstance().getService())
                .setLayout(this)
                .setTag(ChangeAreaFloatView.class.getName())
                .setDragEnable(true)
                .setGravity(FloatGravity.CENTER, 0, 0)
                .setAnimator(null)
                .show();
    }

    @Override
    public void dismiss() {
        EasyFloat.dismiss(ChangeAreaFloatView.class.getName());
    }

    public interface AreaChangeCallback {
        void onChanged(Rect area);
    }

    public interface PointChangeCallback {
        void onChanged(Point pos);
    }

    private interface OnTouchListener {
        void onTick();
    }
}
