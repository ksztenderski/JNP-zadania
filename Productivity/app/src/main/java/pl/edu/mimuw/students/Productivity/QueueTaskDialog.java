package pl.edu.mimuw.students.Productivity;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import pl.edu.mimuw.students.Productivity.Database.Task.Task;
import pl.edu.mimuw.students.Productivity.Database.Task.TaskQueued;
import pl.edu.mimuw.students.Productivity.Database.Task.TaskViewModel;

public class QueueTaskDialog extends DialogFragment {

    private List<Task> tasks;
    private TaskViewModel model;
    private LifecycleOwner owner;
    private AtomicBoolean atomicBoolean;

    QueueTaskDialog(List<Task> tasks, TaskViewModel taskViewModel, LifecycleOwner owner) {
        this.tasks = tasks;
        this.model = taskViewModel;
        this.owner = owner;
        this.atomicBoolean = new AtomicBoolean();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        LinkedList<String> taskList = new LinkedList<String>();
        for (Task t: tasks) {
            taskList.add(t.getName());
        }
        Integer visited = 0;
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(R.string.add_task_to_queue)
               .setItems(taskList.toArray(new String[0]), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
                       model.getQueueLastPosition().observe(owner, pos -> {
                           if (atomicBoolean.compareAndSet(false, true)) {
                               model.insertTaskQueued(new TaskQueued(tasks.get(which).getId(), pos + 1));
                           }
                       });
                   }
               });
        return builder.create();
    }
}