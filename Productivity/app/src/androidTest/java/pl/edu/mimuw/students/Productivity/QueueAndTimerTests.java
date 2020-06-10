package pl.edu.mimuw.students.Productivity;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static pl.edu.mimuw.students.Productivity.TestingUtilClass.addTaskToQueue;
import static pl.edu.mimuw.students.Productivity.TestingUtilClass.prepareTasksToAddToQueue;

@RunWith(AndroidJUnit4.class)
public class QueueAndTimerTests {
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    // Testing adding single task to queue.
    @Test
    public void addSingleTaskToQueueTest() {
        prepareTasksToAddToQueue(activityTestRule, "10");

        // Enter QueueFragment.
        onView(withId(R.id.queueFragment)).perform(click());

        // Open adding tasks list.
        onView(withId(R.id.fab_add_queue)).perform(click());

        // Add some task to queue.
        addTaskToQueue();
    }

    // Testing adding multiple tasks to queue.
    @Test
    public void addMultipleTasksToQueueTest() {
        prepareTasksToAddToQueue(activityTestRule, "10");

        // Enter QueueFragment.
        onView(withId(R.id.queueFragment)).perform(click());

        // Add tasks to queue.
        for (int i = 0; i < 10; i++) {
            // Open adding tasks list.
            onView(withId(R.id.fab_add_queue)).perform(click());

            addTaskToQueue();
        }
    }

    @Test
    public void oneTimerSessionTest() {
        addSingleTaskToQueueTest();

        // Enter TimerFragment.
        onView(withId(R.id.timerFragment)).perform(click());

        // Check if empty queue.
        onView(withId(R.id.timer_description)).check(matches(not(withText(R.string.empty_queue))));

        // Start timer.
        onView(withId(R.id.start_task_button)).perform(click());

        // Check timer description.
        onView(withId(R.id.timer_description)).check(matches(not(withText(R.string.empty_queue))));

        // Finish session.
        onView(withId(R.id.finish_task_button)).perform(click());

        // Check timer description.
        onView(withId(R.id.timer_description)).check(matches(withText(R.string.description_break)));

        // Cancel break.
        onView(withId(R.id.cancel_button)).perform(click());
    }

    @Test
    public void multipleTimerSessionsTest() {
        addMultipleTasksToQueueTest();

        // Enter TimerFragment.
        onView(withId(R.id.timerFragment)).perform(click());

        for (int i = 0; i < 5; i++) {
            // Start timer.
            onView(withId(R.id.start_task_button)).perform(click());

            // Check timer description.
            onView(withId(R.id.timer_description)).check(matches(not(withText(R.string.empty_queue))));

            // Finish session.
            onView(withId(R.id.finish_task_button)).perform(click());

            // Check timer description.
            onView(withId(R.id.timer_description)).check(matches(withText(R.string.description_break)));
        }
    }
}
