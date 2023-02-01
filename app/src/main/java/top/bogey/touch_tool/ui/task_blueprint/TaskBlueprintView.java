package top.bogey.touch_tool.ui.task_blueprint;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.databinding.ViewTaskBlueprintBinding;

public class TaskBlueprintView extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewTaskBlueprintBinding binding = ViewTaskBlueprintBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            String taskId = getArguments().getString("taskId");
            Task task = TaskRepository.getInstance().getTaskById(taskId);
            binding.cardLayout.setTask(task);
        }

        binding.addButton.setOnClickListener(v -> {
            ActionSideSheetDialog dialog = new ActionSideSheetDialog(requireContext(), binding.cardLayout);
            dialog.show();
        });

        return binding.getRoot();
    }
}