package pl.edu.mimuw.students.Productivity.Database.Task;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import pl.edu.mimuw.students.Productivity.Database.Category.Category;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Task.class, RecurringTask.class, Category.class, TaskQueued.class}, version = 2, exportSchema = false)
public abstract class TaskRoomDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();

    private static volatile TaskRoomDatabase INSTANCE;
    private static volatile TaskRoomDatabase TEST_INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static TaskRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TaskRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            TaskRoomDatabase.class, "task_database").build();
                }
            }
        }
        return INSTANCE;
    }

    public static TaskRoomDatabase getTestDatabase(final Context context) {
        if (TEST_INSTANCE == null) {
            synchronized (TaskRoomDatabase.class) {
                if (TEST_INSTANCE == null) {
                    TEST_INSTANCE = Room.inMemoryDatabaseBuilder(context.getApplicationContext(),
                            TaskRoomDatabase.class).build();
                }
            }
        }
        return TEST_INSTANCE;
    }

    private static RoomDatabase.Callback roomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

//            // If you want to keep data through app restarts,
//            // comment out the following block
//            databaseWriteExecutor.execute(() -> {
//                // Populate the database in the background.
//                // If you want to start with more words, just add them.
//                TaskDao dao = INSTANCE.wordDao();
//                dao.deleteAll();
//
//                Task task = new Task("Hello");
//                dao.insert(task);
//                task = new Task("World");
//                dao.insert(task);
//            });
        }
    };
}
