package pl.edu.mimuw.students.Productivity;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static pl.edu.mimuw.students.Productivity.TestingUtilClass.addNewCategory;
import static pl.edu.mimuw.students.Productivity.TestingUtilClass.deleteCategory;
import static pl.edu.mimuw.students.Productivity.TestingUtilClass.editCategory;
import static pl.edu.mimuw.students.Productivity.TestingUtilClass.getTime;

@RunWith(AndroidJUnit4.class)
public class CategoryTests {
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    // Testing editing category name.
    @Test
    public void editCategoryTest() {
        final String categoryName = "category name " + getTime();
        final String categoryNameEdited = "category name edited " + getTime();

        // Enter NewTaskFragment.
        onView(withId(R.id.fab)).perform(click());

        // Enter ChooseCategoryFragment.
        onView(withId(R.id.choose_category_button)).perform(click());

        // Add category.
        addNewCategory(categoryName);

        // Edit category.
        editCategory(activityTestRule, categoryName, categoryNameEdited);
    }

    // Testing deleting category.
    @Test
    public void deleteCategoryTest() {
        final String categoryName = "category name " + getTime();

        // Enter NewTaskFragment.
        onView(withId(R.id.fab)).perform(click());

        // Enter ChooseCategoryFragment.
        onView(withId(R.id.choose_category_button)).perform(click());

        // Add category.
        addNewCategory(categoryName);

        // Delete category.
        deleteCategory(activityTestRule, categoryName);
    }
}
