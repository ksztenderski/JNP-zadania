package pl.edu.mimuw.students.Productivity.Database.Category;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class CategoryRepository {
    private CategoryDao categoryDao;
    private LiveData<List<Category>> allCategories;

    CategoryRepository(Application application) {
        CategoryRoomDatabase db = CategoryRoomDatabase.getDatabase(application);
        categoryDao = db.categoryDao();
        allCategories = categoryDao.getCategories();
    }

    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    void insert(Category category) {
        CategoryRoomDatabase.databaseWriteExecutor.execute(() -> categoryDao.insert(category));
    }

    void update(Category category) {
        CategoryRoomDatabase.databaseWriteExecutor.execute(() -> categoryDao.update(category));
    }

    LiveData<Category> getCategory(Integer id) {
        return categoryDao.getCategory(id);
    }

    void deleteCategory(Integer id) {
        CategoryRoomDatabase.databaseWriteExecutor.execute(() -> categoryDao.deleteCategory(id));
    }
}
