package top.bogey.touch_tool_pro.ui.blueprint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.amrdeveloper.treeview.TreeNode;
import com.amrdeveloper.treeview.TreeNodeManager;
import com.amrdeveloper.treeview.TreeViewAdapter;
import com.amrdeveloper.treeview.TreeViewHolder;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionConfigInfo;
import top.bogey.touch_tool_pro.bean.action.ActionMap;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.Function;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.task.TaskExample;
import top.bogey.touch_tool_pro.databinding.DialogActionHelpBinding;
import top.bogey.touch_tool_pro.databinding.ViewCardListItemBinding;
import top.bogey.touch_tool_pro.databinding.ViewCardListTypeItemBinding;
import top.bogey.touch_tool_pro.super_user.SuperUser;
import top.bogey.touch_tool_pro.utils.DisplayUtils;

public class ActionTreeAdapter extends TreeViewAdapter {
    private final TreeNodeManager manager;
    private final CardLayoutView cardLayoutView;

    public ActionTreeAdapter(CardLayoutView cardLayoutView, TreeNodeManager manager) {
        super(null, manager);
        this.manager = manager;
        this.cardLayoutView = cardLayoutView;

        setTreeNodeClickListener((treeNode, view) -> {
            if (treeNode.getLevel() == 1) {
                ActionType type = (ActionType) treeNode.getValue();
                cardLayoutView.addAction(type.getConfig().getActionClass());
            }
        });

        initRoot();
    }

    @Override
    public void onBindViewHolder(@NonNull TreeViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ((ViewHolder) holder).refreshItem(manager.get(position));
    }

    @SuppressLint("NonConstantResourceId")
    @NonNull
    @Override
    public TreeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int layoutId) {
        if (layoutId == R.layout.view_card_list_type_item) {
            return new ViewHolder(ViewCardListTypeItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
        return new ViewHolder(ViewCardListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    public void initRoot() {
        ArrayList<TreeNode> treeNodes = new ArrayList<>();
        FunctionContext functionContext = cardLayoutView.getFunctionContext();
        for (ActionMap actionMap : ActionMap.values()) {
            if (functionContext instanceof Function && actionMap == ActionMap.START) continue;
            TreeNode treeNode = new TreeNode(actionMap, R.layout.view_card_list_type_item);
            actionMap.getTypes().forEach(type -> {
                if (!SuperUser.isSuperUser() && type.getConfig().isSuperAction()) return;
                TreeNode node = new TreeNode(type, R.layout.view_card_list_item);
                treeNode.addChild(node);
            });
            if (treeNode.getChildren().size() > 0) treeNodes.add(treeNode);
        }
        updateTreeNodes(treeNodes);
    }

    protected class ViewHolder extends TreeViewHolder {
        private final Context context;
        private ViewCardListTypeItemBinding typeBinding;
        private ViewCardListItemBinding itemBinding;

        public ViewHolder(@NonNull ViewCardListTypeItemBinding binding) {
            super(binding.getRoot());
            typeBinding = binding;
            context = binding.getRoot().getContext();
            setNodePadding(0);
        }

        public ViewHolder(@NonNull ViewCardListItemBinding binding) {
            super(binding.getRoot());
            itemBinding = binding;
            context = binding.getRoot().getContext();
            setNodePadding(0);
            itemBinding.icon.setVisibility(View.VISIBLE);

            itemBinding.helpButton.setVisibility(View.VISIBLE);
            itemBinding.helpButton.setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                TreeNode treeNode = manager.get(index);
                ActionType type = (ActionType) treeNode.getValue();
                ActionConfigInfo config = type.getConfig();
                TaskExample example = config.getExample(context);
                if (example == null) {
                    Toast.makeText(context, R.string.no_action_help_tips, Toast.LENGTH_SHORT).show();
                } else {
                    DialogActionHelpBinding helpBinding = DialogActionHelpBinding.inflate(LayoutInflater.from(context), itemBinding.getRoot(), false);
                    helpBinding.cardLayout.setFunctionContext(example);
                    helpBinding.cardLayout.setEditMode(false);
                    helpBinding.cardLayout.setMinimumHeight((int) (DisplayUtils.getScreen(context) * 0.75));
                    helpBinding.des.setText(config.getDescription());
                    new MaterialAlertDialogBuilder(context)
                            .setTitle(config.getTitle())
                            .setView(helpBinding.getRoot())
                            .show();
                }
            });
        }

        public void refreshItem(TreeNode node) {
            int level = node.getLevel();
            if (level == 0) {
                ActionMap actionMap = (ActionMap) node.getValue();
                typeBinding.title.setText(actionMap.getTitle());
            } else if (level == 1) {
                ActionType type = (ActionType) node.getValue();
                ActionConfigInfo config = type.getConfig();
                itemBinding.title.setText(config.getTitle());
                itemBinding.icon.setImageResource(config.getIcon());
                ViewGroup.LayoutParams params = itemBinding.space.getLayoutParams();
                params.width = (int) (DisplayUtils.dp2px(context, 8) * level);
                itemBinding.space.setLayoutParams(params);
            }
        }
    }
}
