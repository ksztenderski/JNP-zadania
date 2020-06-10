package pl.edu.mimuw.students.Productivity;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Date;
import java.util.Objects;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

public class TestingUtilClass {
    // Convenience helper
    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }

    public static long getTime() {
        return new Date().getTime();
    }

    public static int getPositionInRecyclerView(ActivityTestRule<MainActivity> activityTestRule, int viewId, int elementId, String name) {
        // Finding category.
        RecyclerView recyclerView = activityTestRule.getActivity().findViewById(viewId);

        int i, count = Objects.requireNonNull(recyclerView.getAdapter()).getItemCount();

        for (i = 0; i < count; i++) {
            try {
                onView(withRecyclerView(viewId).atPositionOnView(i, elementId)).check(matches(withText(name)));
                break;
            } catch (AssertionError ignored) {
            }
        }

        assert (i < count) : "Element <" + name + "> not found in RecyclerView.";

        return i;
    }

    public static void setTaskName(String taskName) {
        onView(withId(R.id.new_task_name)).perform(typeText(taskName));
        Espresso.closeSoftKeyboard();
    }

    public static void setNumberOfSessions(String noSessions) {
        onView(withId(R.id.new_task_no_sessions)).perform(typeText(noSessions));
        Espresso.closeSoftKeyboard();
    }

    public static void addAndChooseCategory(ActivityTestRule<MainActivity> activityTestRule, String categoryName) {
        // Adding new category.
        onView(allOf(ViewMatchers.withId(R.id.new_category_name), isDisplayed())).perform(typeText(categoryName));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.button_add_new_category)).perform(click());

        // Choosing (clicking) newly added category.
        int i = getPositionInRecyclerView(activityTestRule, R.id.recyclerview_choose_category, R.id.category_name, categoryName);
        onView((withId(R.id.recyclerview_choose_category))).perform(actionOnItemAtPosition(i, click()));

        // Check if displayed category name is correct.
        onView(withId(R.id.choose_category_button)).check(matches(withText(categoryName)));
    }

    public static void checkTask(ActivityTestRule<MainActivity> activityTestRule, String taskName, String taskInfo) {
        // Checking newly added task in list of tasks.
        int i = getPositionInRecyclerView(activityTestRule, R.id.recyclerview, R.id.category_name, taskName);
        onView(withRecyclerView(R.id.recyclerview).atPosition(i)).check(matches(hasDescendant(withText(taskInfo))));
    }

    public static void addNewCategory(String categoryName) {
        // Adding new category.
        onView(allOf(ViewMatchers.withId(R.id.new_category_name), isDisplayed())).perform(typeText(categoryName));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.button_add_new_category)).perform(click());
    }

    public static void editCategory(ActivityTestRule<MainActivity> activityTestRule, String categoryName, String categoryNameEdited) {
        int i = getPositionInRecyclerView(activityTestRule, R.id.recyclerview_choose_category, R.id.category_name, categoryName);

        // Edit category name.
        onView(withRecyclerView(R.id.recyclerview_choose_category).atPosition(i)).perform(longClick());
        onView(withRecyclerView(R.id.recyclerview_choose_category).atPositionOnView(i, R.id.edit_category_name)).perform(clearText(), typeText(categoryNameEdited));
        onView(withRecyclerView(R.id.recyclerview_choose_category).atPositionOnView(i, R.id.save_category_button)).perform(click());

        i = getPositionInRecyclerView(activityTestRule, R.id.recyclerview_choose_category, R.id.category_name, categoryNameEdited);

        // Choose category.
        onView((withId(R.id.recyclerview_choose_category))).perform(actionOnItemAtPosition(i, click()));

        // Check if displayed category name is correct.
        onView(withId(R.id.choose_category_button)).check(matches(withText(categoryNameEdited)));
    }

    public static void deleteCategory(ActivityTestRule<MainActivity> activityTestRule, String categoryName) {
        RecyclerView recyclerView = activityTestRule.getActivity().findViewById(R.id.recyclerview_choose_category);
        int countAfter, count = Objects.requireNonNull(recyclerView.getAdapter()).getItemCount();
        int i = getPositionInRecyclerView(activityTestRule, R.id.recyclerview_choose_category, R.id.category_name, categoryName);

        onView(withRecyclerView(R.id.recyclerview_choose_category).atPosition(i)).perform(longClick());
        onView(withRecyclerView(R.id.recyclerview_choose_category).atPositionOnView(i, R.id.delete_category_button)).perform(click());
        countAfter = Objects.requireNonNull(recyclerView.getAdapter()).getItemCount();

        assert (countAfter == count - 1) : "delete category failed";
    }

    public static void addTaskToQueue() {
        DataInteraction appCompatTextView = onData(anything())
                .inAdapterView(allOf(withId(R.id.select_dialog_listview),
                        childAtPosition(withId(R.id.contentPanel), 1)))
                .atPosition(0);
        appCompatTextView.perform(click());
    }

    public static void prepareTasksToAddToQueue(ActivityTestRule<MainActivity> activityTestRule, String noSessions) {
        final String taskName = "task name " + getTime();

        // Enter NewTaskFragment.
        onView(withId(R.id.fab)).perform(click());

        // Setting name and number of sessions.
        setTaskName(taskName);
        setNumberOfSessions(noSessions);

        // Save new Task.
        onView(withId(R.id.button_save)).perform(click());

        // Check added task.
        checkTask(activityTestRule, taskName, "0/" + noSessions);
    }

    static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
