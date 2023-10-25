package top.bogey.touch_tool_pro.ui.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amrdeveloper.treeview.TreeNodeManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.pins.PinNodePath;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.databinding.FloatPickerNodeBinding;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;
import top.bogey.touch_tool_pro.utils.DisplayUtils;
import top.bogey.touch_tool_pro.utils.TextChangedListener;
import top.bogey.touch_tool_pro.utils.easy_float.EasyFloat;

@SuppressLint("ViewConstructor")
public class NodePickerFloatView extends BasePickerFloatView implements NodePickerTreeAdapter.SelectNode {
    protected final FloatPickerNodeBinding binding;
    private final NodePickerTreeAdapter adapter;

    private final Paint gridPaint;
    private final Paint markPaint;
    private final int[] location = new int[2];

    private final ArrayList<AccessibilityNodeInfo> rootNodes;
    private AccessibilityNodeInfo selectNode;
    private String selectId;

    public NodePickerFloatView(@NonNull Context context, PickerCallback callback, PinString pinNode) {
        super(context, callback);

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setStrokeCap(Paint.Cap.ROUND);
        gridPaint.setStrokeJoin(Paint.Join.ROUND);
        gridPaint.setStyle(Paint.Style.STROKE);

        markPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markPaint.setStyle(Paint.Style.FILL);
        markPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        binding = FloatPickerNodeBinding.inflate(LayoutInflater.from(context), this, true);
        MainAccessibilityService service = MainApplication.getInstance().getService();
        rootNodes = service.getNeedWindowsRoot();

        adapter = new NodePickerTreeAdapter(new TreeNodeManager(), this, rootNodes);
        binding.widgetRecyclerView.setAdapter(adapter);

        binding.saveButton.setOnClickListener(v -> {
            if (pinNode instanceof PinNodePath pinNodePath) {
                pinNodePath.setValue(selectNode);
            } else {
                pinNode.setValue(selectId);
            }
            if (callback != null) callback.onComplete();
            dismiss();
        });

        binding.detailButton.setOnClickListener(v -> {
            BottomSheetBehavior<FrameLayout> sheetBehavior = BottomSheetBehavior.from(binding.bottomSheet);
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        binding.searchEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s == null) {
                    adapter.searchNodes(null);
                } else {
                    adapter.searchNodes(s.toString());
                }
            }
        });

        binding.backButton.setOnClickListener(v -> dismiss());

        binding.markBox.setOnClickListener(v -> showNodeView((AccessibilityNodeInfo) null));

        if (pinNode instanceof PinNodePath pinNodePath) {
            selectNode = pinNodePath.getNode(rootNodes);
            showNodeView(selectNode);
        } else {
            selectId = pinNode.getValue();
            showNodeView(selectId);
        }
    }

    @Override
    public void selectNode(AccessibilityNodeInfo nodeInfo) {
        selectNode = nodeInfo;

        binding.markBox.setVisibility(GONE);
        binding.idTitle.setVisibility(GONE);

        if (selectNode != null) {
            selectId = selectNode.getViewIdResourceName();

            binding.markBox.setVisibility(VISIBLE);
            binding.idTitle.setVisibility(VISIBLE);

            binding.idTitle.setText(selectId);
            binding.idTitle.setVisibility(selectId == null ? INVISIBLE : VISIBLE);

            Rect rect = new Rect();

            selectNode.getBoundsInScreen(rect);
            ViewGroup.LayoutParams params = binding.markBox.getLayoutParams();
            params.width = rect.width();
            params.height = rect.height();
            binding.markBox.setLayoutParams(params);
            binding.markBox.setX(rect.left);
            binding.markBox.setY(rect.top - location[1]);
        }
        postInvalidate();
    }

    public void showNodeView(AccessibilityNodeInfo nodeInfo) {
        selectNode(nodeInfo);
        adapter.setSelectedNode(nodeInfo);
    }

    public void showNodeView(String nodeId) {
        if (nodeId == null || nodeId.isEmpty()) {
            showNodeView((AccessibilityNodeInfo) null);
            return;
        }
        for (AccessibilityNodeInfo root : rootNodes) {
            List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByViewId(nodeId);
            if (nodes.size() == 1) {
                AccessibilityNodeInfo node = nodes.get(0);
                showNodeView(node);
                break;
            }
        }
        showNodeView((AccessibilityNodeInfo) null);
    }

    @Override
    public void show() {
        EasyFloat.with(MainApplication.getInstance().getService())
                .setLayout(this)
                .setTag(tag)
                .setDragEnable(false)
                .hasEditText(true)
                .setMatch(true, true)
                .setCallback(floatCallback)
                .setAnimator(null)
                .show();
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        long drawingTime = getDrawingTime();

        drawChild(canvas, binding.getRoot(), drawingTime);

        canvas.saveLayer(getLeft(), getTop(), getRight(), getBottom(), gridPaint);
        for (int i = rootNodes.size() - 1; i >= 0; i--) {
            canvas.save();
            AccessibilityNodeInfo rootNode = rootNodes.get(i);
            Rect bounds = new Rect();
            rootNode.getBoundsInScreen(bounds);
            if (bounds.width() != getWidth() || bounds.height() != getHeight()) {
                bounds.offset(-location[0], -location[1]);
                canvas.drawRect(bounds, markPaint);
            }
            drawNode(canvas, rootNode);
            canvas.restore();
        }
        canvas.restore();

        if (selectNode != null) {
            canvas.save();
            int[] boxLocation = new int[2];
            binding.markBox.getLocationOnScreen(boxLocation);
            boxLocation[0] -= location[0];
            boxLocation[1] -= location[1];
            Rect rect = new Rect(boxLocation[0], boxLocation[1], binding.markBox.getWidth() + boxLocation[0], binding.markBox.getHeight() + boxLocation[1]);
            canvas.drawRect(rect, markPaint);
            canvas.restore();

            drawChild(canvas, binding.markBox, drawingTime);
            drawChild(canvas, binding.idTitle, drawingTime);
        }

        drawChild(canvas, binding.buttonBox, drawingTime);
        drawChild(canvas, binding.bottomSheet, drawingTime);
    }

    private void drawNode(Canvas canvas, AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) return;

        Rect bounds = new Rect();
        nodeInfo.getBoundsInScreen(bounds);
        bounds.offset(-location[0], -location[1]);

        boolean touchAble = nodeInfo.isClickable() || nodeInfo.isEditable() || nodeInfo.isCheckable() || nodeInfo.isLongClickable();

        if (!touchAble && nodeInfo.isVisibleToUser()) {
            gridPaint.setColor(DisplayUtils.getAttrColor(getContext(), com.google.android.material.R.attr.colorSecondary, 0));
            gridPaint.setStrokeWidth(1);
            canvas.drawRect(bounds, gridPaint);
        }

        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            drawNode(canvas, nodeInfo.getChild(i));
        }

        if (touchAble && nodeInfo.isVisibleToUser()) {
            gridPaint.setColor(DisplayUtils.getAttrColor(getContext(), R.attr.colorPrimaryLight, 0));
            gridPaint.setStrokeWidth(3);
            bounds.offset(2, 2);
            bounds.right -= 4;
            bounds.bottom -= 4;
            canvas.drawRect(bounds, gridPaint);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();

        if (event.getAction() == MotionEvent.ACTION_UP) {
            AccessibilityNodeInfo node = getNodeIn((int) x, (int) y);
            if (node != null) {
                showNodeView(node);
            }
        }
        return true;
    }

    @Nullable
    private AccessibilityNodeInfo getNodeIn(int x, int y) {
        if (rootNodes == null || rootNodes.size() == 0) return null;
        HashMap<AccessibilityNodeInfo, Integer> infoMap = new HashMap<>();
        for (int i = 0; i < rootNodes.size(); i++) {
            AccessibilityNodeInfo rootNode = rootNodes.get(i);
            findNodeIn(infoMap, (rootNodes.size() - i) * Short.MAX_VALUE, rootNode, x, y);
        }

        int deep = 0;
        AccessibilityNodeInfo node = null;
        for (Map.Entry<AccessibilityNodeInfo, Integer> entry : infoMap.entrySet()) {
            // 深度最深
            if (deep < entry.getValue()) {
                deep = entry.getValue();
                node = entry.getKey();
            }
        }
        return node;
    }

    private void findNodeIn(HashMap<AccessibilityNodeInfo, Integer> infoHashSet, int deep, @NonNull AccessibilityNodeInfo nodeInfo, int x, int y) {
        if (nodeInfo.getChildCount() == 0) return;
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo child = nodeInfo.getChild(i);
            if (child != null) {
                Rect rect = new Rect();
                child.getBoundsInScreen(rect);
                if (rect.contains(x, y) && child.isVisibleToUser()) {
                    infoHashSet.put(child, deep);
                }
                findNodeIn(infoHashSet, deep + 1, child, x, y);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (changed) {
            getLocationOnScreen(location);
        }
    }
}
