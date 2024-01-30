package top.bogey.touch_tool_pro.ui.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.bean.pin.pins.PinArea;
import top.bogey.touch_tool_pro.databinding.FloatPickerAreaBinding;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;
import top.bogey.touch_tool_pro.ui.custom.ChangeAreaFloatView;
import top.bogey.touch_tool_pro.utils.DisplayUtils;
import top.bogey.touch_tool_pro.utils.easy_float.EasyFloat;

@SuppressLint("ViewConstructor")
public class AreaPickerFloatView extends BasePickerFloatView {
    private final Rect area;
    private final FloatPickerAreaBinding binding;
    private final Paint bitmapPaint;
    private final int[] location = new int[2];
    private final Paint markPaint;
    private final int offset;
    private MainAccessibilityService service;
    private Bitmap showBitmap;
    private final Rect markArea = new Rect();

    private AdjustMode adjustMode = AdjustMode.NONE;
    private boolean isMarked = false;

    private int lastX = 0;
    private int lastY = 0;

    public AreaPickerFloatView(Context context, IPickerCallback callback, PinArea pinArea) {
        super(context, callback);
        area = pinArea.getArea(context);
        if (pinArea.getArea().isEmpty()) area.set(0, 0, 0, 0);

        floatCallback = new AreaPickerCallback(this);

        binding = FloatPickerAreaBinding.inflate(LayoutInflater.from(context), this, true);

        binding.saveButton.setOnClickListener(v -> {
            markArea.offset(location[0], location[1]);
            pinArea.setArea(context, markArea);
            if (callback != null) callback.onComplete();
            dismiss();
        });

        binding.backButton.setOnClickListener(v -> {
            if (callback != null) callback.onCancel();
            dismiss();
        });

        binding.fullButton.setOnClickListener(v -> {
            markArea.set(0, 0, getWidth(), getHeight());
            isMarked = true;
            refreshUI();
        });

        binding.detailButton.setOnClickListener(v -> new ChangeAreaFloatView(context, markArea, area -> refreshUI()).show());

        markPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markPaint.setStyle(Paint.Style.FILL);
        markPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bitmapPaint.setFilterBitmap(true);
        bitmapPaint.setDither(true);

        offset = Math.round(DisplayUtils.dp2px(context, 4));
    }

    public void realShow(int delay) {
        postDelayed(() -> {
            EasyFloat.show(tag);
            if (service != null && service.isCaptureEnabled()) {
                service.getCurrImage(bitmap -> post(() -> {
                    if (bitmap != null) {
                        showBitmap = DisplayUtils.safeCreateBitmap(bitmap, location[0], location[1], getWidth(), getHeight());
                        if (!area.isEmpty()) {
                            markArea.set(area);
                            markArea.offset(-location[0], -location[1]);
                            isMarked = true;
                        }
                    }
                    refreshUI();
                }));
            } else {
                if (!area.isEmpty()) {
                    markArea.set(area);
                    markArea.offset(-location[0], -location[1]);
                    isMarked = true;
                }
                refreshUI();
            }
        }, delay);
    }

    public void onShow() {
        service = MainApplication.getInstance().getService();
        realShow(100);
    }

    @Override
    public void dispatchDraw(@NonNull Canvas canvas) {
        if (showBitmap != null && !showBitmap.isRecycled()) {
            canvas.drawBitmap(showBitmap, 0, 0, bitmapPaint);
        }
        canvas.saveLayer(getLeft(), getTop(), getRight(), getBottom(), bitmapPaint);
        super.dispatchDraw(canvas);
        canvas.drawRect(markArea, markPaint);
        canvas.restore();

        if (isMarked) drawChild(canvas, binding.buttonBox, getDrawingTime());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        int localX = (int) event.getX();
        int localY = (int) event.getY();

        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            adjustMode = AdjustMode.NONE;
            lastX = x;
            lastY = y;

            View[] views = new View[]{binding.moveRight, binding.moveLeft, binding.markBox};
            int[] location = new int[2];
            for (int i = 0; i < views.length; i++) {
                View view = views[i];
                view.getLocationOnScreen(location);
                Rect rect = new Rect(location[0], location[1], location[0] + view.getWidth(), location[1] + view.getHeight());
                if (rect.contains(x, y)) {
                    adjustMode = AdjustMode.values()[i + AdjustMode.BOTTOM_RIGHT.ordinal()];
                    // 对可能隐藏的控件做剔除
                    if (adjustMode.ordinal() <= AdjustMode.DRAG.ordinal() && !isMarked) {
                        continue;
                    }
                    break;
                }
            }

            // 没点到控制按钮且不是拖动
            if (adjustMode == AdjustMode.NONE) {
                adjustMode = AdjustMode.MARK;
                markArea.left = localX;
                markArea.top = localY;
                markArea.right = localX;
                markArea.bottom = localY;
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            int dx = x - lastX;
            int dy = y - lastY;
            switch (adjustMode) {
                case MARK -> {
                    markArea.right = localX;
                    markArea.bottom = localY;
                }
                case DRAG -> {
                    markArea.left += dx;
                    markArea.top += dy;
                    markArea.right += dx;
                    markArea.bottom += dy;
                }
                case TOP_LEFT -> {
                    markArea.left += dx;
                    markArea.top += dy;
                }
                case BOTTOM_RIGHT -> {
                    markArea.right += dx;
                    markArea.bottom += dy;
                }
            }
            markArea.sort();
            lastX = x;
            lastY = y;
        } else if (action == MotionEvent.ACTION_UP) {
            if (adjustMode == AdjustMode.MARK) {
                if (!markArea.isEmpty()) {
                    isMarked = true;
                }
            }
        }
        refreshUI();
        return true;
    }

    private void refreshUI() {
        markArea.sort();
        markArea.left = Math.max(0, markArea.left);
        markArea.top = Math.max(0, markArea.top);
        markArea.right = Math.min(getWidth(), markArea.right);
        markArea.bottom = Math.min(getHeight(), markArea.bottom);

        binding.markBox.setVisibility(isMarked ? VISIBLE : INVISIBLE);
        binding.buttonBox.setVisibility(isMarked ? VISIBLE : INVISIBLE);
        if (isMarked) {
            ViewGroup.LayoutParams params = binding.markBox.getLayoutParams();
            params.width = markArea.width() + 2 * offset;
            params.height = markArea.height() + 2 * offset;
            binding.markBox.setLayoutParams(params);

            binding.markBox.setX(markArea.left - offset);
            binding.markBox.setY(markArea.top - offset);

            float x = markArea.left + (markArea.width() - binding.buttonBox.getWidth()) / 2f;
            x = Math.max(Math.min(x, getWidth() - binding.buttonBox.getWidth()), 0);
            binding.buttonBox.setX(x);
            if (markArea.bottom + offset * 2 + binding.buttonBox.getHeight() > getHeight()) {
                binding.buttonBox.setY(markArea.top - offset * 2 - binding.buttonBox.getHeight());
            } else {
                binding.buttonBox.setY(markArea.bottom + offset * 2);
            }
        }

        postInvalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (changed) {
            getLocationOnScreen(location);
            refreshUI();
        }
    }

    @Override
    public void dismiss() {
        EasyFloat.dismiss(ChangeAreaFloatView.class.getName());
        super.dismiss();
    }

    private enum AdjustMode {NONE, MARK, BOTTOM_RIGHT, TOP_LEFT, DRAG}

    protected static class AreaPickerCallback extends FloatBaseCallback {
        private boolean first = true;
        private final AreaPickerFloatView floatView;

        public AreaPickerCallback(AreaPickerFloatView floatView) {
            this.floatView = floatView;
        }

        @Override
        public void onShow(String tag) {
            if (first) {
                super.onShow("");
                first = false;
                floatView.onShow();
            }
        }
    }

    public static class AreaPickerInTaskCallback extends AreaPickerCallback {
        public AreaPickerInTaskCallback(AreaPickerFloatView floatView) {
            super(floatView);
        }

        @Override
        public void onDismiss() {
        }
    }
}
