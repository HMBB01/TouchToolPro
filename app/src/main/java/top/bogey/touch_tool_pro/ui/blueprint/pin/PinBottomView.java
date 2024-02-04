package top.bogey.touch_tool_pro.ui.blueprint.pin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.databinding.PinBottomBinding;
import top.bogey.touch_tool_pro.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool_pro.utils.DisplayUtils;

@SuppressLint("ViewConstructor")
public class PinBottomView extends PinView {
    private final PinBottomBinding binding;

    public PinBottomView(@NonNull Context context, ActionCard<?> card, Pin pin) {
        super(context, card, pin);

        binding = PinBottomBinding.inflate(LayoutInflater.from(context), this, true);
        initRemoveButton(binding.removeButton);
        refreshPinView();
    }

    @Override
    public void refreshPinUI() {
        binding.pinSlot.setStrokeColor(getPinColor());
        binding.pinSlot.setShapeAppearanceModel(getPinStyle());
        binding.title.setText(pin.getTitle());

        boolean empty = pin.getLinks().isEmpty();
        binding.pinSlot.setCardBackgroundColor(empty ? DisplayUtils.getAttrColor(getContext(), com.google.android.material.R.attr.colorSurfaceVariant, 0) : getPinColor());
    }

    @Override
    public PointF getSlotLocationInCard() {
        float scale = card.getScaleX();
        PointF location = DisplayUtils.getLocationInParentView(card, binding.pinSlot);
        location.x = (location.x + binding.pinSlot.getWidth() / 2f) * scale;
        location.y = (location.y + binding.pinSlot.getHeight()) * scale;
        return location;
    }

    @Override
    public ViewGroup getPinViewBox() {
        return binding.pinBox;
    }

}
