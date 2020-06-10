package pl.edu.mimuw.students.Productivity.Database.Task;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {
    private TaskRepository repository;

    private LiveData<List<Task>> allTasks;
    private MutableLiveData<Boolean> bottomNavDisabled;

    public TaskViewModel(Application application) {
        super(application);
        repository = new TaskRepository(application);
        allTasks = repository.getAllTasks();
        bottomNavDisabled = new MutableLiveData<>(false);
        bottomNavDisabled.setValue(false);
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public LiveData<List<Task>> getStatTasks() { return repository.getStatTask(); }

    public void insert(Task task) {
        repository.insert(task);
    }

    public void insertRecurring(Task task, RecurringTask recurring) {
        repository.insertRecurring(task, recurring);
    }

    public void update(Task task) { repository.update(task); }

    public LiveData<Task> getTaskById(long id) {
        return repository.getTaskById(id);
    }

    public LiveData<List<Task>> getAvailableTask() {
        return repository.getAvailableTasks();
    }

    public void updateRecurring(RecurringTask rec) { repository.updateRecurring(rec); }

    public void insertRecurrence(RecurringTask rec) { repository.insertRecurrence(rec); }

    public LiveData<List<RecurringTask>> getTaskSchedule(long taskId) { return repository.getTaskSchedule(taskId); }

    public void delete(Task task) {
        repository.delete(task);
    }

    public void deleteRecurring(int id) { repository.deleteRecurring(id); }

    public void insertTaskQueued(TaskQueued taskQueued) {
        repository.insertTaskQueued(taskQueued);
    }

    public LiveData<Integer> getQueueLastPosition() {
        return repository.getQueueLastPosition();
    }

    public void updateTaskQueued(TaskQueued taskQueued) {
        repository.updateTaskQueued(taskQueued);
    }

    public void deleteTaskQueued(TaskQueued taskQueued) {
        repository.deleteTaskQueued(taskQueued);
    }

    public void deleteAllTaskQueued() {
        repository.deleteAllTaskQueued();
    }

    public LiveData<List<TaskQueued>> getQueue() {
        return repository.getQueue();
    }

    public void decreasePos() {
        repository.decreasePos();
    }

    public LiveData<Integer> getSessionLeft() {
        return repository.getSessionLeft();
    }

    public LiveData<Integer> getSessionDone() {
        return repository.getSessionDone();
    }


    public void popFirstQueuedTask() {
        repository.popFirstQueuedTask();
    }

    public LiveData<List<Task>> getQueuedTask() {
        LiveData<List<Task>> result = repository.getQueuedTask();;
        return result;
    }

    public void disableBottomNav() {
        bottomNavDisabled.setValue(true);
    }

    public void enableBottomNav() {
        bottomNavDisabled.setValue(false);
    }

    public LiveData<Boolean> getBottomNavState() {
        return bottomNavDisabled;
    }

}
