package pl.edu.mimuw.students.Productivity.Database.Task;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import pl.edu.mimuw.students.Productivity.Database.Task.Task;

/*
@Entity(tableName = "task_queued", foreignKeys = @ForeignKey(entity = Task.class,
                                                            parentColumns = "id",
                                                            childColumns = "taskid"))*/

@Entity(tableName = "task_queued", foreignKeys = @ForeignKey(entity = Task.class,
        parentColumns = "id",
        childColumns = "taskid",
        onDelete = ForeignKey.CASCADE))
public class TaskQueued {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @NonNull
    @ColumnInfo(name = "position")
    private Integer position;

    @NonNull
    @ColumnInfo(name = "taskid")
    private Integer taskId;

    public TaskQueued(int taskId, int position) {
        this.position = position;
        this.taskId = taskId;
    }

    void setPosition(@NonNull int newPosition) {
        this.position = newPosition;
    }

    void setId(@NonNull Integer id) {
        this.id = id;
    }

    void setTaskId(@NonNull Integer id) {
        this.id = id;
    }

    Integer getPosition() {
        return this.position;
    }

    Integer getTaskId() {
        return this.taskId;
    }

    Integer getId() { return this.id; }



}