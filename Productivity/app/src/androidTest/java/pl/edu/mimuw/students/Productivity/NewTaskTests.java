package pl.edu.mimuw.students.Productivity;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Objects;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static pl.edu.mimuw.students.Productivity.TestingUtilClass.addAndChooseCategory;
import static pl.edu.mimuw.students.Productivity.TestingUtilClass.checkTask;
import static pl.edu.mimuw.students.Productivity.TestingUtilClass.getPositionInRecyclerView;
import static pl.edu.mimuw.students.Productivity.TestingUtilClass.getTime;
import static pl.edu.mimuw.students.Productivity.TestingUtilClass.setNumberOfSessions;
import static pl.edu.mimuw.students.Productivity.TestingUtilClass.setTaskName;
import static pl.edu.mimuw.students.Productivity.TestingUtilClass.withRecyclerView;

@RunWith(AndroidJUnit4.class)
public class NewTaskTests {
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    // Tests adding new (standard) task with name, category and session number.
    @Test
    public void addNewTaskTest() {
        final String taskName = "task name " + getTime();
        final String categoryName = "category name " + getTime();
        final String noSessions = "10";

        addNewTask(taskName, categoryName, noSessions);
    }

    // Tests adding recurring task.
    @Test
    public void addNewRecurringTaskTest() {
        final String taskName = "task name " + getTime();
        final String monNoSessions = "5";

        // Enter NewTaskFragment.
        onView(withId(R.id.fab)).perform(click());

        // Setting name.
        setTaskName(taskName);

        // Set recurring.
        onView(withId(R.id.is_weekly_toggle)).perform(click());

        // Add sessions for monday.
        onView(withId(R.id.mon_session_no)).perform(typeText(monNoSessions));
        Espresso.closeSoftKeyboard();

        // Save new Task.
        onView(withId(R.id.button_save)).perform(ViewActions.scrollTo(), click());

        // Check added task.
        checkTask(activityTestRule, taskName, "recurring");
    }

    @Test
    public void editTaskNameTest() {
        final String taskName = "task name " + getTime();
        final String newTaskName = "new task name " + getTime();
        final String categoryName = "category name " + getTime();
        final String noSessions = "10";

        addNewTask(taskName, categoryName, noSessions);

        int i = getPositionInRecyclerView(activityTestRule, R.id.recyclerview, R.id.category_name, taskName);

        // Choose task to edit.
        onView(withRecyclerView(R.id.recyclerview).atPosition(i)).perform(longClick());

        // Enter newTaskFragment.
        onView(withId(R.id.newTaskFragment)).perform(click());

        // Clear task name.
        onView(withId(R.id.new_task_name)).perform(clearText());

        // Set new name.
        setTaskName(newTaskName);

        // Save.
        onView(withId(R.id.button_save)).perform(click());

        // Check task on the list.
        checkTask(activityTestRule, newTaskName, "0/" + noSessions);
    }

    @Test
    public void editNoSessionsTest() {
        final String taskName = "task name " + getTime();
        final String categoryName = "category name " + getTime();
        final String noSessions = "15";
        final String newNoSessions = "20";

        addNewTask(taskName, categoryName, noSessions);

        int i = getPositionInRecyclerView(activityTestRule, R.id.recyclerview, R.id.category_name, taskName);

        // Choose task to edit.
        onView(withRecyclerView(R.id.recyclerview).atPosition(i)).perform(longClick());

        // Enter newTaskFragment.
        onView(withId(R.id.newTaskFragment)).perform(click());

        // Clear noSessions.
        onView(withId(R.id.new_task_no_sessions)).perform(clearText());

        // Set new noSessions.
        setNumberOfSessions(newNoSessions);

        // Save.
        onView(withId(R.id.button_save)).perform(click());

        // Check task on the list.
        checkTask(activityTestRule, taskName, "0/" + newNoSessions);
    }

    public void deleteTask() {
        final String taskName = "task name " + getTime();
        final String categoryName = "category name " + getTime();
        final String noSessions = "13";

        addNewTask(taskName, categoryName, noSessions);

        RecyclerView recyclerView = activityTestRule.getActivity().findViewById(R.id.recyclerview);

        // Get number of tasks.
        int countAfter, count = Objects.requireNonNull(recyclerView.getAdapter()).getItemCount();
        int i = getPositionInRecyclerView(activityTestRule, R.id.recyclerview, R.id.category_name, taskName);

        // Choose task to delete.
        onView(withRecyclerView(R.id.recyclerview).atPosition(i)).perform(longClick());

        // Delete task.
        onView(withId(R.id.action_delete_task)).perform(click());

        // Get new number of tasks.
        countAfter = Objects.requireNonNull(recyclerView.getAdapter()).getItemCount();

        assert (countAfter == count - 1) : "delete task failed";
    }

    private void addNewTask(String taskName, String categoryName, String noSessions) {
        // Enter NewTaskFragment.
        onView(withId(R.id.fab)).perform(click());

        // Setting name and number of sessions.
        setTaskName(taskName);
        setNumberOfSessions(noSessions);

        // Enter ChooseCategoryFragment.
        onView(withId(R.id.choose_category_button)).perform(click());

        // Choosing category for task.
        addAndChooseCategory(activityTestRule, categoryName);

        // Save new Task.
        onView(withId(R.id.button_save)).perform(click());

        // Check added task.
        checkTask(activityTestRule, taskName, "0/" + noSessions);
    }
}
