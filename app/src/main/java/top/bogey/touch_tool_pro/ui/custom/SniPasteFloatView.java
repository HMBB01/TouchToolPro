package top.bogey.touch_tool_pro.ui.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import java.util.UUID;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.databinding.FloatSniPasteBinding;
import top.bogey.touch_tool_pro.utils.DisplayUtils;
import top.bogey.touch_tool_pro.utils.easy_float.EasyFloat;
import top.bogey.touch_tool_pro.utils.easy_float.FloatGravity;
import top.bogey.touch_tool_pro.utils.easy_float.FloatViewInterface;

@SuppressLint("ViewConstructor")
public class SniPasteFloatView extends FrameLayout implements FloatViewInterface {
    private final FloatSniPasteBinding binding;
    private final String tag;

    private boolean needDelete = false;

    public SniPasteFloatView(@NonNull Context context) {
        super(context);
        binding = FloatSniPasteBinding.inflate(LayoutInflater.from(context), this, true);
        tag = UUID.randomUUID().toString();

        binding.getRoot().setOnClickListener(v -> {
            if (needDelete) {
                dismiss();
            } else {
                needDelete = true;
                postDelayed(() -> needDelete = false, 1500);
            }
        });
    }

    public SniPasteFloatView(@NonNull Context context, String msg) {
        this(context);
        binding.title.setVisibility(VISIBLE);
        binding.title.setText(msg);
    }

    public SniPasteFloatView(@NonNull Context context, Bitmap image) {
        this(context);
        binding.image.setVisibility(VISIBLE);
        binding.image.setImageBitmap(image);
    }

    public SniPasteFloatView(@NonNull Context context, int[] color) {
        this(context);
        binding.image.setVisibility(VISIBLE);
        binding.image.setImageTintList(ColorStateList.valueOf(DisplayUtils.getColorFromHsv(color)));
    }

    @Override
    public void show() {
        EasyFloat.with(MainApplication.getInstance().getService())
                .setLayout(this)
                .setGravity(FloatGravity.CENTER, 0, 0)
                .setTag(tag)
                .setAlwaysShow(true)
                .show();
    }

    @Override
    public void dismiss() {
        EasyFloat.dismiss(tag);
    }
}
