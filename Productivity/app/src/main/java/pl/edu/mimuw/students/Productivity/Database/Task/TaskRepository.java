package pl.edu.mimuw.students.Productivity.Database.Task;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

class TaskRepository {
    private TaskDao taskDao;
    private LiveData<List<Task>> allTasks;

    TaskRepository(Application application) {
        TaskRoomDatabase db = TaskRoomDatabase.getDatabase(application);
        taskDao = db.taskDao();
        allTasks = taskDao.getAlphabetizedTasks();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    LiveData<List<Task>> getStatTask() {
        return taskDao.getStatTasks();
    }

    private void insertRec(Task task, RecurringTask recurring) {
        long ret = taskDao.insert(task);
        recurring.setId(ret);
        taskDao.insertRecurring(recurring);
    }

    void insertRecurring(Task task, RecurringTask recurring) {
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> insertRec(task, recurring));
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    void insert(Task task) {
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> taskDao.insert(task));
    }

    void update(Task task) {
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> taskDao.update(task));
    }

    LiveData<Task> getTaskById(long id) {
        return taskDao.getTaskById(id);
    }

    LiveData<List<Task>> getAvailableTasks() {
        return taskDao.getAvailableTasks();
    }

    void insertRecurrence(RecurringTask rec) {
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> taskDao.insertRecurring(rec));
    }

    void updateRecurring(RecurringTask rec) {
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> taskDao.updateRecurring(rec));
    }

    void delete(Task task) {
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> {
                if(task.isRecurring()) {
                    taskDao.deleteRecurring(task.getId());
                }
                taskDao.deleteTask(task.getId());
                taskDao.deleteTaskQueued(task.getId());
        });
    }

    void deleteRecurring(int id) {
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> taskDao.deleteRecurring(id));
    }

    LiveData<List<RecurringTask>> getTaskSchedule(long taskId) {
        return taskDao.getTaskSchedule(taskId);
    }

    LiveData<Integer> getSessionDone() {
        return taskDao.getSessionDone();
    }

    LiveData<Integer> getSessionLeft() {
        return taskDao.getSessionLeft();
    }

    List<Task> getTasksDebug() {
        return taskDao.getTasksDebug();
    }

    List<RecurringTask> getRecDebug() {
        return taskDao.getRecDebug();
    }

    void insertTaskQueued(TaskQueued queuedTask) {
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> { taskDao.insertTaskQueued(queuedTask); });
    }

    LiveData<Integer> getQueueLastPosition() {
        return taskDao.getQueueLastPosition();
    }

    void updateTaskQueued(TaskQueued taskQueued) {
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> taskDao.updateTaskQueued(taskQueued));
    }

    void deleteTaskQueued(TaskQueued taskQueued) {
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> taskDao.deleteTaskQueued(taskQueued.getId()));
    }

    void deleteAllTaskQueued() {
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> taskDao.deleteAllTaskQueued());
    };

    LiveData<List<TaskQueued>> getQueue() {
        return taskDao.getQueue();
    }

    void decreasePos() {
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> { taskDao.decreasePos(); });
    }

    void popFirstQueuedTask() {
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> { taskDao.popFirstQueuedTask(); });
    }

    LiveData<List<Task>> getQueuedTask() {
        return taskDao.getQueuedTask();
    }


}
