package top.bogey.touch_tool_pro.ui.picker;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.amrdeveloper.treeview.TreeNode;
import com.amrdeveloper.treeview.TreeNodeManager;
import com.amrdeveloper.treeview.TreeViewAdapter;
import com.amrdeveloper.treeview.TreeViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.databinding.FloatPickerNodeItemBinding;
import top.bogey.touch_tool_pro.utils.DisplayUtils;

public class NodePickerTreeAdapter extends TreeViewAdapter {
    private final ArrayList<TreeNode> treeNodes = new ArrayList<>();
    private final TreeNodeManager manager;
    private final ArrayList<NodePickerItemInfo> roots;
    private TreeNode selectedNode;


    public NodePickerTreeAdapter(TreeNodeManager manager, SelectNode picker, ArrayList<NodePickerItemInfo> roots) {
        super(null, manager);
        this.manager = manager;
        this.roots = roots;
        setTreeNodeLongClickListener((treeNode, view) -> {
            NodePickerItemInfo nodeInfo = (NodePickerItemInfo) treeNode.getValue();
            picker.selectNode(nodeInfo);
            setSelectedNode(nodeInfo);
            return true;
        });
        searchNodes(null);
    }

    @Override
    public void onBindViewHolder(@NonNull TreeViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ((ViewHolder) holder).refreshItem(manager.get(position));
    }

    @NonNull
    @Override
    public TreeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int layoutId) {
        return new ViewHolder(FloatPickerNodeItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    public void searchNodes(String search) {
        treeNodes.clear();
        Pattern pattern = null;
        if (search != null && !search.isEmpty()) {
            try {
                pattern = Pattern.compile(search);
            } catch (PatternSyntaxException ignored){
            }
        }
        for (NodePickerItemInfo root : roots) {
            if (pattern == null) {
                TreeNode rootNode = createTree(root, 0);
                treeNodes.add(rootNode);
            } else {
                TreeNode rootNode = searchTree(root, 0, pattern);
                if (rootNode != null) treeNodes.add(rootNode);
            }
        }
        updateTreeNodes(treeNodes);
        if (pattern != null) manager.expandAll();
    }

    private TreeNode createTree(NodePickerItemInfo root, int level) {
        TreeNode node = new TreeNode(root, R.layout.float_picker_node_item);
        node.setLevel(level);
        for (NodePickerItemInfo child : root.children) {
            if (child != null) {
                node.addChild(createTree(child, level + 1));
            }
        }
        return node;
    }

    private TreeNode searchTree(NodePickerItemInfo root, int level, Pattern pattern) {
        TreeNode node = new TreeNode(root, R.layout.float_picker_node_item);
        node.setLevel(level);

        boolean finded = false;
        if (root.text != null && pattern.matcher(root.text).find()) {
            finded = true;
        } else if (root.id != null && pattern.matcher(root.id).find()) {
            finded = true;
        } else if (root.cls != null && pattern.matcher(root.cls).find()) {
            finded = true;
        }

        for (NodePickerItemInfo child : root.children) {
            if (child != null) {
                TreeNode treeNode = searchTree(child, level + 1, pattern);
                if (treeNode != null) node.addChild(treeNode);
            }
        }
        if (node.getChildren().isEmpty() && !finded) return null;
        return node;
    }

    public void setSelectedNode(NodePickerItemInfo node) {
        collapseAll();
        if (node == null) {
            selectedNode = null;
        } else {
            selectedNode = findTreeNode(treeNodes, node);
            if (selectedNode != null) {
                TreeNode parent = selectedNode.getParent();
                while (parent != null) {
                    TreeNode p = parent.getParent();
                    if (p == null) {
                        parent.setExpanded(false);
                        expandNode(parent);
                    } else {
                        parent.setExpanded(true);
                    }
                    parent = p;
                }
            }
        }
    }

    private TreeNode findTreeNode(List<TreeNode> treeNodes, Object value) {
        for (TreeNode treeNode : treeNodes) {
            if (value.equals(treeNode.getValue())) return treeNode;
            TreeNode node = findTreeNode(treeNode.getChildren(), value);
            if (node != null) return node;
        }
        return null;
    }

    public interface SelectNode {
        void selectNode(NodePickerItemInfo info);
    }

    protected class ViewHolder extends TreeViewHolder {
        private final FloatPickerNodeItemBinding binding;
        private final Context context;

        public ViewHolder(@NonNull FloatPickerNodeItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            context = binding.getRoot().getContext();
        }

        @Override
        public void bindTreeNode(TreeNode node) {
            int padding = (int) (node.getLevel() * DisplayUtils.dp2px(context, 8));
            binding.contentBox.setPaddingRelative(padding, 0, 0, 0);
        }

        public void refreshItem(TreeNode node) {
            NodePickerItemInfo value = (NodePickerItemInfo) node.getValue();
            binding.titleText.setText(getNodeTitle(value));

            int color;
            if (value.isUsable()) {
                color = DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorPrimary, 0);
            } else {
                color = DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorOnSurface, 0);
            }
            binding.titleText.setTextColor(color);
            binding.imageView.setImageTintList(ColorStateList.valueOf(color));

            binding.imageView.setVisibility(node.getChildren().size() > 0 ? View.VISIBLE : View.INVISIBLE);
            binding.imageView.setImageResource(node.isExpanded() ? R.drawable.icon_up : R.drawable.icon_down);

            if (node.equals(selectedNode)) {
                binding.titleText.setTextColor(DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorError, 0));
            }
        }

        private String getNodeTitle(NodePickerItemInfo node) {
            StringBuilder builder = new StringBuilder();
            builder.append(node.cls);
            if (node.text != null && !node.text.isEmpty()) {
                builder.append(" | ");
                builder.append(node.text);
            }

            if (node.id != null && !node.id.isEmpty()) {
                String[] split = node.id.split(":");
                if (split.length > 1) {
                    builder.append(" [ ");
                    builder.append(split[1]);
                    builder.append(" ]");
                } else {
                    builder.append(node.id);
                }
            }

            return builder.toString();
        }
    }
}