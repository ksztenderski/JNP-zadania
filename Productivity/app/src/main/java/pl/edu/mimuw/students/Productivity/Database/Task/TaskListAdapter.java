package pl.edu.mimuw.students.Productivity.Database.Task;


import androidx.recyclerview.widget.RecyclerView;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;

import pl.edu.mimuw.students.Productivity.R;
import pl.edu.mimuw.students.Productivity.TaskEditor;

import java.util.List;


public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {
    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView taskNameView;
        private final TextView taskNumberView;
        private final LinearLayout layout;

        private TaskViewHolder(View itemView) {
            super(itemView);
            taskNameView = itemView.findViewById(R.id.category_name);
            taskNumberView = itemView.findViewById(R.id.task_numbers);
            layout = itemView.findViewById(R.id.task_layout);
        }
    }

    private final LayoutInflater inflater;
    private TaskEditor onLongClickListener;
    private List<Task> tasks; // Cached copy of tasks.


    public TaskListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setOnLongClickListener(TaskEditor onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }
    @Override
    @NonNull
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_main_activity, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        if (tasks != null) {
            Task current = tasks.get(position);
            holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(onLongClickListener != null) {
                        onLongClickListener.taskEdit(current);
                    }
                    return true;
                }
            });
            holder.taskNameView.setText(current.getName());
            if (!current.isRecurring()) {
                holder.taskNumberView.setText(current.getSessionsDone() + "/" + (current.getSessionsLeft() + current.getSessionsDone()));
            } else {
                //TODO add sensible printing of recurring tasks
                holder.taskNumberView.setText(R.string.recurring_message);
            }
        } else {
            // Covers the case of data not being ready yet.
            holder.taskNameView.setText(R.string.task_not_ready_error);
        }
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (tasks != null) {
            return tasks.size();
        } else return 0;
    }
}
