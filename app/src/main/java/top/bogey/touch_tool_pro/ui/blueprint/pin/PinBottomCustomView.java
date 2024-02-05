package top.bogey.touch_tool_pro.ui.blueprint.pin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.databinding.PinBottomCustomBinding;
import top.bogey.touch_tool_pro.databinding.PinDebugValueBinding;
import top.bogey.touch_tool_pro.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool_pro.utils.DisplayUtils;
import top.bogey.touch_tool_pro.utils.TextChangedListener;

@SuppressLint("ViewConstructor")
public class PinBottomCustomView extends PinCustomView {
    private final PinBottomCustomBinding binding;

    public PinBottomCustomView(@NonNull Context context, ActionCard<?> card, Pin pin) {
        super(context, card, pin);
        binding = PinBottomCustomBinding.inflate(LayoutInflater.from(context), this, true);

        initRemoveButton(binding.removeButton);
        refreshPinUI();

        binding.editText.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(functionPin.getTitle())) return;
                functionPin.setTitle(s.toString());
            }
        });
    }

    @Override
    public void refreshPinUI() {
        binding.pinSlot.setStrokeColor(getPinColor());
        binding.pinSlot.setShapeAppearanceModel(getPinStyle());

        boolean empty = pin.getLinks().isEmpty();
        binding.pinSlot.setCardBackgroundColor(empty ? DisplayUtils.getAttrColor(getContext(), com.google.android.material.R.attr.colorSurfaceVariant, 0) : getPinColor());

        if (!binding.editText.hasFocus() && functionPin.getTitle() != null) {
            binding.editText.setText(functionPin.getTitle());
        }
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
        return null;
    }

    @Override
    public PinDebugValueBinding getDebugValueView() {
        return null;
    }

}
