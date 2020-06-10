package pl.edu.mimuw.students.Productivity.Database.Task;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "recurring_task", foreignKeys = @ForeignKey(entity = Task.class, parentColumns = "id", childColumns = "id", onDelete = ForeignKey.CASCADE))
public class RecurringTask {
    @PrimaryKey
    @NonNull
    private long id;

    @ColumnInfo(name = "mon_tasks")
    private int monTasks;

    @ColumnInfo(name = "tue_tasks")
    private int tueTasks;

    @ColumnInfo(name = "wed_tasks")
    private int wedTasks;

    @ColumnInfo(name = "thu_tasks")
    private int thuTasks;

    @ColumnInfo(name = "fri_tasks")
    private int friTasks;

    @ColumnInfo(name = "sat_tasks")
    private int satTasks;

    @ColumnInfo(name = "sun_tasks")
    private int sunTasks;

    public RecurringTask(int monTasks, int tueTasks, int wedTasks, int thuTasks, int friTasks, int satTasks, int sunTasks) {
        this.id = 0;
        this.monTasks = monTasks;
        this.tueTasks = tueTasks;
        this.wedTasks = wedTasks;
        this.thuTasks = thuTasks;
        this.friTasks = friTasks;
        this.satTasks = satTasks;
        this.sunTasks = sunTasks;
    }

    public void setId(@NonNull long id) {
        this.id = id;
    }

    @NonNull
    public long getId() {
        return id;
    }

    public int getMonTasks() {
        return monTasks;
    }

    public int getTueTasks() {
        return tueTasks;
    }

    public int getWedTasks() {
        return wedTasks;
    }

    public int getThuTasks() {
        return thuTasks;
    }

    public int getFriTasks() {
        return friTasks;
    }

    public int getSatTasks() {
        return satTasks;
    }

    public int getSunTasks() {
        return sunTasks;
    }
}
