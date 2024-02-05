package top.bogey.touch_tool_pro.ui.blueprint.pin;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PointF;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinListener;
import top.bogey.touch_tool_pro.bean.pin.pins.PinColor;
import top.bogey.touch_tool_pro.bean.pin.pins.PinImage;
import top.bogey.touch_tool_pro.bean.pin.pins.PinObject;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.databinding.PinDebugValueBinding;
import top.bogey.touch_tool_pro.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidget;
import top.bogey.touch_tool_pro.utils.DisplayUtils;

public abstract class PinView extends FrameLayout implements PinListener {
    protected final ActionCard<?> card;
    protected final Action action;
    protected final Pin pin;
    protected final boolean custom;

    public PinView(@NonNull Context context, ActionCard<?> card, Pin pin) {
        this(context, card, pin, false);
    }

    public PinView(@NonNull Context context, ActionCard<?> card, Pin pin, boolean custom) {
        super(context);
        this.card = card;
        action = card.getAction();
        this.pin = pin;
        this.custom = custom;

        pin.addPinListener(this);
    }

    protected void initRemoveButton(MaterialButton button) {
        button.setVisibility(pin.isRemoveAble() ? VISIBLE : GONE);
        button.setOnClickListener(view -> card.removePin(pin));
    }

    public void setExpand(boolean expand) {
        if (pin.isVertical()) return;
        if (pin.getLinks().isEmpty() && !expand) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
        }
    }

    public abstract void refreshPinUI();

    public abstract PointF getSlotLocationInCard();

    public abstract ViewGroup getPinViewBox();

    public abstract PinDebugValueBinding getDebugValueView();

    public void setDebugValue(PinValue value) {
        PinDebugValueBinding debugValueView = getDebugValueView();
        if (debugValueView == null) return;
        debugValueView.text.setVisibility(GONE);
        debugValueView.image.setVisibility(GONE);
        if (value == null) return;
        if (value instanceof PinImage pinImage) {
            debugValueView.image.setVisibility(VISIBLE);
            debugValueView.image.setImageBitmap(pinImage.getImage(getContext()));
            debugValueView.image.setImageTintList(null);
        } else if (value instanceof PinColor pinColor) {
            debugValueView.image.setVisibility(VISIBLE);
            debugValueView.image.setImageBitmap(null);
            debugValueView.image.setImageTintList(ColorStateList.valueOf(DisplayUtils.getColorFromHsv(pinColor.getColor())));
        } else {
            debugValueView.text.setVisibility(VISIBLE);
            debugValueView.text.setText(value.toString());
        }
    }

    public void refreshPinView() {
        ViewGroup viewGroup = getPinViewBox();
        if (viewGroup != null) {
            viewGroup.removeAllViews();
            Context context = getContext();
            Class<? extends PinWidget<? extends PinObject>> widgetClass = pin.getValue().getType().getConfig().getPinWidgetClass();
            if (widgetClass != null) {
                try {
                    Constructor<? extends PinWidget<? extends PinObject>> constructor = widgetClass.getConstructor(Context.class, ActionCard.class, PinView.class, pin.getPinClass(), boolean.class);
                    PinWidget<? extends PinObject> pinWidget = constructor.newInstance(context, card, this, pin.getValue(), custom);
                    viewGroup.addView(pinWidget);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        refreshPinUI();
    }

    @Override
    public void onLinked(Pin linkedPin) {
        post(this::refreshPinUI);
    }

    @Override
    public void onUnlink(Pin unlinkedPin) {
        post(this::refreshPinUI);
    }

    @Override
    public void onValueChanged(PinObject value) {
        post(this::refreshPinView);
    }

    @Override
    public void onTitleChanged(String title) {
        post(this::refreshPinUI);
    }

    protected @ColorInt int getPinColor() {
        return pin.getValue().getPinColor(getContext());
    }

    protected ShapeAppearanceModel getPinStyle() {
        return pin.getValue().getPinStyle(getContext());
    }

    public ActionCard<?> getCard() {
        return card;
    }

    public Action getAction() {
        return action;
    }

    public Pin getPin() {
        return pin;
    }

    @Override
    protected void onDetachedFromWindow() {
        pin.removePinListener(this);
        super.onDetachedFromWindow();
    }
}
