package pl.edu.mimuw.students.Productivity.Database.Category;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert
    long insert(Category category);

    @Update
    void update(Category category);

    @Query("DELETE FROM category WHERE id = :id")
    void deleteCategory(Integer id);

    @Query("Select * FROM category WHERE id = :id")
    LiveData<Category> getCategory(Integer id);

    @Query("SELECT * FROM category ORDER BY name")
    LiveData<List<Category>> getCategories();
}
