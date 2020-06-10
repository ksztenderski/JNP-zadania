package pl.edu.mimuw.students.Productivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import pl.edu.mimuw.students.Productivity.Database.Task.Task;
import pl.edu.mimuw.students.Productivity.Database.Task.TaskViewModel;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;

public class TimerFragment extends Fragment {
    enum TimerState {
        startNextTask, taskCountdown, breakCountdown
    }

    private TaskViewModel taskViewModel;
    private OnBackPressedCallback callback;

    private boolean hideOptions = false;

    private long sessionLength;
    private long shortBreakLength;
    private long longBreakLength;
    private int sessionsPerCircle;
    private long secondsRemaining = 0;
    private int sessionsCompleted = 0;
    private CountDownTimer timer;
    private TextView descriptionTextView;
    private TextView countdownTextView;
    private ProgressBar progressBar;
    private FloatingActionButton startTaskButton;
    private FloatingActionButton finishTaskButton;
    private TimerState timerState;
    private Task task;
    private List<Task> tasksQueue;

    public TimerFragment() {
        // Required empty public constructor
    }

    public static TimerFragment newInstance() {
        return new TimerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_timer, container, false);

        setHasOptionsMenu(true);

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        //Todo blokada przycisku "powrót", który psuje timer.
        callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        callback.setEnabled(false);

        descriptionTextView = rootView.findViewById(R.id.timer_description);
        countdownTextView = rootView.findViewById(R.id.countdown);
        progressBar = rootView.findViewById(R.id.progress_bar);
        startTaskButton = rootView.findViewById(R.id.start_task_button);
        finishTaskButton = rootView.findViewById(R.id.finish_task_button);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        sessionLength = Integer.parseInt(preferences.getString("session_length", "25")) * 60;
        shortBreakLength = Integer.parseInt(preferences.getString("short_break_length", "5")) * 60;
        longBreakLength = Integer.parseInt(preferences.getString("long_break_length", "20")) * 60;
        sessionsPerCircle = Integer.parseInt(preferences.getString("sessions_per_circle", "4"));
        timerState = TimerState.startNextTask;
        rootView.findViewById(R.id.cancel_button).setOnClickListener(view -> cancelButtonListener());
        startTaskButton.setOnClickListener(view -> startTaskButtonListener());
        finishTaskButton.setOnClickListener(view -> finishTaskButtonListener());

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       /* NavController navController = Navigation.findNavController(view);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph()).build();
        Toolbar toolbar = view.findViewById(R.id.toolbar_new_task);
         ((MainActivity) requireActivity()).setSupportActionBar(toolbar);
         NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration); */
        Toolbar toolbar = view.findViewById(R.id.toolbar_timer);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        NavController navHostFragment = NavHostFragment.findNavController(this);

        AppBarConfiguration config = new AppBarConfiguration.Builder(R.id.taskListFragment, R.id.timerFragment, R.id.statisticsFragment, R.id.queueFragment).build();

        NavigationUI.setupWithNavController(toolbar, navHostFragment, config);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onPause() {
        super.onPause();

        if (timerState != TimerState.startNextTask) {
            timer.cancel();
            setAlarm();
        }
    }

    @Override
    public void onDestroy() {
        hideOptions(false);
        super.onDestroy();
        removeAlarm();
    }

    @Override
    public void onResume() {
        super.onResume();

        taskViewModel.getQueuedTask().observe(getViewLifecycleOwner(), list -> {
            tasksQueue = list;
            getNextTask();
            if (timerState == TimerState.startNextTask) {
                setStartNextTaskUI();
            }
        });

        if (timerState != TimerState.startNextTask) {
            long alarmSetTime = TimerPrefUtil.getAlarmSetTime(requireActivity());

            if (alarmSetTime != 0) {
                secondsRemaining -= getCurrentTimeSeconds() - TimerPrefUtil.getAlarmSetTime(requireActivity());

                if (timerState == TimerState.taskCountdown) {
                    Log.i("Timer.onResume", "timer_state_task_resume");
                    startTimer(sessionLength, secondsRemaining);
                } else {
                    Log.i("Timer.onResume", "timer_state_break_resume");
                    startTimer(sessionsCompleted % sessionsPerCircle != 0 ? shortBreakLength : longBreakLength, secondsRemaining);
                }

                removeAlarm();
            } else {
                if (timerState == TimerState.taskCountdown) {
                    Log.i("Timer.onResume", "timer_state_task");
                    finishTask();
                } else {
                    Log.i("Timer.onResume", "timer_state_break");
                    timerState = TimerState.startNextTask;
                    callback.setEnabled(false);
                    hideOptions(true);
                    progressBar.setProgress(0);
                    setStartNextTaskUI();
                }
            }
        }
    }

    private void setCountdownTextView(long time) {

        long minutesRemaining = time / 60;
        long secondsInMinuteRemaining = time % 60;

        countdownTextView.setText(getString(R.string.countdown_text,
                minutesRemaining, secondsInMinuteRemaining > 9 ? "" : "0", secondsInMinuteRemaining));
    }

    private void setStartNextTaskUI() {
        //taskViewModel.enableBottomNav();
        if (!tasksQueue.isEmpty()) {
            descriptionTextView.setText(getString(R.string.description_task, task.getName(),
                    task.getSessionsDone() + 1, task.getSessionsDone() + task.getSessionsLeft()));
            setCountdownTextView(sessionLength);
        } else {
            descriptionTextView.setText(R.string.empty_queue);
        }

        finishTaskButton.hide();
        startTaskButton.show();
    }

    private void setTaskCountdownUI() {
        if (timerState == TimerState.breakCountdown) {
            descriptionTextView.setText(getString(R.string.description_task, task.getName(),
                    task.getSessionsDone() + 1, task.getSessionsDone() + task.getSessionsLeft()));

            setCountdownTextView(sessionLength);
        }

        startTaskButton.hide();
        finishTaskButton.show();
    }

    private void setBreakCountdownUI() {
        descriptionTextView.setText(R.string.description_break);

        setCountdownTextView(sessionsCompleted % sessionsPerCircle != 0 ? shortBreakLength : longBreakLength);

        finishTaskButton.hide();
        startTaskButton.show();
    }

    private void finishTask() {
        //taskViewModel.enableBottomNav();
        ++sessionsCompleted;
        task.setSessionsDone(task.getSessionsDone() + 1);
        task.setSessionLeft(task.getSessionsLeft() - 1);
        taskViewModel.update(task);
        taskViewModel.popFirstQueuedTask();
        taskViewModel.decreasePos();
        startBreak();
    }

    private void finishTaskButtonListener() {
        callback.setEnabled(false);
        hideOptions(false);
        timer.cancel();
        finishTask();
    }

    private void cancelButtonListener() {
        if (timerState == TimerState.startNextTask) {
            // TODO wyjscie z timera
            Toast.makeText(getContext(), "No escape from timer!", Toast.LENGTH_SHORT).show();
        } else {
            callback.setEnabled(false);
            hideOptions(false);
            timer.cancel();
            progressBar.setProgress(0);
            timerState = TimerState.startNextTask;
            setStartNextTaskUI();
        }
    }

    private void startTaskButtonListener() {
        if (tasksQueue.isEmpty()) {
            Toast.makeText(getContext(), "No More Sessions Planed", Toast.LENGTH_SHORT).show();
        } else {
            if (timerState == TimerState.breakCountdown) {
                callback.setEnabled(false);
                hideOptions(true);
                timer.cancel();
            }

            setTaskCountdownUI();

            progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.taskProgressBar), PorterDuff.Mode.SRC_IN);
            timerState = TimerState.taskCountdown;
            startTimer(sessionLength);
        }
    }

    private void startTimer(long timeInterval) {
        startTimer(timeInterval, timeInterval);
    }

    private void startTimer(long timeInterval, long timeRemaining) {
        hideOptions(true);
        callback.setEnabled(true);
        progressBar.setMax((int) timeInterval);
        progressBar.setProgress((int) (timeInterval - timeRemaining));

        timer = new CountDownTimer(timeRemaining * 1000, 1000) {
            boolean timerFinishing = false;

            @Override
            public void onTick(long millisUntilFinished) {
                secondsRemaining = millisUntilFinished / 1000;
                setCountdownTextView(secondsRemaining);

                if (secondsRemaining <= 5 && !timerFinishing) {
                    timerFinishing = true;
                    progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.finishingProgessBar), PorterDuff.Mode.SRC_IN);
                }

                progressBar.setProgress((int) (timeInterval - secondsRemaining));
            }

            @Override
            public void onFinish() {
                progressBar.setProgress(0);

                if (timerState == TimerState.taskCountdown) {
                    finishTask();
                } else {
                    timerState = TimerState.startNextTask;
                    setStartNextTaskUI();
                }
                callback.setEnabled(false);
                hideOptions(false);
            }
        }.start();
    }

    private void startBreak() {
        timerState = TimerState.breakCountdown;
        setBreakCountdownUI();

        progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.breakProgessBar), PorterDuff.Mode.SRC_IN);
        startTimer(sessionsCompleted % sessionsPerCircle != 0 ? shortBreakLength : longBreakLength);
    }

    private void getNextTask() {
        if (!tasksQueue.isEmpty()) {
            task = tasksQueue.get(0);
        }
    }

    private static long getCurrentTimeSeconds() {
        return Calendar.getInstance().getTimeInMillis() / 1000;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setAlarm() {
        long currentTime = getCurrentTimeSeconds();
        AlarmManager alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(requireActivity(), ProcessTimerReceiver.class);
        TimerPrefUtil.setTimerState(timerState, requireActivity());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireActivity(), 0, intent, 0);
        assert alarmManager != null;
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, (currentTime + secondsRemaining) * 1000, pendingIntent);
        TimerPrefUtil.setAlarmSetTime(currentTime, requireActivity());
    }

    private void removeAlarm() {
        Intent intent = new Intent(requireActivity(), ProcessTimerReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireActivity(), 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.cancel(pendingIntent);
        TimerPrefUtil.setAlarmSetTime(0, requireActivity());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Pomodoro Timer");


        MenuItem editTask = menu.findItem(R.id.newTaskFragment);
        MenuItem removeTask = menu.findItem(R.id.action_delete_task);
        MenuItem settings = menu.findItem(R.id.settingsFragment);
        MenuItem cancel = menu.findItem(R.id.action_cancel_task);
        editTask.setVisible(false);
        removeTask.setVisible(false);
        cancel.setVisible(false);
        settings.setVisible(!hideOptions);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
    }

    private void hideOptions(boolean b) {
        hideOptions = b;
        requireActivity().invalidateOptionsMenu();
        ((MainActivity)requireActivity()).hideBottomNav(b);
    }
}
