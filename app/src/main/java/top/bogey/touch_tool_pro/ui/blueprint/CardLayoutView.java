package top.bogey.touch_tool_pro.ui.blueprint;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionMap;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.function.FunctionInnerAction;
import top.bogey.touch_tool_pro.bean.action.function.FunctionReferenceAction;
import top.bogey.touch_tool_pro.bean.action.var.GetCommonVariableValue;
import top.bogey.touch_tool_pro.bean.action.var.SetCommonVariableValue;
import top.bogey.touch_tool_pro.bean.base.FunctionSaveChangedListener;
import top.bogey.touch_tool_pro.bean.base.SaveRepository;
import top.bogey.touch_tool_pro.bean.base.TaskSaveChangedListener;
import top.bogey.touch_tool_pro.bean.base.VariableSaveChangedListener;
import top.bogey.touch_tool_pro.bean.function.Function;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.task.Task;
import top.bogey.touch_tool_pro.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool_pro.ui.blueprint.card.FunctionCard;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinView;
import top.bogey.touch_tool_pro.utils.DisplayUtils;

public class CardLayoutView extends FrameLayout implements TaskSaveChangedListener, FunctionSaveChangedListener, VariableSaveChangedListener {
    private static final int DRAG_NONE = 0;
    private static final int DRAG_SELF = 1;
    private static final int DRAG_CARD = 2;
    private static final int DRAG_PIN = 3;
    private static final int DRAG_SCALE = 4;

    private final float gridSize;
    private final Paint gridPaint;
    private final Paint linePaint;
    private final int[] location = new int[2];
    private final RectF show = new RectF();

    private final LinkedHashMap<String, ActionCard<?>> cardMap = new LinkedHashMap<>();
    private FunctionContext functionContext;

    private int dragState = DRAG_NONE;
    private final HashMap<String, String> dragLinks = new HashMap<>();
    private ActionCard<?> dragCard = null;
    private PinView dragPin = null;
    private PinView matchedPin = null;

    private float dragX = 0;
    private float dragY = 0;
    private float startX = 0;
    private float startY = 0;
    private boolean dragOut;

    private boolean isClick = false;
    private boolean isBreakLink = false;
    private AlertDialog dialog;

    private float offsetX = 0;
    private float offsetY = 0;

    private float scale = 1f;
    private final ScaleGestureDetector detector;

    private boolean editMode = true;
    private final HashMap<ActionType, Action> tmpActions = new HashMap<>();

    public CardLayoutView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setSaveEnabled(false);
        setSaveFromParentEnabled(false);

        gridSize = DisplayUtils.dp2px(context, 8);

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setStrokeWidth(1);
        gridPaint.setStrokeCap(Paint.Cap.ROUND);
        gridPaint.setStrokeJoin(Paint.Join.ROUND);
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
                dragState = DRAG_SCALE;
                return true;
            }

            @Override
            public void onScaleEnd(@NonNull ScaleGestureDetector detector) {
                dragState = DRAG_NONE;
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
                postInvalidate();
                return true;
            }
        });

        for (ActionMap actionMap : ActionMap.values()) {
            if (actionMap == ActionMap.START) continue;
            for (ActionType actionType : actionMap.getTypes()) {
                Class<? extends Action> actionClass = actionType.getActionClass();
                try {
                    Constructor<? extends Action> constructor = actionClass.getConstructor();
                    Action action = constructor.newInstance();
                    tmpActions.put(actionType, action);
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

    public void setFunctionContext(FunctionContext functionContext) {
        this.functionContext = functionContext;
        offsetX = 0;
        offsetY = 0;
        scale = 1f;
        cardMap.clear();
        removeAllViews();
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

    public void addAction(Action action) {
        functionContext.addAction(action);
        ActionCard<?> card = newCard(functionContext, action);
        setCardPosition(card);
        addView(card);
        cardMap.put(action.getId(), card);
    }

    public void addAction(Class<? extends Action> actionClass) {
        try {
            Constructor<? extends Action> constructor = actionClass.getConstructor();
            Action action = constructor.newInstance();
            action.setX((int) ((dragX - location[0] - offsetX) / getScaleGridSize()) + 1);
            action.setY((int) ((dragY - location[1] - offsetY) / getScaleGridSize()) + 1);
            tryLinkDragPin(action);
            addAction(action);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    public void addAction(Class<? extends Action> actionClass, String key, PinValue value) {
        try {
            Constructor<? extends Action> constructor = actionClass.getConstructor(String.class, PinValue.class);
            Action action = constructor.newInstance(key, value);
            action.setX((int) ((dragX - location[0] - offsetX) / getScaleGridSize()) + 1);
            action.setY((int) ((dragY - location[1] - offsetY) / getScaleGridSize()) + 1);
            tryLinkDragPin(action);
            addAction(action);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
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
            referenceAction.setX((int) ((dragX - location[0] - offsetX) / getScaleGridSize()) + 1);
            referenceAction.setY((int) ((dragY - location[1] - offsetY) / getScaleGridSize()) + 1);
            tryLinkDragPin(referenceAction);
            addAction(referenceAction);
        }
    }

    public void removeAction(Action action) {
        functionContext.removeAction(action);
        ActionCard<?> card = cardMap.remove(action.getId());
        if (card == null) return;
        for (Pin pin : action.getPins()) {
            pinRemoveLinks(card.getPinViewById(pin.getId()));
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
        float newX = (2 - x) * getScaleGridSize();
        float newY = (2 - y) * getScaleGridSize();
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(animation -> {
            float percent = (float) animation.getAnimatedValue();
            offsetX += (newX - offsetX) * percent;
            offsetY += (newY - offsetY) * percent;
            setCardsPosition();
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                for (Map.Entry<String, ActionCard<?>> entry : cardMap.entrySet()) {
                    ActionCard<?> card = entry.getValue();
                    Action action = card.getAction();
                    if (action.getX() == x && action.getY() == y) {
                        if (actionClass.isInstance(action)) {
                            card.flick();
                            break;
                        }
                    }
                }
            }
        });
        animator.start();
    }

    private void showSelectActionDialog() {
        if (dragPin == null) return;
        SelectActionDialog actionDialog = new SelectActionDialog(getContext(), this, dragPin.getPin().getPinClass(), dragOut);
        if (actionDialog.isEmpty()) return;
        dialog = new MaterialAlertDialogBuilder(getContext())
                .setView(actionDialog)
                .setOnDismissListener(dialog -> {
                    this.dialog = null;
                    dragLinks.clear();
                    dragCard = null;
                    dragPin = null;
                    dragX = location[0];
                    dragY = location[1];
                    dragState = DRAG_NONE;
                    postInvalidate();
                })
                .show();
    }

    public void dismissDialog() {
        if (dialog != null) dialog.dismiss();
    }

    public boolean tryLinkDragPin(Action action) {
        if (dragPin != null) {
            Pin pin = action.getFirstPinByClass(dragPin.getPin().getPinClass(), dragOut);
            if (pin != null) {
                pin.addLinks(dragLinks, functionContext);
                return true;
            }
        }
        return false;
    }

    private float getScaleGridSize() {
        return gridSize * scale;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        float gridScaleSize = getScaleGridSize();
        float ofX = offsetX % gridScaleSize;
        float ofY = offsetY % gridScaleSize;
        canvas.translate(ofX, ofY);

        // 格子背景
        float gridRow = getHeight() / gridScaleSize; //有多少行
        float gridCol = getWidth() / gridScaleSize;  //有多少列

        float bigGridSize = 10 * gridScaleSize;
        float startY = offsetY - ofY;
        for (int i = 0; i < gridRow; i++) {
            if (startY == i * gridScaleSize) {
                gridPaint.setStrokeWidth(6);
            } else {
                float v = (startY - i * gridScaleSize) % bigGridSize;
                gridPaint.setStrokeWidth((Math.abs(v) < 1 || Math.abs(v) > bigGridSize - 1) ? 2 : 0.5f);
            }
            canvas.drawLine(-gridScaleSize, i * gridScaleSize, getWidth() + gridScaleSize, i * gridScaleSize, gridPaint);
        }

        float startX = offsetX - ofX;
        for (int i = 0; i < gridCol; i++) {
            if (offsetX == i * gridScaleSize + ofX) {
                gridPaint.setStrokeWidth(4);
            } else {
                float v = (startX - i * gridScaleSize) % bigGridSize;
                gridPaint.setStrokeWidth((Math.abs(v) < 1 || Math.abs(v) > bigGridSize - 1) ? 2 : 0.5f);
            }
            canvas.drawLine(i * gridScaleSize, -gridScaleSize, i * gridScaleSize, getHeight() + gridScaleSize, gridPaint);
        }
        canvas.restore();

        // 所有连接的线
        linePaint.setStrokeWidth(5 * scale);
        for (ActionCard<?> card : cardMap.values()) {
            Action action = card.getAction();
            for (Pin pin : action.getPins()) {
                for (Map.Entry<String, String> entry : pin.getLinks().entrySet()) {
                    ActionCard<?> baseCard = cardMap.get(entry.getValue());
                    if (baseCard == null) continue;
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

        if (dragState == DRAG_PIN) {
            for (Map.Entry<String, String> entry : dragLinks.entrySet()) {
                ActionCard<?> card = cardMap.get(entry.getValue());
                if (card == null) continue;
                PinView pinBaseView = card.getPinViewById(entry.getKey());
                if (pinBaseView == null) continue;
                if (matchedPin != null &&
                        (
                                (isBreakLink && dragPin.getPin().isSameValueType(matchedPin.getPin())) ||
                                        (!isBreakLink && dragPin.getPin().isCanLink(matchedPin.getPin()))
                        )
                ) {
                    linePaint.setColor(dragPin.getPin().getValue().getPinColor(getContext()));
                } else {
                    linePaint.setColor(DisplayUtils.getAttrColor(getContext(), com.google.android.material.R.attr.colorPrimaryInverse, 0));
                }
                canvas.drawPath(calculateLinePath(pinBaseView), linePaint);
            }
        }

        // 所有卡片
        super.dispatchDraw(canvas);
    }

    //带拐点的路径，尽可能少的拐点
    private Path calculateLinePath(int[] outLocation, int[] inLocation, boolean v) {
        Path path = new Path();
        if (outLocation == null || inLocation == null) return path;
        float scaleGridSize = getScaleGridSize();

        PointF outLinkLinePoint;
        PointF inLinkLinePoint;

        if (v) {
            outLinkLinePoint = new PointF(outLocation[0], outLocation[1] + scaleGridSize);
            inLinkLinePoint = new PointF(inLocation[0], inLocation[1] - scaleGridSize);
        } else {
            outLinkLinePoint = new PointF(outLocation[0] + scaleGridSize, outLocation[1]);
            inLinkLinePoint = new PointF(inLocation[0] - scaleGridSize, inLocation[1]);
        }

        // 结束点在右边，为正方向
        int xScale = outLinkLinePoint.x < inLinkLinePoint.x ? 1 : -1;
        // 结束点在下边，为正方向
        int yScale = outLinkLinePoint.y < inLinkLinePoint.y ? 1 : -1;

        path.moveTo(outLocation[0], outLocation[1]);
        path.lineTo(outLinkLinePoint.x, outLinkLinePoint.y);

        float offsetX = Math.abs(outLinkLinePoint.x - inLinkLinePoint.x);
        float offsetY = Math.abs(outLinkLinePoint.y - inLinkLinePoint.y);
        boolean xLong = offsetX > offsetY;

        /*
        垂直连接：
            X长度为0：
                yScale = 1, 向下连接，看其他条件
                yScale = -1, 向右绕2格连接
            X更长：
                yScale = 1, 就先竖，再横，再竖
                yScale = -1， 就先横，再斜，再横
            Y更长：
                yScale = 1, 就先竖，再斜，再竖
                yScale = -1, 就先横，再竖，再横
        水平连接：
            X更长：
                xScale = 1, 就先横，再斜，再横
                xScale = -1, 就先竖，再横，再竖
            Y更长：
                xScale = 1, 就先横，再竖，再横
                xScale = -1, 就先竖，再斜，再竖
            Y长度为0：
                xScale = 1, 水平连接，看其他条件
                xScale = -1, 向下绕2格连接
        */
        float linkLineLen = Math.abs(offsetX - offsetY) / 2;

        boolean flag = true;
        if (offsetX < scaleGridSize * 3.1 && v) {
            if (yScale == 1 && offsetX < scaleGridSize / 2) {
                flag = false;
            } else if (yScale == -1 && offsetY > scaleGridSize) {
                // 向左绕2格连接
                float x = Math.max(outLinkLinePoint.x, inLinkLinePoint.x) - scaleGridSize * 6;
                path.lineTo(x, outLinkLinePoint.y);
                path.lineTo(x, inLinkLinePoint.y);
                flag = false;
            }
        } else if (offsetY < scaleGridSize * 3.1 && !v) {
            if (xScale == 1 && offsetY < scaleGridSize / 2) {
                flag = false;
            } else if (xScale == -1 && offsetX > scaleGridSize) {
                //向下绕2格连接
                float y = Math.max(outLinkLinePoint.y, inLinkLinePoint.y) + scaleGridSize * 6;
                path.lineTo(outLinkLinePoint.x, y);
                path.lineTo(inLinkLinePoint.x, y);
                flag = false;
            }
        }

        if (flag) {
            if (xLong) {
                if ((v && yScale == 1) || (!v && xScale == -1)) {
                    //就先竖，再横，再竖
                    path.lineTo(outLinkLinePoint.x, outLinkLinePoint.y + offsetY / 2 * yScale);
                    path.lineTo(inLinkLinePoint.x, inLinkLinePoint.y - offsetY / 2 * yScale);
                } else {
                    //就先横，再斜，再横
                    path.lineTo(outLinkLinePoint.x + linkLineLen * xScale, outLinkLinePoint.y);
                    path.lineTo(inLinkLinePoint.x - linkLineLen * xScale, inLinkLinePoint.y);
                }
            } else {
                if ((v && yScale == 1) || (!v && xScale == -1)) {
                    //就先竖，再斜，再竖
                    path.lineTo(outLinkLinePoint.x, outLinkLinePoint.y + linkLineLen * yScale);
                    path.lineTo(inLinkLinePoint.x, inLinkLinePoint.y - linkLineLen * yScale);
                } else {
                    //就先横，再竖，再横
                    path.lineTo(outLinkLinePoint.x + offsetX / 2 * xScale, outLinkLinePoint.y);
                    path.lineTo(inLinkLinePoint.x - offsetX / 2 * xScale, inLinkLinePoint.y);
                }
            }
        }

        path.lineTo(inLinkLinePoint.x, inLinkLinePoint.y);
        path.lineTo(inLocation[0], inLocation[1]);

        path.offset(-location[0] - getX(), -location[1] - getY());

        return path;
    }

    private Path calculateLinePath(PinView outPin, PinView inPin) {
        Path path = new Path();
        if (outPin == null || inPin == null) return path;

        int[] outLocation = outPin.getSlotLocationOnScreen(scale);
        int[] inLocation = inPin.getSlotLocationOnScreen(scale);
        return calculateLinePath(outLocation, inLocation, outPin.getPin().isVertical());
    }

    private Path calculateLinePath(PinView pinBaseView) {
        Path path = new Path();
        if (pinBaseView == null) return path;
        int[] pinLocation = pinBaseView.getSlotLocationOnScreen(scale);

        int[] outLocation, inLocation;
        if (dragOut) {
            inLocation = pinLocation;
            outLocation = new int[]{(int) dragX, (int) dragY};
        } else {
            inLocation = new int[]{(int) dragX, (int) dragY};
            outLocation = pinLocation;
        }
        return calculateLinePath(outLocation, inLocation, pinBaseView.getPin().isVertical());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        if (dragState == DRAG_SCALE) return true;

        float rawX = event.getRawX();
        float rawY = event.getRawY();
        int actionMasked = event.getActionMasked();
        if (actionMasked == MotionEvent.ACTION_DOWN) {
            startX = rawX;
            startY = rawY;
            isClick = true;
            if (editMode) {
                for (int i = getChildCount() - 1; i >= 0; i--) {
                    ActionCard<?> card = (ActionCard<?>) getChildAt(i);
                    int[] location = new int[2];
                    card.getLocationOnScreen(location);
                    if (new Rect(location[0], location[1], location[0] + (int) (card.getWidth() * scale), location[1] + (int) (card.getHeight() * scale)).contains((int) rawX, (int) rawY)) {
                        dragState = DRAG_CARD;
                        dragCard = card;
                        PinView pinBaseView = card.getPinViewByPos(rawX, rawY);
                        if (pinBaseView != null) {
                            Pin pin = pinBaseView.getPin();
                            dragState = DRAG_PIN;
                            dragPin = pinBaseView;
                            HashMap<String, String> links = pin.getLinks();
                            // 数量为0 或者 是出线且可以出多条线，从这个点出线。进线要么连接，要么断开
                            if (links.size() == 0 || (!pin.isSingleLink() && pin.isOut())) {
                                dragLinks.put(pin.getId(), pin.getActionId());
                                // 目标方向与自身相反
                                dragOut = !pin.isOut();
                                isBreakLink = false;
                            } else {
                                // 否则就是挪线
                                dragLinks.putAll(links);
                                dragOut = pin.isOut();
                                isBreakLink = true;
                                pinRemoveLinks(pinBaseView);
                            }
                        } else {
                            dragCard.bringToFront();
                        }
                        dragX = rawX;
                        dragY = rawY;
                        break;
                    }
                }
            }
            if (dragState == DRAG_NONE) {
                dragState = DRAG_SELF;
                dragX = rawX;
                dragY = rawY;
            }
        } else if (actionMasked == MotionEvent.ACTION_UP) {
            if (dragState == DRAG_PIN) {
                if (isClick) {
                    if (dragPin != null) {
                        pinRemoveLinks(dragPin);
                    }
                } else {
                    boolean flag = true;
                    ActionCard<?> selectCard = null;
                    // 看是否放到针脚上了
                    for (ActionCard<?> baseCard : cardMap.values()) {
                        int[] location = new int[2];
                        baseCard.getLocationOnScreen(location);
                        if (new Rect(location[0], location[1], location[0] + (int) (baseCard.getWidth() * scale), location[1] + (int) (baseCard.getHeight() * scale)).contains((int) rawX, (int) rawY)) {
                            if (selectCard == null) selectCard = baseCard;

                            PinView pinBaseView = baseCard.getPinViewByPos(rawX, rawY);
                            if (pinBaseView == null) continue;
                            if (pinAddLinks(pinBaseView, dragLinks)) {
                                flag = false;
                                break;
                            }
                        }
                    }

                    // 看是否能连接卡片
                    if (flag && selectCard != null) {
                        flag = !tryLinkDragPin(selectCard.getAction());
                    }

                    // 无效的拖动，尝试弹出能连接这个拖动针脚的所有动作
                    if (flag && dragPin != null && !isBreakLink) {
                        showSelectActionDialog();
                        postInvalidate();
                        return true;
                    }
                }
            }

            dragLinks.clear();
            dragCard = null;
            dragPin = null;
            dragX = location[0];
            dragY = location[1];
            dragState = DRAG_NONE;
        } else if (actionMasked == MotionEvent.ACTION_MOVE) {
            if (Math.abs(rawX - startX) * Math.abs(rawY - startY) > 81) {
                isClick = false;
            }

            if (dragState == DRAG_CARD) {
                float scaleGridSize = getScaleGridSize();
                Action action = dragCard.getAction();
                int dx = (int) ((rawX - dragX) / scaleGridSize);
                if (dx != 0) {
                    action.setX(action.getX() + dx);
                    dragX += dx * scaleGridSize;
                }
                int dy = (int) ((rawY - dragY) / scaleGridSize);
                if (dy != 0) {
                    action.setY(action.getY() + dy);
                    dragY += dy * scaleGridSize;
                }
                setCardPosition(dragCard);
            } else if (dragState == DRAG_PIN) {
                dragX = rawX;
                dragY = rawY;
                int width = getWidth();
                int height = getHeight();
                float offset = gridSize;
                float areaSize = gridSize * 4;
                if (rawX - location[0] < areaSize) {
                    offsetX += offset;
                } else if (rawX - location[0] > width - areaSize) {
                    offsetX -= offset;
                }
                if (rawY - location[1] < areaSize) {
                    offsetY += offset;
                } else if (rawY - location[1] > height - areaSize) {
                    offsetY -= offset;
                }
                setCardsPosition();

                matchedPin = null;
                for (ActionCard<?> baseCard : cardMap.values()) {
                    int[] location = new int[2];
                    baseCard.getLocationOnScreen(location);
                    if (new Rect(location[0], location[1], location[0] + (int) (baseCard.getWidth() * scale), location[1] + (int) (baseCard.getHeight() * scale)).contains((int) rawX, (int) rawY)) {
                        PinView pinBaseView = baseCard.getPinViewByPos(rawX, rawY);
                        if (pinBaseView == null) continue;
                        matchedPin = pinBaseView;
                        break;
                    }
                }
            } else if (dragState == DRAG_SELF) {
                offsetX += (rawX - dragX);
                offsetY += (rawY - dragY);
                dragX = rawX;
                dragY = rawY;
                setCardsPosition();
            }
        }
        postInvalidate();
        return true;
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

    private boolean pinAddLinks(PinView pinBaseView, HashMap<String, String> links) {
        Pin pin = pinBaseView.getPin();
        return pin.addLinks(links, functionContext);
    }

    private void pinRemoveLinks(PinView pinBaseView) {
        Pin pin = pinBaseView.getPin();
        pin.cleanLinks(functionContext);
    }

    public FunctionContext getFunctionContext() {
        return functionContext;
    }

    public HashMap<ActionType, Action> getTmpActions() {
        return tmpActions;
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

    public void refreshVariableActionPins(Action action) {
        ActionCard<?> baseCard = cardMap.get(action.getId());
        if (baseCard == null) return;
        for (Pin pin : action.getPins()) {
            if (pin.getValue() instanceof PinValue) {
                pin.cleanLinks(functionContext);
            }
        }
        postInvalidate();
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
        cardMap.forEach((id, card) -> {
            if (card.getAction() instanceof GetCommonVariableValue getValue) {
                if (getValue.getVarKey().equals(key)) {
                    getValue.setValue((PinValue) value.copy());
                    refreshVariableActionPins(getValue);
                }
            }
            if (card.getAction() instanceof SetCommonVariableValue setValue) {
                if (setValue.getVarKey().equals(key)) {
                    setValue.setValue((PinValue) value.copy());
                    refreshVariableActionPins(setValue);
                }
            }
        });
    }

    @Override
    public void onRemoved(String key, PinValue value) {
        checkCards();
    }
}
