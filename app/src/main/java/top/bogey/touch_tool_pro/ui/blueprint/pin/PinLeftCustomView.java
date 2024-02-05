package top.bogey.touch_tool_pro.ui.blueprint.pin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinObject;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArray;
import top.bogey.touch_tool_pro.databinding.PinDebugValueBinding;
import top.bogey.touch_tool_pro.databinding.PinLeftCustomBinding;
import top.bogey.touch_tool_pro.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool_pro.utils.DisplayUtils;
import top.bogey.touch_tool_pro.utils.SpinnerSelectedListener;
import top.bogey.touch_tool_pro.utils.TextChangedListener;

@SuppressLint("ViewConstructor")
public class PinLeftCustomView extends PinCustomView {
    private final PinLeftCustomBinding binding;
    private final ArrayList<PinType> pinTypes = new ArrayList<>();

    public PinLeftCustomView(@NonNull Context context, ActionCard<?> card, Pin pin) {
        super(context, card, pin);
        binding = PinLeftCustomBinding.inflate(LayoutInflater.from(context), this, true);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.pin_widget_spinner_item);
        for (PinType pinType : PinType.values()) {
            if (pinType.getConfig().isCanCustom()) {
                pinTypes.add(pinType);
                adapter.add(pinType.getConfig().getTitle());
            }
        }

        binding.spinner.setAdapter(adapter);
        binding.spinner.setOnItemSelectedListener(new SpinnerSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PinType pinType = pinTypes.get(position);
                PinValue pinValue = functionPin.getValue(PinValue.class);
                if (pinValue != null && pinType == pinValue.getType()) return;

                Class<? extends PinObject> pinObjectClass = pinType.getConfig().getPinClass();
                if (pinObjectClass == null) return;

                try {
                    PinObject pinObject = pinObjectClass.newInstance();
                    if (pinObject instanceof PinValueArray array) array.setCanChange(false);
                    functionPin.setValue(pinObject);
                } catch (IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                }

            }
        });

        binding.editText.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(functionPin.getTitle())) return;
                functionPin.setTitle(s.toString());
            }
        });

        initRemoveButton(binding.removeButton);
        refreshPinView();
    }

    @Override
    public void refreshPinUI() {
        binding.pinSlot.setStrokeColor(getPinColor());
        binding.pinSlot.setShapeAppearanceModel(getPinStyle());

        boolean empty = pin.getLinks().isEmpty();
        binding.pinSlot.setCardBackgroundColor(empty ? DisplayUtils.getAttrColor(getContext(), com.google.android.material.R.attr.colorSurfaceVariant, 0) : getPinColor());

        int index = pinTypes.indexOf(functionPin.getValue().getType());
        if (index == -1) index = 0;
        binding.spinner.setSelection(index);
        if (!binding.editText.hasFocus() && functionPin.getTitle() != null) {
            binding.editText.setText(functionPin.getTitle());
        }
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
