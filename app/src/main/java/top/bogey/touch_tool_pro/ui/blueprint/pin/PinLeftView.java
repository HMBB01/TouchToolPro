package top.bogey.touch_tool_pro.ui.blueprint.pin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArray;
import top.bogey.touch_tool_pro.databinding.PinDebugValueBinding;
import top.bogey.touch_tool_pro.databinding.PinLeftBinding;
import top.bogey.touch_tool_pro.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool_pro.utils.DisplayUtils;

@SuppressLint("ViewConstructor")
public class PinLeftView extends PinView {
    private final PinLeftBinding binding;

    public PinLeftView(@NonNull Context context, ActionCard<?> card, Pin pin) {
        super(context, card, pin);

        binding = PinLeftBinding.inflate(LayoutInflater.from(context), this, true);
        initRemoveButton(binding.removeButton);
        refreshPinView();
    }

    @Override
    public void refreshPinView() {
        if (pin.isSameValueType(PinValueArray.class) && !pin.getValue(PinValueArray.class).isCanChange()) {
            ViewGroup viewGroup = getPinViewBox();
            if (viewGroup != null) {
                viewGroup.removeAllViews();
            }
            refreshPinUI();
        } else {
            super.refreshPinView();
        }
    }

    @Override
    public void refreshPinUI() {
        binding.pinSlot.setStrokeColor(getPinColor());
        binding.pinSlot.setShapeAppearanceModel(getPinStyle());
        binding.title.setText(pin.getTitle());

        boolean empty = pin.getLinks().isEmpty();
        binding.pinBox.setVisibility(empty ? VISIBLE : GONE);
        binding.pinSlot.setCardBackgroundColor(empty ? DisplayUtils.getAttrColor(getContext(), com.google.android.material.R.attr.colorSurfaceVariant, 0) : getPinColor());
    }

    @Override
    public PointF getSlotLocationInCard() {
        float scale = card.getScaleX();
        PointF location = DisplayUtils.getLocationInParentView(card, binding.pinSlot);
        location.x = location.x * scale;
        location.y = (location.y + binding.pinSlot.getHeight() / 2f) * scale;
        return location;
    }

    @Override
    public ViewGroup getPinViewBox() {
        return binding.pinBox;
    }

    @Override
    public PinDebugValueBinding getDebugValueView() {
        return binding.debugValue;
    }

}
