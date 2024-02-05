package top.bogey.touch_tool_pro.ui.setting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.databinding.FloatLogItemBinding;
import top.bogey.touch_tool_pro.ui.blueprint.BlueprintView;

public class LogRecyclerViewAdapter extends RecyclerView.Adapter<LogRecyclerViewAdapter.ViewHolder> {
    private final ArrayList<RuntimeLogInfo> showLogs = new ArrayList<>();
    private String taskId;
    private RecyclerView recyclerView;

    public LogRecyclerViewAdapter() {

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(FloatLogItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.refreshItem(showLogs.get(position));
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
        recyclerView.scrollToPosition(getItemCount() - 1);
    }

    @Override
    public int getItemCount() {
        return showLogs.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addLogs(String taskId, ArrayList<RuntimeLogInfo> logs) {
        this.taskId = taskId;
        showLogs.clear();
        showLogs.addAll(logs);
        notifyDataSetChanged();
        if (recyclerView != null) recyclerView.scrollToPosition(showLogs.size() - 1);
    }

    public void addLog(String taskId, RuntimeLogInfo log) {
        if (!taskId.equals(this.taskId)) return;
        showLogs.add(log);
        notifyItemInserted(showLogs.size());
        if (recyclerView != null) recyclerView.scrollToPosition(showLogs.size() - 1);
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        public final FloatLogItemBinding binding;
        private final Context context;

        public ViewHolder(@NonNull FloatLogItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            context = binding.getRoot().getContext();

            binding.showButton.setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                RuntimeLogInfo logInfo = showLogs.get(index);
                BlueprintView.tryShowCard(logInfo.getActionId(), logInfo.getValues());
            });
        }

        @SuppressLint("DefaultLocale")
        public void refreshItem(RuntimeLogInfo log) {
            binding.titleText.setText(log.getLogString());
        }
    }
}