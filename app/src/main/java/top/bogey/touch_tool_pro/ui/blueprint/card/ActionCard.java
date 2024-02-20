package top.bogey.touch_tool_pro.ui.blueprint.card;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionCheckResult;
import top.bogey.touch_tool_pro.bean.action.ActionListener;
import top.bogey.touch_tool_pro.bean.action.function.FunctionReferenceAction;
import top.bogey.touch_tool_pro.bean.function.Function;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinAdd;
import top.bogey.touch_tool_pro.bean.pin.pins.PinExecute;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.databinding.CardBaseBinding;
import top.bogey.touch_tool_pro.save.SaveRepository;
import top.bogey.touch_tool_pro.ui.blueprint.BlueprintView;
import top.bogey.touch_tool_pro.ui.blueprint.CardLayoutView;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinBottomView;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinLeftView;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinRightView;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinTopView;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinView;
import top.bogey.touch_tool_pro.utils.AppUtils;
import top.bogey.touch_tool_pro.utils.DisplayUtils;

@SuppressLint("ViewConstructor")
public class ActionCard<A extends Action> extends MaterialCardView implements ActionListener {
    protected final CardBaseBinding binding;
    protected final FunctionContext functionContext;
    protected final A action;

    protected final HashMap<String, PinView> pinViews = new HashMap<>();
    protected boolean needDelete = false;

    @SuppressLint("SetTextI18n")
    public ActionCard(Context context, FunctionContext functionContext, A action) {
        super(context);
        this.functionContext = functionContext;
        this.action = action;

        setCardBackgroundColor(DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorSurfaceVariant, 0));
        setStrokeColor(DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorPrimary, 0));
        setStrokeWidth(0);
        setPivotX(0);
        setPivotY(0);
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params == null)
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);

        binding = CardBaseBinding.inflate(LayoutInflater.from(context), this, true);

        binding.title.setText(action.getTitle());
        binding.icon.setImageResource(action.getType().getConfig().getIcon());
        binding.des.setText(action.getDescription());
        binding.des.setVisibility((action.getDescription() == null || action.getDescription().isEmpty()) ? GONE : VISIBLE);
        binding.expandButton.setIconResource(action.isExpand() ? R.drawable.icon_zoom_in : R.drawable.icon_zoom_out);
        binding.functionButton.setVisibility(action instanceof FunctionReferenceAction ? VISIBLE : GONE);

        binding.editButton.setOnClickListener(v -> AppUtils.showEditDialog(context, R.string.action_subtitle_add_des, action.getDescription(), result -> {
            action.setDescription((String) result);
            binding.des.setText(result);
            binding.des.setVisibility((result == null || result.length() == 0) ? GONE : VISIBLE);
        }));

        binding.functionButton.setOnClickListener(v -> {
            if (action instanceof FunctionReferenceAction referenceAction) {
                Function function = SaveRepository.getInstance().getFunction(referenceAction.getParentId(), referenceAction.getFunctionId());
                BlueprintView.tryPushActionContext(function);
            }
        });

        binding.expandButton.setOnClickListener(v -> {
            action.setExpand(!action.isExpand());
            binding.expandButton.setIconResource(action.isExpand() ? R.drawable.icon_zoom_in : R.drawable.icon_zoom_out);
            pinViews.forEach((id, pinView) -> pinView.setExpand(action.isExpand()));
        });

        binding.copyButton.setOnClickListener(v -> {
            Action copy = (Action) action.copy();
            copy.newInfo();
            ((CardLayoutView) getParent()).addAction(copy);
        });

        binding.removeButton.setOnClickListener(v -> {
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
        });

        action.getPins().forEach(this::addPinView);
        action.addListener(this);
    }

    public boolean check() {
        ActionCheckResult result = action.check(functionContext);
        switch (result.type) {
            case NORMAL -> {
                binding.functionButton.setVisibility(action instanceof FunctionReferenceAction ? VISIBLE : GONE);
                binding.errorText.setVisibility(GONE);
            }
            case WARNING -> {
                binding.functionButton.setVisibility(action instanceof FunctionReferenceAction ? VISIBLE : GONE);
                binding.errorText.setVisibility(VISIBLE);
                binding.errorText.setBackgroundColor(DisplayUtils.getAttrColor(getContext(), com.google.android.material.R.attr.colorErrorContainer, 0));
                binding.errorText.setTextColor(DisplayUtils.getAttrColor(getContext(), com.google.android.material.R.attr.colorOnErrorContainer, 0));
                binding.errorText.setText(result.tips);
            }
            case ERROR -> {
                binding.functionButton.setVisibility(GONE);
                binding.errorText.setVisibility(VISIBLE);
                binding.errorText.setBackgroundColor(DisplayUtils.getAttrColor(getContext(), com.google.android.material.R.attr.colorError, 0));
                binding.errorText.setTextColor(DisplayUtils.getAttrColor(getContext(), com.google.android.material.R.attr.colorOnError, 0));
                binding.errorText.setText(result.tips);
            }
        }
        return result.type != ActionCheckResult.ActionResultType.ERROR;
    }

    @SuppressLint("SetTextI18n")
    public void setPosition(float x, float y) {
        setX(x);
        setY(y);
        binding.position.setText(action.getX() + "," + action.getY());
    }

    public void flick() {
        AlphaAnimation animation = new AlphaAnimation(1, .5f);
        animation.setDuration(200);
        animation.setRepeatCount(3);
        animation.setRepeatMode(Animation.REVERSE);
        startAnimation(animation);
    }

    public void setSelected(boolean selected) {
        if (selected) {
            setStrokeWidth((int) DisplayUtils.dp2px(getContext(), 1));
        } else {
            setStrokeWidth(0);
        }
    }

    public void showDebugValue(HashMap<String, PinValue> values) {
        pinViews.forEach((id, pinView) -> {
            PinValue value = values.get(id);
            pinView.setDebugValue(value);
        });
    }

    protected void addPinView(Pin pin) {
        addPinView(pin, 0);
    }

    protected void addPinView(Pin pin, int offset) {
        PinView pinView;
        if (pin.isOut()) {
            if (pin.isVertical()) {
                pinView = new PinBottomView(getContext(), this, pin);
                binding.bottomBox.addView(pinView, binding.bottomBox.getChildCount() - offset);
            } else {
                pinView = new PinRightView(getContext(), this, pin);
                binding.outBox.addView(pinView, binding.outBox.getChildCount() - offset);
            }
        } else {
            if (pin.isVertical()) {
                pinView = new PinTopView(getContext(), this, pin);
                binding.topBox.addView(pinView, binding.topBox.getChildCount() - offset);
            } else {
                pinView = new PinLeftView(getContext(), this, pin);
                binding.inBox.addView(pinView, binding.inBox.getChildCount() - offset);
            }
        }
        pinView.setExpand(action.isExpand());
        pinViews.put(pin.getId(), pinView);
    }

    public void addPin(Pin pin, int offset) {
        action.addPin(pin, action.getPins().size() - offset);
    }

    public void removePin(Pin pin) {
        action.removePin(pin, functionContext);
    }

    @Override
    public void onPinAdded(Pin pin) {
        ArrayList<Pin> pins = new ArrayList<>();
        for (Pin actionPin : action.getPins()) {
            if (actionPin.isOut() == pin.isOut()) {
                boolean allExecute = actionPin.isSameValueType(PinExecute.class) && pin.isSameValueType(PinExecute.class);
                boolean notExecute = (!actionPin.isSameValueType(PinExecute.class)) && (!pin.isSameValueType(PinExecute.class));
                if (allExecute || notExecute) pins.add(actionPin);
            }
        }
        int index = pins.indexOf(pin);
        int offset = pins.size() - 1 - index;
        addPinView(pin, offset);
    }

    @Override
    public void onPinRemoved(Pin pin) {
        PinView pinView = pinViews.remove(pin.getId());
        if (pinView != null) ((ViewGroup) pinView.getParent()).removeView(pinView);
    }

    @Override
    public void onPinChanged(Pin pin) {
        PinView pinView = pinViews.get(pin.getId());
        if (pinView != null) pinView.refreshPinUI();
        pinViews.forEach((id, view) -> view.setExpand(action.isExpand()));
        check();
    }

    public PinView getPinViewById(String id) {
        return pinViews.get(id);
    }

    public PinView getPinViewByPos(float x, float y) {
        float scale = getScaleX();
        for (Map.Entry<String, PinView> entry : pinViews.entrySet()) {
            PinView pinView = entry.getValue();
            // 跳过隐藏的针脚
            if (pinView.getVisibility() != VISIBLE) continue;
            // 跳过添加针脚
            if (pinView.getPin().isSameValueType(PinAdd.class)) continue;

            boolean vertical = pinView.getPin().isVertical();
            boolean out = pinView.getPin().isOut();

            PointF pos = DisplayUtils.getLocationInParentView(binding.getRoot(), pinView);
            float px = pos.x * scale;
            float py = pos.y * scale;
            float width = pinView.getWidth() * scale;
            float height = pinView.getHeight() * scale;
            if (!vertical) {
                float offset = DisplayUtils.dp2px(getContext(), 32 * scale);
                if (out) px = px + width - offset;
                width = offset;
            }
            if (new RectF(px, py, px + width, py + height).contains(x, y)) {
                return pinView;
            }
        }
        return null;
    }

    public boolean touchedEmpty(float x, float y) {
        float scale = getScaleX();
        for (Map.Entry<String, PinView> entry : pinViews.entrySet()) {
            PinView pinView = entry.getValue();
            PointF pos = DisplayUtils.getLocationInParentView(binding.getRoot(), pinView);
            float px = pos.x * scale;
            float py = pos.y * scale;
            float width = pinView.getWidth() * scale;
            float height = pinView.getHeight() * scale;
            if (new RectF(px, py, px + width, py + height).contains(x, y)) return false;
        }

        ArrayList<MaterialButton> buttons = new ArrayList<>(Arrays.asList(binding.editButton, binding.copyButton, binding.expandButton, binding.removeButton));
        if (binding.functionButton.getVisibility() == VISIBLE) buttons.add(binding.functionButton);
        for (MaterialButton button : buttons) {
            PointF pos = DisplayUtils.getLocationInParentView(binding.getRoot(), button);
            float px = pos.x * scale;
            float py = pos.y * scale;
            float width = button.getWidth() * scale;
            float height = button.getHeight() * scale;
            if (new RectF(px, py, px + width, py + height).contains(x, y)) return false;
        }
        return true;
    }

    public void refreshPinView() {
        pinViews.forEach((id, view) -> view.refreshPinView());
    }

    public FunctionContext getFunctionContext() {
        return functionContext;
    }

    public A getAction() {
        return action;
    }

    @Override
    protected void onDetachedFromWindow() {
        action.removeListener(this);
        super.onDetachedFromWindow();
    }
}
