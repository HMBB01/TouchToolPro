package top.bogey.touch_tool_pro.ui.blueprint.card;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;

import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.function.FunctionEndAction;
import top.bogey.touch_tool_pro.bean.action.function.FunctionInnerAction;
import top.bogey.touch_tool_pro.bean.action.function.FunctionPinsAction;
import top.bogey.touch_tool_pro.bean.action.function.FunctionStartAction;
import top.bogey.touch_tool_pro.bean.function.Function;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinExecute;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.databinding.CardCustomBinding;
import top.bogey.touch_tool_pro.ui.blueprint.CardLayoutView;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinBottomCustomView;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinLeftCustomView;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinRightCustomView;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinTopCustomView;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinView;
import top.bogey.touch_tool_pro.utils.DisplayUtils;

@SuppressLint("ViewConstructor")
public class FunctionCard extends ActionCard<FunctionInnerAction> {
    private final CardCustomBinding cardBinding;

    public FunctionCard(Context context, Function function, FunctionInnerAction action) {
        super(context, function, action);

        binding.copyButton.setOnClickListener(v -> {
            if (action instanceof FunctionStartAction) return;
            Action copy = (Action) action.copy();
            copy.newInfo();
            ((CardLayoutView) getParent()).addAction(copy);
        });

        binding.removeButton.setOnClickListener(v -> {
            if (action instanceof FunctionStartAction) return;

            ArrayList<Action> actions = function.getActionsByClass(FunctionEndAction.class);
            if (actions.size() > 1) {
                if (needDelete) {
                    ((CardLayoutView) getParent()).removeAction(action);
                } else {
                    binding.removeButton.setChecked(true);
                    needDelete = true;
                    postDelayed(() -> {
                        binding.removeButton.setChecked(false);
                        needDelete = false;
                    }, 1500);
                }
            }
        });

        boolean isStart = action instanceof FunctionStartAction;
        if (!isStart) binding.editButton.setVisibility(GONE);

        cardBinding = CardCustomBinding.inflate(LayoutInflater.from(context), isStart ? binding.topBox : binding.bottomBox, true);
        cardBinding.addPinButton.setOnClickListener(v -> {
            Pin pin = new Pin(new PinString(), !isStart);
            function.getAction().addPin(pin);
        });

        cardBinding.addExecuteButton.setOnClickListener(v -> {
            Pin pin = new Pin(new PinExecute(), !isStart);
            function.getAction().addPin(pin);
        });

        cardBinding.justCallSwitch.setChecked(function.isJustCall());
        cardBinding.justCallSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> function.setJustCall(isChecked));

        cardBinding.fastEndSwitch.setChecked(function.isFastEnd());
        cardBinding.fastEndSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> function.setFastEnd(isChecked));

        cardBinding.stateBox.setVisibility(isStart ? VISIBLE : GONE);
        cardBinding.fastEndBox.setVisibility(isStart ? GONE : VISIBLE);
    }

    @Override
    protected void addPinView(Pin pin, int offset) {
        PinView pinView;
        if (pin.isOut()) {
            if (pin.isVertical()) {
                pinView = new PinBottomCustomView(getContext(), this, pin);
                binding.bottomBox.addView(pinView, binding.bottomBox.getChildCount() - offset);
            } else {
                pinView = new PinRightCustomView(getContext(), this, pin);
                binding.outBox.addView(pinView, binding.outBox.getChildCount() - offset);
            }
        } else {
            if (pin.isVertical()) {
                pinView = new PinTopCustomView(getContext(), this, pin);
                binding.topBox.addView(pinView, binding.topBox.getChildCount() - offset);
            } else {
                pinView = new PinLeftCustomView(getContext(), this, pin);
                binding.inBox.addView(pinView, binding.inBox.getChildCount() - offset);
            }
        }
        pinView.setExpand(action.isExpand(), action.isShowHide());
        pinViews.put(pin.getId(), pinView);
    }

    @Override
    public void removePin(Pin pin) {
        FunctionPinsAction action = ((Function) functionContext).getAction();
        action.removePin(action.getPinByUid(pin.getUid()));
    }

    @Override
    public boolean touchedEmpty(float x, float y) {
        float scale = getScaleX();
        ArrayList<View> views = new ArrayList<>(Arrays.asList(cardBinding.addExecuteButton, cardBinding.addPinButton));
        if (cardBinding.stateBox.getVisibility() == VISIBLE) views.add(cardBinding.justCallSwitch);
        if (cardBinding.fastEndBox.getVisibility() == VISIBLE) views.add(cardBinding.fastEndSwitch);
        for (View view : views) {
            PointF pos = DisplayUtils.getLocationInParentView(binding.getRoot(), view);
            float px = pos.x * scale;
            float py = pos.y * scale;
            float width = view.getWidth() * scale;
            float height = view.getHeight() * scale;
            if (new RectF(px, py, px + width, py + height).contains(x, y)) return false;
        }
        return super.touchedEmpty(x, y);
    }
}
