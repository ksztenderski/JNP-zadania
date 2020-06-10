package pl.edu.mimuw.students.Productivity.Database.Task;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {
    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Task task);

    @Query("DELETE FROM task_table")
    void deleteAll();

    @Update
    void update(Task task);

    @Query("SELECT * from task_table WHERE sessions_left > 0 OR is_recurring = 1 ORDER BY name ASC")
    LiveData<List<Task>> getAlphabetizedTasks();


    @Query("SELECT max(id) as id, name, max(category_id) as category_id, sum(sessions_left) as sessions_left, " +
            "sum(sessions_done) as sessions_done, max(is_recurring) as is_recurring from task_table group by name ORDER BY name ASC")
    LiveData<List<Task>> getStatTasks();


    @Query("SELECT tt.* FROM task_table tt LEFT JOIN task_queued tq ON tt.id = tq.taskid WHERE NOT tt.is_recurring " +
            "GROUP BY tt.id HAVING count(tq.taskid) < tt.sessions_left")
    LiveData<List<Task>> getAvailableTasks();

    @Query("SELECT * FROM task_table WHERE id = :id")
    LiveData<Task> getTaskById(long id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertRecurring(RecurringTask task);

    @Query("DELETE FROM recurring_task WHERE id = :id")
    void deleteRecurring(long id);

    @Query("DELETE FROM task_table WHERE id = :id")
    void deleteTask(long id);

    @Update
    void updateRecurring(RecurringTask task);

    @Query("SELECT * FROM recurring_task WHERE id = :taskId")
    LiveData<List<RecurringTask>> getTaskSchedule(long taskId);


    @Query("SELECT * FROM recurring_task")
    List<RecurringTask> getRecDebug();

    @Query("SELECT * from task_table")
    List<Task> getTasksDebug();

    @Query("DELETE FROM recurring_task")
    void deleteAllRecurring();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertTaskQueued(TaskQueued taskQueued);

    @Query("SELECT coalesce(max(position), 0) FROM task_queued")
    LiveData<Integer> getQueueLastPosition();

    @Query("SELECT coalesce(sum(sessions_done), 0) FROM task_table")
    LiveData<Integer> getSessionDone();

    @Query("SELECT coalesce(sum(sessions_left), 0) FROM task_table")
    LiveData<Integer> getSessionLeft();

    @Update
    void updateTaskQueued(TaskQueued taskQueued);

    @Query("DELETE FROM task_queued WHERE id = :id")
    void deleteTaskQueued(long id);

    @Query("DELETE FROM task_queued")
    void deleteAllTaskQueued();


    @Query("SELECT * from task_queued ORDER BY position ASC")
    LiveData<List<TaskQueued>> getQueue();


    @Query("UPDATE task_queued SET position = position - 1 WHERE position > 1")
    void decreasePos();

    @Query("DELETE FROM task_queued WHERE position = (SELECT min(position) FROM task_queued)")
    void popFirstQueuedTask();

    @Query("SELECT tt.* FROM task_queued tq LEFT JOIN task_table tt ON tt.id = tq.taskid ORDER BY position ASC")
    LiveData<List<Task>> getQueuedTask();

}
