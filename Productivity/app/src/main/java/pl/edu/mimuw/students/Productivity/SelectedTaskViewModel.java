package pl.edu.mimuw.students.Productivity;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import pl.edu.mimuw.students.Productivity.Database.Category.Category;
import pl.edu.mimuw.students.Productivity.Database.Task.Task;

public class SelectedTaskViewModel extends AndroidViewModel {

    MutableLiveData<Task> selectedTask = new MutableLiveData<>();
    MutableLiveData<Category> selectedCategory = new MutableLiveData<>();

    public SelectedTaskViewModel(@NonNull Application application) {
        super(application);
        selectedTask.setValue(null);
        selectedCategory.setValue(null);
    }

    public void selectTask(Task task) {
        selectedTask.setValue(task);
    }

    public LiveData<Task> getSelectedTask() {
        return selectedTask;
    }

    public void setCategory(Category category) {
        selectedCategory.setValue(category);
        if (selectedTask.getValue() != null) {
            selectedTask.getValue().setCategoryId(category.getId());
        }
    }
}
