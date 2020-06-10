package pl.edu.mimuw.students.Productivity.Database.Category;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class CategoryViewModel extends AndroidViewModel {
    private CategoryRepository repository;

    private LiveData<List<Category>> allCategories;

    public CategoryViewModel(Application application) {
        super(application);
        repository = new CategoryRepository(application);
        allCategories = repository.getAllCategories();
    }

    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public void insert(Category category) {
        repository.insert(category);
    }

    public void update(Category category) {
        repository.update(category);
    }

    public LiveData<Category> getCategory(Integer id) {
        return repository.getCategory(id);
    }

    public void deleteCategory(Integer id) {
        repository.deleteCategory(id);
    }
}
