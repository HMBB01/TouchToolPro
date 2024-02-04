package top.bogey.touch_tool_pro.ui.blueprint;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionMap;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.array.ArrayAction;
import top.bogey.touch_tool_pro.bean.action.function.FunctionInnerAction;
import top.bogey.touch_tool_pro.bean.action.function.FunctionReferenceAction;
import top.bogey.touch_tool_pro.bean.action.var.GetVariableValue;
import top.bogey.touch_tool_pro.bean.action.var.SetVariableValue;
import top.bogey.touch_tool_pro.bean.function.Function;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArray;
import top.bogey.touch_tool_pro.bean.task.Task;
import top.bogey.touch_tool_pro.databinding.CardMultiSelectMenuBinding;
import top.bogey.touch_tool_pro.save.FunctionSaveChangedListener;
import top.bogey.touch_tool_pro.save.SaveRepository;
import top.bogey.touch_tool_pro.save.TaskSaveChangedListener;
import top.bogey.touch_tool_pro.save.VariableSaveChangedListener;
import top.bogey.touch_tool_pro.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool_pro.ui.blueprint.card.FunctionCard;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinView;
import top.bogey.touch_tool_pro.utils.DisplayUtils;

public class CardLayoutView extends FrameLayout implements TaskSaveChangedListener, FunctionSaveChangedListener, VariableSaveChangedListener {
    private static final int TOUCH_NONE = 0;
    private static final int TOUCH_BACKGROUND = 1;
    private static final int TOUCH_CARD = 2;
    private static final int TOUCH_PIN = 3;

    private static final int TOUCH_SCALE = 4;
    private static final int TOUCH_SELECT_AREA = 5;

    private static final int TOUCH_DRAG_BACKGROUND = 6;
    private static final int TOUCH_DRAG_CARD = 7;
    private static final int TOUCH_DRAG_PIN = 8;

    private static final long LONG_TOUCH_TIME = 500L;


    private final CardMultiSelectMenuBinding binding;
    private final Handler longTouchHandler;

    private final float gridSize;
    private final Paint gridPaint;
    private final Paint linePaint;
    private final int[] location = new int[2];
    private final RectF show = new RectF();

    private final HashMap<ActionType, Action> cacheActions = new HashMap<>();
    private final ScaleGestureDetector detector;

    private FunctionContext functionContext;
    private final LinkedHashMap<String, ActionCard<?>> cardMap = new LinkedHashMap<>();

    private int touchState = TOUCH_NONE;
    private ActionCard<?> touchedCard = null;
    private PinView touchedPinView = null;
    private final HashMap<String, String> dragLinks = new HashMap<>();
    private boolean dragOut;// 拖动的针脚是不是输出针脚
    private boolean toLink;

    private RectF selectedArea;
    private final HashSet<ActionCard<?>> selectedCards = new HashSet<>();

    private float dragX = 0;
    private float dragY = 0;
    private float startX = 0;
    private float startY = 0;

    private float offsetX = 0;
    private float offsetY = 0;
    private float scale = 1f;

    private boolean editMode = true;

    private AlertDialog dialog;

    private boolean includeBackground = true;
    private boolean needDelete = false;
    private boolean touchMoved = false;


    public CardLayoutView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setSaveEnabled(false);
        setSaveFromParentEnabled(false);

        binding = CardMultiSelectMenuBinding.inflate(LayoutInflater.from(context), this, true);
        binding.getRoot().setVisibility(GONE);
        binding.getRoot().setPivotX(0);
        binding.getRoot().setPivotY(0);
        binding.deleteButton.setOnClickListener(v -> {
            if (needDelete) {
                for (ActionCard<?> card : selectedCards) {
                    removeAction(card.getAction());
                }
                selectedCards.clear();
            } else {
                binding.deleteButton.setChecked(true);
                needDelete = true;
                postDelayed(() -> {
                    binding.deleteButton.setChecked(false);
                    needDelete = false;
                }, 1500);
            }
        });

        binding.copyButton.setOnClickListener(v -> {
            for (ActionCard<?> card : selectedCards) {
                Action copy = (Action) card.getAction().copy();
                copy.newInfo();
                addAction(copy);
            }
        });

        binding.expandButton.setOnClickListener(v -> {
            for (ActionCard<?> card : selectedCards) {
                Action action = card.getAction();
                action.setExpand(!action.isExpand());
                for (Pin pin : action.getPins()) {
                    PinView pinView = card.getPinViewById(pin.getId());
                    if (pinView != null) pinView.setExpand(action.isExpand());
                }
            }
        });

        binding.changeButton.setOnClickListener(v -> {

        });

        longTouchHandler = new Handler();

        gridSize = DisplayUtils.dp2px(context, 8);

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setStrokeWidth(1);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setColor(DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorPrimary, 0));
        gridPaint.setAlpha(40);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStrokeWidth(5);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        linePaint.setStyle(Paint.Style.STROKE);

        detector = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScaleBegin(@NonNull ScaleGestureDetector detector) {
                touchState = TOUCH_SCALE;
                return true;
            }

            @Override
            public void onScaleEnd(@NonNull ScaleGestureDetector detector) {
                touchState = TOUCH_NONE;
            }

            @Override
            public boolean onScale(@NonNull ScaleGestureDetector detector) {
                float oldScale = scale;
                scale *= detector.getScaleFactor();
                scale = Math.max(0.3f, Math.min(scale, 2f));

                // 设置居中缩放偏移
                float v = 1 - scale / oldScale;
                float focusX = detector.getFocusX() - offsetX;
                float focusY = detector.getFocusY() - offsetY;
                offsetX += focusX * v;
                offsetY += focusY * v;

                setCardsPosition();
                binding.getRoot().setScaleX(scale);
                binding.getRoot().setScaleY(scale);
                postInvalidate();
                return true;
            }
        });

        for (ActionMap actionMap : ActionMap.values()) {
            if (actionMap == ActionMap.START) continue;
            for (ActionType actionType : actionMap.getTypes()) {
                Class<? extends Action> actionClass = actionType.getConfig().getActionClass();
                try {
                    Constructor<? extends Action> constructor = actionClass.getConstructor();
                    Action action = constructor.newInstance();
                    cacheActions.put(actionType, action);
                } catch (Exception ignored) {
                }
            }
        }

        SaveRepository.getInstance().addTaskListener(this);
        SaveRepository.getInstance().addFunctionListener(this);
        SaveRepository.getInstance().addVariableListener(this);
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public ActionCard<?> newCard(FunctionContext functionContext, Action action) {
        if (functionContext instanceof Function function && action instanceof FunctionInnerAction innerAction) {
            return new FunctionCard(getContext(), function, innerAction);
        }
        return new ActionCard<>(getContext(), functionContext, action);
    }

    public void addAction(Action action) {
        functionContext.addAction(action);
        ActionCard<?> card = newCard(functionContext, action);
        setCardPosition(card);
        cardMap.put(action.getId(), card);
        addView(card);
    }

    public void addAction(Class<? extends Action> actionClass) {
        try {
            Constructor<? extends Action> constructor = actionClass.getConstructor();
            Action action = constructor.newInstance();
            action.setX((int) ((dragX - offsetX) / getScaleGridSize()) + 1);
            action.setY((int) ((dragY - offsetY) / getScaleGridSize()) + 1);
            tryLinkDragPin(action);
            addAction(action);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException |
                 InstantiationException e) {
            e.printStackTrace();
        }
    }

    public void addAction(Class<? extends Action> actionClass, String key, PinValue value) {
        try {
            Constructor<? extends Action> constructor = actionClass.getConstructor(String.class, PinValue.class);
            Action action = constructor.newInstance(key, value);
            action.setX((int) ((dragX - offsetX) / getScaleGridSize()) + 1);
            action.setY((int) ((dragY - offsetY) / getScaleGridSize()) + 1);
            tryLinkDragPin(action);
            addAction(action);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException |
                 InstantiationException e) {
            e.printStackTrace();
        }
    }

    public void addAction(String functionId) {
        Function function = SaveRepository.getInstance().getFunctionById(functionId);
        addAction(function);
    }

    public void addAction(Function function) {
        if (function != null) {
            FunctionReferenceAction referenceAction = new FunctionReferenceAction(function);
            referenceAction.sync(functionContext);
            referenceAction.setX((int) ((dragX - offsetX) / getScaleGridSize()) + 1);
            referenceAction.setY((int) ((dragY - offsetY) / getScaleGridSize()) + 1);
            tryLinkDragPin(referenceAction);
            addAction(referenceAction);
        }
    }

    public void removeAction(Action action) {
        functionContext.removeAction(action);
        ActionCard<?> card = cardMap.remove(action.getId());
        if (card == null) return;
        for (Pin pin : action.getPins()) {
            pin.cleanLinks(functionContext);
        }

        removeView(card);
    }

    private void setCardsPosition() {
        for (ActionCard<?> baseCard : cardMap.values()) {
            setCardPosition(baseCard);
        }
    }

    private void setCardPosition(ActionCard<?> card) {
        Action action = card.getAction();
        card.setScaleX(scale);
        card.setScaleY(scale);
        float x = action.getX() * getScaleGridSize() + offsetX;
        float y = action.getY() * getScaleGridSize() + offsetY;
        card.setPosition(x, y);
        float width = card.getWidth() * scale;
        float height = card.getHeight() * scale;
        RectF cardArea = new RectF(x, y, x + width, y + height);
        if (RectF.intersects(show, cardArea)) {
            card.setVisibility(VISIBLE);
        } else {
            card.setVisibility(INVISIBLE);
        }
    }

    public void showCard(int x, int y, Class<? extends Action> actionClass) {
        ActionCard<?> card = null;
        for (Map.Entry<String, ActionCard<?>> entry : cardMap.entrySet()) {
            ActionCard<?> actionCard = entry.getValue();
            Action action = actionCard.getAction();
            if (action.getX() == x && action.getY() == y && actionClass.isInstance(action)) {
                card = actionCard;
                break;
            }
        }
        if (card == null) return;

        float targetX = -x * getScaleGridSize() + (getWidth() - card.getWidth() * scale) / 2f;
        float targetY = -y * getScaleGridSize() + (getHeight() - card.getHeight() * scale) / 2f;
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(animation -> {
            float percent = (float) animation.getAnimatedValue();
            offsetX += (targetX - offsetX) * percent;
            offsetY += (targetY - offsetY) * percent;
            setCardsPosition();
        });

        ActionCard<?> finalCard = card;
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                finalCard.flick();
            }
        });
        animator.start();
    }

    private void showSelectActionDialog() {
        if (touchedPinView == null) return;
        SelectActionDialog actionDialog = new SelectActionDialog(getContext(), this, touchedPinView.getPin().getPinClass(), dragOut);
        if (actionDialog.isEmpty()) return;
        dialog = new MaterialAlertDialogBuilder(getContext())
                .setView(actionDialog)
                .setOnDismissListener(dialog -> {
                    this.dialog = null;
                    dragLinks.clear();
                    touchedCard = null;
                    touchedPinView = null;
                    dragX = location[0];
                    dragY = location[1];
                    touchState = TOUCH_NONE;
                    postInvalidate();
                })
                .show();
    }

    public void dismissDialog() {
        if (dialog != null) dialog.dismiss();
    }

    public void tryLinkDragPin(Action action) {
        if (touchedPinView != null) {
            Pin pin = action.getFirstPinByClass(touchedPinView.getPin().getPinClass(), dragOut);
            if (pin != null) {
                if (action instanceof ArrayAction arrayAction && touchedPinView.getPin().getValue() instanceof PinValueArray array) {
                    arrayAction.setValueType(functionContext, array.getPinType());
                }
                pin.addLinks(dragLinks, functionContext);
            }
        }
    }

    private float getScaleGridSize() {
        return gridSize * scale;
    }

    public Bitmap captureFunctionContext() {
        cleanSelectedCards();
        float tmpScale = scale;
        scale = 1;
        setCardsPosition();
        cardMap.forEach((id, card) -> card.setVisibility(VISIBLE));

        float scaleGridSize = getScaleGridSize();
        Rect area = CardLayoutUtils.getCardsArea(new HashSet<>(cardMap.values()), 1);
        area.left -= scaleGridSize;
        area.top -= scaleGridSize;
        area.right += scaleGridSize;
        area.bottom += scaleGridSize;
        Bitmap bitmap = Bitmap.createBitmap(area.width(), area.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        canvas.drawColor(DisplayUtils.getAttrColor(getContext(), com.google.android.material.R.attr.colorSurface, 0));
        drawBackground(canvas, offsetX - area.left, offsetY - area.top);
        canvas.translate(-area.left, -area.top);
        includeBackground = false;
        dispatchDraw(canvas);
        includeBackground = true;

        scale = tmpScale;
        setCardsPosition();
        return bitmap;
    }

    private void drawBackground(Canvas canvas, float cx, float cy) {
        canvas.save();
        float gridScaleSize = getScaleGridSize();
        float ofX = cx % gridScaleSize;
        float ofY = cy % gridScaleSize;
        canvas.translate(ofX, ofY);

        // 格子背景
        float gridRow = canvas.getHeight() / gridScaleSize; //有多少行
        float gridCol = canvas.getWidth() / gridScaleSize;  //有多少列

        float bigGridSize = 10 * gridScaleSize;
        float startY = cy - ofY;
        for (int i = 0; i < gridRow; i++) {
            float y = i * gridScaleSize;
            if (startY == y) {
                gridPaint.setStrokeWidth(6);
            } else {
                float v = (startY - y) % bigGridSize;
                gridPaint.setStrokeWidth((Math.abs(v) < 1 || Math.abs(v) > bigGridSize - 1) ? 2 : 0.5f);
            }
            canvas.drawLine(-gridScaleSize, y, canvas.getWidth() + gridScaleSize, y, gridPaint);
        }

        float startX = cx - ofX;
        for (int i = 0; i < gridCol; i++) {
            float x = i * gridScaleSize;
            if (cx == x + ofX) {
                gridPaint.setStrokeWidth(4);
            } else {
                float v = (startX - x) % bigGridSize;
                gridPaint.setStrokeWidth((Math.abs(v) < 1 || Math.abs(v) > bigGridSize - 1) ? 2 : 0.5f);
            }
            canvas.drawLine(x, -gridScaleSize, x, canvas.getHeight() + gridScaleSize, gridPaint);
        }
        canvas.restore();
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        if (includeBackground) drawBackground(canvas, offsetX, offsetY);
        float scaleGridSize = getScaleGridSize();
        CornerPathEffect cornerPathEffect = new CornerPathEffect(scaleGridSize);
        linePaint.setPathEffect(cornerPathEffect);

        // 所有连接的线
        linePaint.setStrokeWidth(4 * scale);
        for (ActionCard<?> card : cardMap.values()) {
            if (selectedCards.contains(card)) continue;
            Action action = card.getAction();
            for (Pin pin : action.getPins()) {
                for (Map.Entry<String, String> entry : pin.getLinks().entrySet()) {
                    ActionCard<?> baseCard = cardMap.get(entry.getValue());
                    if (baseCard == null) continue;
                    if (selectedCards.contains(baseCard)) continue;
                    PinView pinBaseView = baseCard.getPinViewById(entry.getKey());
                    if (pinBaseView == null) continue;
                    // 只画输出的线
                    if (pinBaseView.getPin().isOut()) {
                        linePaint.setColor(pinBaseView.getPin().getValue().getPinColor(getContext()));
                        canvas.drawPath(calculateLinePath(pinBaseView, card.getPinViewById(pin.getId())), linePaint);
                    }
                }
            }
        }

        // 选中的卡的连线需要置顶，且变色
        linePaint.setStrokeWidth(8 * scale);
        linePaint.setColorFilter(new LightingColorFilter(getResources().getColor(R.color.SelectedPinMul, null), getResources().getColor(R.color.SelectedPinAdd, null)));
        for (ActionCard<?> card : selectedCards) {
            Action action = card.getAction();
            for (Pin pin : action.getPins()) {
                for (Map.Entry<String, String> entry : pin.getLinks().entrySet()) {
                    ActionCard<?> baseCard = cardMap.get(entry.getValue());
                    if (baseCard == null) continue;
                    PinView pinBaseView = baseCard.getPinViewById(entry.getKey());
                    if (pinBaseView == null) continue;
                    linePaint.setColor(pinBaseView.getPin().getValue().getPinColor(getContext()));
                    if (pinBaseView.getPin().isOut()) {
                        canvas.drawPath(calculateLinePath(pinBaseView, card.getPinViewById(pin.getId())), linePaint);
                    } else {
                        canvas.drawPath(calculateLinePath(card.getPinViewById(pin.getId()), pinBaseView), linePaint);
                    }
                }
            }
        }
        linePaint.setColorFilter(null);

        // 拖动的连线
        if (touchState == TOUCH_DRAG_PIN) {
            PinView pinInDragPos = null;
            ActionCard<?> cardInPos = getCardInPos(dragX, dragY);
            if (cardInPos != null) {
                pinInDragPos = cardInPos.getPinViewByPos(dragX - cardInPos.getX(), dragY - cardInPos.getY());
            }

            for (Map.Entry<String, String> entry : dragLinks.entrySet()) {
                ActionCard<?> card = cardMap.get(entry.getValue());
                if (card == null) continue;
                PinView pinView = card.getPinViewById(entry.getKey());
                if (pinView == null) continue;

                boolean next = true;
                if (pinInDragPos != null) {
                    if (toLink && touchedPinView.getPin().isCanLink(pinInDragPos.getPin())) {
                        linePaint.setColor(touchedPinView.getPin().getValue().getPinColor(getContext()));
                        next = false;
                    }

                    if (next && !toLink && touchedPinView.getPin().isSameValueType(pinInDragPos.getPin())) {
                        linePaint.setColor(touchedPinView.getPin().getValue().getPinColor(getContext()));
                        next = false;
                    }
                }
                if (next) {
                    linePaint.setColor(DisplayUtils.getAttrColor(getContext(), com.google.android.material.R.attr.colorPrimaryInverse, 0));
                }
                canvas.drawPath(calculateLinePath(pinView), linePaint);
            }
        }

        // 所有卡片
        super.dispatchDraw(canvas);

        // 选框
        if (touchState == TOUCH_SELECT_AREA) {
            DashPathEffect dashPathEffect = new DashPathEffect(new float[]{scaleGridSize, scaleGridSize}, 0);
            linePaint.setPathEffect(new ComposePathEffect(cornerPathEffect, dashPathEffect));
            linePaint.setStrokeWidth(4 * scale);
            linePaint.setColor(DisplayUtils.getAttrColor(getContext(), com.google.android.material.R.attr.colorPrimary, 0));
            RectF rect = new RectF(selectedArea);
            canvas.drawRect(rect, linePaint);
        } else {
            if (selectedCards.size() > 1) {
                DashPathEffect dashPathEffect = new DashPathEffect(new float[]{scaleGridSize, scaleGridSize}, 0);
                linePaint.setPathEffect(new ComposePathEffect(cornerPathEffect, dashPathEffect));
                linePaint.setStrokeWidth(4 * scale);
                linePaint.setColor(DisplayUtils.getAttrColor(getContext(), com.google.android.material.R.attr.colorPrimary, 0));
                Rect rect = CardLayoutUtils.getCardsArea(selectedCards, scale);
                rect.left -= scaleGridSize;
                rect.top -= scaleGridSize;
                rect.right += scaleGridSize;
                rect.bottom += scaleGridSize;
                canvas.drawRect(rect, linePaint);

                binding.getRoot().setX(rect.right + scaleGridSize / 2);
                binding.getRoot().setY(rect.top);
            }
        }
    }

    private Path calculateLinePath(PointF outLocation, PointF inLocation, boolean v) {
        Path path = new Path();
        if (outLocation == null || inLocation == null) return path;
        path = CardLayoutUtils.calculateLinePath(outLocation, inLocation, v, getScaleGridSize());
        return path;
    }

    private Path calculateLinePath(PinView outPin, PinView inPin) {
        Path path = new Path();
        if (outPin == null || inPin == null) return path;

        PointF outLocation = outPin.getSlotLocationInCard();
        outLocation.offset(outPin.getCard().getX(), outPin.getCard().getY());
        PointF inLocation = inPin.getSlotLocationInCard();
        inLocation.offset(inPin.getCard().getX(), inPin.getCard().getY());
        return calculateLinePath(outLocation, inLocation, outPin.getPin().isVertical());
    }

    private Path calculateLinePath(PinView pinView) {
        if (pinView == null) return new Path();
        PointF pinLocation = pinView.getSlotLocationInCard();
        pinLocation.offset(pinView.getCard().getX(), pinView.getCard().getY());

        PointF outLocation, inLocation;
        if (dragOut) {
            inLocation = pinLocation;
            outLocation = new PointF(dragX, dragY);
        } else {
            inLocation = new PointF(dragX, dragY);
            outLocation = pinLocation;
        }
        return calculateLinePath(outLocation, inLocation, pinView.getPin().isVertical());
    }

    private ActionCard<?> getCardInPos(float x, float y) {
        ArrayList<ActionCard<?>> cards = new ArrayList<>(cardMap.values());
        cards.sort((o1, o2) -> indexOfChild(o1) - indexOfChild(o2));
        for (ActionCard<?> card : cards) {
            float cardX = card.getX();
            float cardY = card.getY();
            float cardWidth = card.getWidth() * scale;
            float cardHeight = card.getHeight() * scale;
            RectF rectF = new RectF(cardX, cardY, cardX + cardWidth, cardY + cardHeight);
            if (rectF.contains(x, y)) {
                return card;
            }
        }
        return null;
    }

    private void cleanSelectedCards() {
        HashSet<ActionCard<?>> cards = new HashSet<>(selectedCards);
        cards.forEach(this::removeSelectedCard);
    }

    private void removeSelectedCard(ActionCard<?> card) {
        if (selectedCards.remove(card)) {
            card.setSelected(false);
        }
    }

    private void addSelectedCard(ActionCard<?> card) {
        card.setSelected(true);
        selectedCards.add(card);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        if (touchState == TOUCH_SCALE) {
            longTouchHandler.removeCallbacksAndMessages(null);
            return true;
        }

        float x = event.getX();
        float y = event.getY();

        int actionMasked = event.getActionMasked();
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN -> {
                startX = x;
                startY = y;
                touchState = TOUCH_BACKGROUND;
                touchMoved = false;
                if (editMode) {
                    ActionCard<?> card = getCardInPos(x, y);
                    if (card != null) {
                        touchState = TOUCH_CARD;
                        touchedCard = card;
                        touchedCard.bringToFront();

                        PinView pinView = card.getPinViewByPos(x - card.getX(), y - card.getY());
                        if (pinView != null) {
                            touchState = TOUCH_PIN;
                            touchedPinView = pinView;
                        }
                    }
                }

                switch (touchState) {
                    case TOUCH_BACKGROUND ->
                            longTouchHandler.postDelayed(() -> touchState = TOUCH_SELECT_AREA, LONG_TOUCH_TIME);

                    case TOUCH_PIN -> longTouchHandler.postDelayed(() -> {
                        Pin pin = touchedPinView.getPin();
                        Pin linkedPin = pin.getLinkedPin(functionContext);
                        if (linkedPin == null) return;
                        Action action = functionContext.getActionById(linkedPin.getActionId());
                        showCard(action.getX(), action.getY(), action.getClass());

                        touchState = TOUCH_NONE;
                    }, LONG_TOUCH_TIME);
                }

                dragX = x;
                dragY = y;
            }

            case MotionEvent.ACTION_MOVE -> {
                if (Math.abs(x - startX) * Math.abs(y - startY) > 81) touchMoved = true;

                if (touchMoved) {
                    longTouchHandler.removeCallbacksAndMessages(null);
                    switch (touchState) {
                        case TOUCH_BACKGROUND -> touchState = TOUCH_DRAG_BACKGROUND;
                        case TOUCH_CARD -> {
                            touchState = TOUCH_DRAG_CARD;
                            if (!selectedCards.contains(touchedCard)) {
                                cleanSelectedCards();
                                addSelectedCard(touchedCard);
                            }
                        }
                        case TOUCH_PIN -> {
                            touchState = TOUCH_DRAG_PIN;
                            dragLinks.clear();
                            Pin pin = touchedPinView.getPin();
                            HashMap<String, String> links = pin.getLinks();
                            // 数量为0 或者 是输出针脚且可以多输出，从这个点出线。进线要么连接，要么断开，没有只断开一个连接的方式
                            if (links.isEmpty() || (!pin.isSingleLink() && pin.isOut())) {
                                dragLinks.put(pin.getId(), pin.getActionId());
                                dragOut = !pin.isOut();
                                toLink = true;
                            } else {
                                // 否则就是挪线
                                dragLinks.putAll(links);
                                dragOut = pin.isOut();
                                toLink = false;
                                pin.cleanLinks(functionContext);
                            }
                        }
                    }
                }

                switch (touchState) {
                    case TOUCH_DRAG_BACKGROUND -> {
                        offsetX += (x - dragX);
                        offsetY += (y - dragY);
                        dragX = x;
                        dragY = y;
                        setCardsPosition();
                    }

                    case TOUCH_SELECT_AREA -> {
                        cleanSelectedCards();
                        selectedArea = new RectF(startX, startY, x, y);
                        selectedArea.sort();
                        cardMap.forEach((id, card) -> {
                            float cardX = card.getX();
                            float cardY = card.getY();
                            float cardWidth = card.getWidth() * scale;
                            float cardHeight = card.getHeight() * scale;
                            RectF rectF = new RectF(cardX, cardY, cardX + cardWidth, cardY + cardHeight);
                            if (RectF.intersects(selectedArea, rectF)) {
                                addSelectedCard(card);
                            }
                        });
                    }

                    case TOUCH_DRAG_CARD -> {
                        float scaleGridSize = getScaleGridSize();
                        int dx = (int) ((x - dragX) / scaleGridSize);
                        int dy = (int) ((y - dragY) / scaleGridSize);

                        for (ActionCard<?> card : selectedCards) {
                            Action action = card.getAction();
                            if (dx != 0) action.setX(action.getX() + dx);
                            if (dy != 0) action.setY(action.getY() + dy);
                            setCardPosition(card);
                        }
                        dragX += dx * scaleGridSize;
                        dragY += dy * scaleGridSize;
                    }

                    case TOUCH_DRAG_PIN -> {
                        float offset = gridSize * 4;
                        float scaleGridSize = getScaleGridSize();
                        if (x < offset) {
                            offsetX += scaleGridSize;
                        } else if (x > getWidth() - offset) {
                            offsetX -= scaleGridSize;
                        }
                        if (y < offset) {
                            offsetY += scaleGridSize;
                        } else if (y > getHeight() - offset) {
                            offsetY -= scaleGridSize;
                        }
                        dragX = x;
                        dragY = y;
                        setCardsPosition();
                    }
                }
            }

            case MotionEvent.ACTION_UP -> {
                longTouchHandler.removeCallbacksAndMessages(null);
                switch (touchState) {
                    case TOUCH_BACKGROUND -> {
                        cleanSelectedCards();
                        binding.getRoot().setVisibility(selectedCards.size() > 1 ? VISIBLE : GONE);
                    }

                    case TOUCH_CARD -> {
                        if (selectedCards.contains(touchedCard)) cleanSelectedCards();
                        addSelectedCard(touchedCard);
                        binding.getRoot().setVisibility(GONE);
                    }

                    case TOUCH_PIN -> {
                        Pin pin = touchedPinView.getPin();
                        pin.cleanLinks(functionContext);
                    }

                    case TOUCH_SELECT_AREA -> binding.getRoot().setVisibility(selectedCards.size() > 1 ? VISIBLE : GONE);

                    case TOUCH_DRAG_PIN -> {
                        ActionCard<?> card = getCardInPos(x, y);
                        if (card != null) {
                            boolean next = true;
                            // 看是否放到卡片针脚上了
                            PinView pinView = card.getPinViewByPos(x - card.getX(), y - card.getY());
                            if (pinView != null) {
                                Pin pin = pinView.getPin();
                                if (pin.addLinks(dragLinks, functionContext)) {
                                    next = false;
                                }
                            }

                            // 没放到针脚上，尝试直接连接卡片对应针脚
                            if (next) tryLinkDragPin(card.getAction());

                        } else {
                            // 放到空白处了
                            showSelectActionDialog();
                        }
                    }
                }
                touchState = TOUCH_NONE;
            }
        }
        postInvalidate();
        return true;
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        refreshElevation();
    }

    @Override
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
        refreshElevation();
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        refreshElevation();
    }

    private void refreshElevation() {
        if (cardMap == null) return;
        HashSet<ActionCard<?>> checkedCards = new HashSet<>();
        cardMap.forEach((id, card) -> {
            if (card.getVisibility() != VISIBLE) return;
            if (checkedCards.contains(card)) return;

            // 搜索重叠的卡片
            HashSet<ActionCard<?>> cards = new HashSet<>();
            cards.add(card);
            searchOverrideCards(checkedCards, cards, card);

            checkedCards.addAll(cards);
            ArrayList<ActionCard<?>> cardList = new ArrayList<>(cards);
            cardList.sort((o1, o2) -> indexOfChild(o1) - indexOfChild(o2));
            for (int i = 0; i < cardList.size(); i++) {
                cardList.get(i).setElevation(10 + i);
            }
        });
    }

    private void searchOverrideCards(HashSet<ActionCard<?>> checkedCards, HashSet<ActionCard<?>> cards, ActionCard<?> checkCard) {
        RectF checkCardRect = new RectF(checkCard.getX(), checkCard.getY(), checkCard.getX() + checkCard.getWidth() * scale, checkCard.getY() + checkCard.getHeight() * scale);
        for (Map.Entry<String, ActionCard<?>> entry : cardMap.entrySet()) {
            ActionCard<?> card = entry.getValue();
            if (card.getVisibility() != VISIBLE) continue;
            if (cards.contains(card)) continue;
            if (checkedCards.contains(card)) continue;
            RectF cardRect = new RectF(card.getX(), card.getY(), card.getX() + card.getWidth() * scale, card.getY() + card.getHeight() * scale);
            if (RectF.intersects(cardRect, checkCardRect)) {
                cards.add(card);
                searchOverrideCards(checkedCards, cards, card);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            getLocationOnScreen(location);
            show.set(0, 0, getWidth(), getHeight());
            dragX = location[0];
            dragY = location[1];
            setCardsPosition();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        SaveRepository.getInstance().removeTaskListener(this);
        SaveRepository.getInstance().removeFunctionListener(this);
        SaveRepository.getInstance().removeVariableListener(this);
        super.onDetachedFromWindow();
    }

    public FunctionContext getFunctionContext() {
        return functionContext;
    }

    public void setFunctionContext(FunctionContext functionContext) {
        this.functionContext = functionContext;
        offsetX = 0;
        offsetY = 0;
        scale = 1f;
        cardMap.forEach((id, card) -> removeView(card));
        cardMap.clear();
        for (Action action : functionContext.getActions()) {
            if (action instanceof FunctionReferenceAction) {
                ((FunctionReferenceAction) action).sync(functionContext);
            }
            ActionCard<?> card = newCard(functionContext, action);
            setCardPosition(card);
            addView(card);
            cardMap.put(action.getId(), card);
        }
        checkCards();
    }

    public HashMap<ActionType, Action> getCacheActions() {
        return cacheActions;
    }

    public LinkedHashMap<String, ActionCard<?>> getCardMap() {
        return cardMap;
    }

    public void checkCards() {
        int count = 0;
        for (Map.Entry<String, ActionCard<?>> entry : cardMap.entrySet()) {
            ActionCard<?> card = entry.getValue();
            if (!card.check()) count++;
        }
        if (count == 0) return;
        Toast.makeText(getContext(), getContext().getString(R.string.card_error_tips, count), Toast.LENGTH_SHORT).show();
    }


    public void refreshVariableAction(String key, PinValue value) {
        cardMap.forEach((id, card) -> {
            if (card.getAction() instanceof GetVariableValue getValue) {
                if (getValue.getVarKey().equals(key)) {
                    getValue.setValue((PinValue) value.copy());
                    refreshVariableActionPins(getValue);
                }
            }
            if (card.getAction() instanceof SetVariableValue setValue) {
                if (setValue.getVarKey().equals(key)) {
                    setValue.setValue((PinValue) value.copy());
                    refreshVariableActionPins(setValue);
                }
            }
        });
        postInvalidate();
    }

    public void refreshVariableActionPins(Action action) {
        ActionCard<?> baseCard = cardMap.get(action.getId());
        if (baseCard == null) return;
        for (Pin pin : action.getPins()) {
            if (pin.getValue() instanceof PinValue) {
                pin.cleanLinks(functionContext);
            }
        }
    }

    @Override
    public void onCreated(Function value) {
    }

    @Override
    public void onChanged(Function value) {
        checkCards();
    }

    @Override
    public void onRemoved(Function value) {
        checkCards();
    }

    @Override
    public void onCreated(Task value) {
    }

    @Override
    public void onChanged(Task value) {
        checkCards();
    }

    @Override
    public void onRemoved(Task value) {
    }

    @Override
    public void onCreated(String key, PinValue value) {
        checkCards();
    }

    @Override
    public void onChanged(String key, PinValue value) {
        refreshVariableAction(key, value);
    }

    @Override
    public void onRemoved(String key, PinValue value) {
        checkCards();
    }
}
