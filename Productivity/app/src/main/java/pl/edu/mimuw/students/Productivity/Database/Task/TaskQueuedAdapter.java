package pl.edu.mimuw.students.Productivity.Database.Task;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import pl.edu.mimuw.students.Productivity.R;


public class TaskQueuedAdapter extends RecyclerView.Adapter<TaskQueuedAdapter.MyViewHolder> {
    private List<TaskQueued> list;
    private TaskViewModel model;
    private LifecycleOwner owner;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private MyViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.queue);

        }
    }

    public TaskQueuedAdapter(TaskViewModel model, LifecycleOwner owner) {
        this.model = model;
        this.owner = owner;
    }


    @NonNull
    @Override
    public TaskQueuedAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_queue, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskQueuedAdapter.MyViewHolder holder, int position) {
        model.getTaskById(list.get(position).getTaskId()).observe(owner, task -> {
                holder.textView.setText(task.getName());
        });
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public void setList(List<TaskQueued> taskQueueds) {
            this.list = taskQueueds;
            notifyDataSetChanged();
    }


    public void swapTask(TaskViewModel model, int holder, int target) {
        TaskQueued holderTask = this.list.get(holder);
        TaskQueued targetTask = this.list.get(target);
        Integer holderTaskPosition = holderTask.getPosition();
        Integer targetTaskPosition = targetTask.getPosition();
        targetTask.setPosition(holderTaskPosition);
        holderTask.setPosition(targetTaskPosition);
        model.updateTaskQueued(holderTask);
        model.updateTaskQueued(targetTask);
        Collections.swap(this.list, holder, target);
    }
}