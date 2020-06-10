package pl.edu.mimuw.students.Productivity.Database.Task;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "task_table")
public class Task {
    @Ignore
    public static final int MAX_SESSIONS = 100000;
    @PrimaryKey(autoGenerate =  true)
    private Integer id;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "category_id")
    private Integer categoryId;

    @ColumnInfo(name = "sessions_left")
    private int sessionsLeft;

    @ColumnInfo(name = "sessions_done")
    private int sessionsDone;

    @ColumnInfo(name = "is_recurring")
    private boolean isRecurring;

    public Task(@NonNull String name, int sessionsLeft, boolean isRecurring) {
        this.name = name;
        this.categoryId = null;
        this.isRecurring = isRecurring;
        if (!isRecurring) {
            this.sessionsLeft = sessionsLeft;
        }
        this.sessionsDone = 0;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public int getSessionsLeft() {
        return sessionsLeft;
    }

    public int getSessionsDone() {
        return sessionsDone;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public void setSessionsDone(int sessionsDone) {
        this.sessionsDone = sessionsDone;
    }

    public void setSessionLeft(int sessionLeft) {
        this.sessionsLeft = sessionLeft;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }
}
