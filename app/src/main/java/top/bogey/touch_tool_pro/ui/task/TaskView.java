package top.bogey.touch_tool_pro.ui.task;

import android.app.Activity;
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
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.function.Function;
import top.bogey.touch_tool_pro.bean.task.Task;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.bean.task.TaskRunningListener;
import top.bogey.touch_tool_pro.databinding.ViewTaskBinding;
import top.bogey.touch_tool_pro.save.SaveRepository;
import top.bogey.touch_tool_pro.save.TaskSaveChangedListener;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;
import top.bogey.touch_tool_pro.ui.MainActivity;
import top.bogey.touch_tool_pro.ui.custom.CreateFunctionContextDialogBuilder;
import top.bogey.touch_tool_pro.ui.setting.HandleFunctionContextView;
import top.bogey.touch_tool_pro.utils.AppUtils;

public class TaskView extends Fragment implements TaskSaveChangedListener, TaskRunningListener {
    final HashMap<String, Task> selectedTasks = new HashMap<>();
    private final MenuProvider menuProvider = new MenuProvider() {
        @Override
        public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
            menuInflater.inflate(R.menu.menu_task, menu);
        }

        @Override
        public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.importTask) {
                MainActivity mainActivity = MainApplication.getInstance().getMainActivity();
                mainActivity.launcherContent((code, intent) -> {
                    if (code == Activity.RESULT_OK) {
                        Uri uri = intent.getData();
                        if (uri != null) {
                            mainActivity.saveTasks(uri);
                        }
                    }
                });
                return true;
            }
            return false;
        }
    };
    boolean isSelect = false;
    private ViewTaskBinding binding;
    private TaskListRecyclerViewAdapter adapter;
    private final OnBackPressedCallback callback = new OnBackPressedCallback(false) {
        @Override
        public void handleOnBackPressed() {
            unSelectAll();
            hideBottomBar();
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service != null) service.removeListener(this);
        SaveRepository.getInstance().removeTaskListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ViewTaskBinding.inflate(inflater, container, false);

        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service != null) service.addListener(this);
        SaveRepository.getInstance().addTaskListener(this);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        requireActivity().addMenuProvider(menuProvider, getViewLifecycleOwner());

        adapter = new TaskListRecyclerViewAdapter(this);
        binding.tasksBox.setAdapter(adapter);
        reCalculateTags();

        new TabLayoutMediator(binding.tabBox, binding.tasksBox, (tab, position) -> tab.setText(adapter.getTagByIndex(position))).attach();

        binding.selectAllButton.setOnClickListener(v -> selectAll());

        binding.deleteButton.setOnClickListener(v -> AppUtils.showDialog(getContext(), R.string.delete_task_tips, result -> {
            if (result) {
                deleteSelectTasks();
                hideBottomBar();
            }
        }));

        binding.exportButton.setOnClickListener(v -> {
            exportSelectTasks();
            hideBottomBar();
        });

        binding.moveButton.setOnClickListener(v -> showTagView());

        binding.copyButton.setOnClickListener(v -> {
            selectedTasks.forEach((id, task) -> {
                Task copy = (Task) task.copy();
                if (copy == null) return;
                copy.newInfo();
                copy.setTitle(getString(R.string.task_copy_title, copy.getTitle()));
                copy.save();
            });

            unSelectAll();
            hideBottomBar();
        });

        binding.exchangeButton.setOnClickListener(v -> new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dialog_title)
                .setMessage(R.string.exchange_task_tips)
                .setPositiveButton(R.string.exchange_task_1, (dialog, which) -> {
                    dialog.dismiss();
                    SaveRepository repository = SaveRepository.getInstance();
                    selectedTasks.forEach((id, task) -> {
                        repository.saveFunction(new Function(task));
                        repository.removeTask(id);
                    });
                    hideBottomBar();
                })
                .setNeutralButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setNegativeButton(R.string.exchange_task_2, (dialog, which) -> {
                    SaveRepository repository = SaveRepository.getInstance();
                    selectedTasks.forEach((id, task) -> repository.saveFunction(new Function(task)));
                    unSelectAll();
                    hideBottomBar();
                })
                .show());

        binding.folderButton.setOnClickListener(v -> showTagView());

        binding.addButton.setOnClickListener(v -> {
            String tag = null;
            TabLayout.Tab tab = binding.tabBox.getTabAt(binding.tabBox.getSelectedTabPosition());
            if (tab != null && tab.getText() != null) {
                tag = tab.getText().toString();
                if (SaveRepository.NO_TAG.equals(tag)) tag = null;
            }
            CreateFunctionContextDialogBuilder dialog = new CreateFunctionContextDialogBuilder(requireContext(), SaveRepository.getInstance().getTaskTags(), tag);
            dialog.setTitle(R.string.task_add);
            dialog.setCallback(result -> {
                if (result && !dialog.getTitle().isEmpty()) {
                    Task task = new Task();
                    task.setTitle(dialog.getTitle());
                    task.addTags(dialog.getTags());
                    task.save();
                }
            });
            dialog.show();
        });

        return binding.getRoot();
    }

    public void reCalculateTags() {
        ArrayList<String> tags = SaveRepository.getInstance().getAllTaskTags();
        adapter.setTags(tags);
    }

    public void showBottomBar() {
        MainApplication.getInstance().getMainActivity().hideBottomNavigation();

        binding.addButton.hide();
        binding.bottomBar.setVisibility(View.VISIBLE);

        isSelect = true;
        callback.setEnabled(true);
    }

    public void hideBottomBar() {
        MainApplication.getInstance().getMainActivity().showBottomNavigation();

        binding.addButton.show();
        binding.bottomBar.setVisibility(View.GONE);

        isSelect = false;
        callback.setEnabled(false);
    }

    private void selectAll() {
        TabLayout.Tab tab = binding.tabBox.getTabAt(binding.tabBox.getSelectedTabPosition());
        if (tab == null || tab.getText() == null) return;

        String tag = tab.getText().toString();
        ArrayList<Task> tasks = SaveRepository.getInstance().getTasksByTag(tag);

        boolean flag = true;
        if (selectedTasks.size() == tasks.size()) {
            boolean matched = true;
            for (Task task : tasks) {
                if (!selectedTasks.containsKey(task.getId())) {
                    matched = false;
                    break;
                }
            }
            if (matched) {
                unSelectAll();
                flag = false;
            }
        }
        if (flag) {
            selectedTasks.clear();
            tasks.forEach(task -> selectedTasks.put(task.getId(), task));
            adapter.notifyItemRangeChanged(0, adapter.getItemCount());
        }
    }

    public void unSelectAll() {
        selectedTasks.clear();
        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
    }

    private void deleteSelectTasks() {
        selectedTasks.forEach((id, task) -> SaveRepository.getInstance().removeTask(id));
        unSelectAll();
    }

    private void showTagView() {
        TaskTagView taskTagView = new TaskTagView(this);
        taskTagView.show(requireActivity().getSupportFragmentManager(), null);
    }

    private void exportSelectTasks() {
        if (selectedTasks.size() == 0) return;
        HandleFunctionContextView view = new HandleFunctionContextView(requireContext(), new HashMap<>(selectedTasks), Task.class);
        if (view.isEmpty()) return;
        view.switchState(true);

        new MaterialAlertDialogBuilder(requireContext())
                .setPositiveButton(R.string.enter, (dialog, which) -> {
                    AppUtils.exportFunctionContexts(requireContext(), view.getSelectFunctionContext());
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setView(view)
                .setTitle(R.string.task_setting_export)
                .show();

        unSelectAll();
    }

    private void callMethod(String methodName, Task task) {
        for (int i = 0; i < binding.tasksBox.getChildCount(); i++) {
            View child = binding.tasksBox.getChildAt(i);
            if (child instanceof RecyclerView recyclerView) {
                for (int j = 0; j < recyclerView.getChildCount(); j++) {
                    View subChild = recyclerView.getChildAt(j);
                    if (subChild instanceof RecyclerView subRecyclerView) {
                        TaskRecyclerViewAdapter subAdapter = (TaskRecyclerViewAdapter) subRecyclerView.getAdapter();
                        if (subAdapter != null) {
                            try {
                                Method method = TaskRecyclerViewAdapter.class.getMethod(methodName, Task.class);
                                method.invoke(subAdapter, task);
                            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onStart(TaskRunnable runnable) {
        binding.getRoot().post(() -> callMethod("onChanged", runnable.getTask()));
    }

    @Override
    public void onEnd(TaskRunnable runnable) {
        onStart(runnable);
    }

    @Override
    public void onProgress(TaskRunnable runnable, Action action, int progress, boolean execute) {

    }

    @Override
    public void onCreated(Task value) {
        reCalculateTags();
    }

    @Override
    public void onChanged(Task value) {
        reCalculateTags();
    }

    @Override
    public void onRemoved(Task value) {
        reCalculateTags();
    }


}
