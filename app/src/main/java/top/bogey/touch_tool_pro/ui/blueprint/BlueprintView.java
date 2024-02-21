package top.bogey.touch_tool_pro.ui.blueprint;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;

import com.amrdeveloper.treeview.TreeNodeManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.HashMap;
import java.util.Stack;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.pins.PinImage;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.task.Task;
import top.bogey.touch_tool_pro.databinding.ViewBlueprintBinding;
import top.bogey.touch_tool_pro.save.SaveRepository;
import top.bogey.touch_tool_pro.ui.MainActivity;
import top.bogey.touch_tool_pro.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool_pro.utils.AppUtils;
import top.bogey.touch_tool_pro.utils.SettingSave;

public class BlueprintView extends Fragment {
    private final Stack<FunctionContext> functionContextStack = new Stack<>();
    private ViewBlueprintBinding binding;
    private ActionSideSheetDialog dialog;
    private Task task;

    private ActionCard<?> showCard = null;

    public static void tryPushActionContext(FunctionContext functionContext) {
        MainActivity activity = MainApplication.getInstance().getMainActivity();
        if (activity == null) return;
        BlueprintView currFragment = activity.getCurrFragment(BlueprintView.class);
        if (currFragment == null) return;
        currFragment.pushActionContext(functionContext);
    }

    private final OnBackPressedCallback callback = new OnBackPressedCallback(false) {
        @Override
        public void handleOnBackPressed() {
            popActionContext();
        }
    };

    public static void tryShowCard(String actionId, HashMap<String, PinValue> values) {
        MainActivity activity = MainApplication.getInstance().getMainActivity();
        if (activity == null) return;
        BlueprintView currFragment = activity.getCurrFragment(BlueprintView.class);
        if (currFragment == null) return;
        if (currFragment.showCard != null) {
            currFragment.showCard.showDebugValue(new HashMap<>());
        }
        currFragment.showCard = currFragment.binding.cardLayout.showCard(actionId);
        if (currFragment.showCard != null) {
            currFragment.showCard.showDebugValue(values);
        }
    }

    private final MenuProvider menuProvider = new MenuProvider() {
        @Override
        public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
            menuInflater.inflate(R.menu.menu_task_detail, menu);
        }

        @Override
        public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.saveTask) {
                binding.cardLayout.getFunctionContext().save();
                return true;
            } else if (itemId == R.id.showLog) {
                if (task == null) return true;
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.task_running_log)
                        .setMessage(SaveRepository.getInstance().getLog(task.getId()))
                        .setPositiveButton(R.string.close, (dialog, which) -> dialog.dismiss())
                        .setNeutralButton(R.string.export_task, (dialog, which) -> {
                            dialog.dismiss();
                            AppUtils.exportString(getContext(), SaveRepository.getInstance().getLog(task.getId()));
                        })
                        .setNegativeButton(R.string.task_running_log_clear, (dialog, which) -> {
                            dialog.dismiss();
                            SaveRepository.getInstance().removeLog(task.getId());
                        })
                        .show();
                return true;
            } else if (itemId == R.id.capture) {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.dialog_title)
                        .setMessage(R.string.task_capture_tips)
                        .setPositiveButton(R.string.save, (dialog, which) -> {
                            dialog.dismiss();
                            Bitmap bitmap = binding.cardLayout.captureFunctionContext();
                            AppUtils.exportImage(getContext(), bitmap);
                        })
                        .setNegativeButton(R.string.action_share_action_title, (dialog, which) -> {
                            dialog.dismiss();
                            Bitmap bitmap = binding.cardLayout.captureFunctionContext();
                            Uri uri = AppUtils.valueToCacheFile(getContext(), new PinImage(bitmap));
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setType("image/*");
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.putExtra(Intent.EXTRA_STREAM, uri);

                            Intent chooser = Intent.createChooser(intent, "");
                            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(chooser);
                        })
                        .setNeutralButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                        .show();


            } else if (functionContextStack.size() > 1) {
                popActionContext();
                return true;
            }
            return false;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() == null) throw new IllegalArgumentException();
        String taskId = getArguments().getString("taskId");
        String functionId = getArguments().getString("functionId");
        task = SaveRepository.getInstance().getTaskById(taskId);
        FunctionContext functionContext = task;
        if (functionId != null && !functionId.isEmpty()) {
            functionContext = SaveRepository.getInstance().getFunction(taskId, functionId);
        }
        if (functionContext == null) throw new IllegalArgumentException();

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        requireActivity().addMenuProvider(menuProvider, getViewLifecycleOwner());

        binding = ViewBlueprintBinding.inflate(inflater, container, false);

        binding.addButton.setOnClickListener(v -> {
            binding.cardLayout.resetDragPos();
            ActionTreeAdapter adapter = new ActionTreeAdapter(binding.cardLayout, new TreeNodeManager());
            dialog = new ActionSideSheetDialog(requireContext(), adapter);
            dialog.show();
        });

        binding.attrButton.setOnClickListener(v -> {
            binding.cardLayout.resetDragPos();
            CustomTreeAdapter adapter = new CustomTreeAdapter(this, binding.cardLayout, new TreeNodeManager());
            dialog = new ActionSideSheetDialog(requireContext(), adapter);
            dialog.show();
        });

        binding.lockEditButton.setOnClickListener(v -> {
            boolean editMode = binding.cardLayout.isEditMode();
            binding.cardLayout.setEditMode(!editMode);
            binding.lockEditButton.setImageResource(editMode ? R.drawable.icon_hand : R.drawable.icon_edit);
        });
        boolean lookMode = !SettingSave.getInstance().isFirstLookBlueprint();
        binding.cardLayout.setEditMode(lookMode);
        binding.lockEditButton.setImageResource(lookMode ? R.drawable.icon_edit : R.drawable.icon_hand);

        pushActionContext(functionContext);

        return binding.getRoot();
    }

    public void pushActionContext(FunctionContext functionContext) {
        if (functionContext == null) return;

        if (functionContextStack.size() > 0) {
            functionContextStack.peek().save();
        }
        // 不能存在相同栈内元素
        functionContextStack.remove(functionContext);
        functionContextStack.push(functionContext);
        binding.cardLayout.setFunctionContext(functionContext);
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(functionContext.getTitle());
        }
        callback.setEnabled(functionContextStack.size() > 1);
    }

    public void popActionContext() {
        FunctionContext functionContext = functionContextStack.pop();
        functionContext.save();

        if (functionContextStack.size() > 0) {
            functionContext = functionContextStack.pop();
            pushActionContext(functionContext);
        }
        callback.setEnabled(functionContextStack.size() > 1);
    }

    public void dismissDialog() {
        if (dialog != null) dialog.dismiss();
    }

    @Override
    public void onDestroy() {
        try {
            binding.cardLayout.getFunctionContext().save();
            ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
            if (actionBar != null) actionBar.setSubtitle(null);
        } catch (Exception ignored) {
        }
        super.onDestroy();
    }


}
