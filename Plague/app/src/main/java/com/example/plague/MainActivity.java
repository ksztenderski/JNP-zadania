package com.example.plague;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {
    private TextView threadsWorkingTextView, healthyTextView, infectedTextView, deadTextView, timerTextView;
    private AtomicInteger threadsCount, infectedCount, deadCount = new AtomicInteger(0);
    private long timeLeft;
    private CountDownTimer timer;
    ArrayList<MyAsyncTask> threads;
    EditText simulationTimeEditText, threadsEditText, infectionProbabilityEditText, deathProbabilityEditText,
            infectedCountEditText, healthySleepTimeEditText, infectedSleepTimeEditText;
    private LinearLayout inputLayout, displayLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        threadsWorkingTextView = findViewById(R.id.threads_count);
        healthyTextView = findViewById(R.id.threads_healthy);
        infectedTextView = findViewById(R.id.threads_infected);
        deadTextView = findViewById(R.id.threads_dead);
        timerTextView = findViewById(R.id.timer);
        simulationTimeEditText = findViewById(R.id.simulation_time);
        inputLayout = findViewById(R.id.input_layout);
        displayLayout = findViewById(R.id.display_layout);
        threads = new ArrayList<>();
    }

    public void start(View view) {
        if (inputLayout.getVisibility() == View.GONE) {
            return;
        }
        threadsEditText = findViewById(R.id.number_of_threads);
        infectionProbabilityEditText = findViewById(R.id.infection_probability);
        deathProbabilityEditText = findViewById(R.id.death_probability);
        infectedCountEditText = findViewById(R.id.starting_number_of_infected);
        healthySleepTimeEditText = findViewById(R.id.healthy_sleep_time);
        infectedSleepTimeEditText = findViewById(R.id.infected_sleep_time);

        if (threadsEditText.getText().toString().isEmpty() ||
                infectionProbabilityEditText.getText().toString().isEmpty() ||
                deathProbabilityEditText.getText().toString().isEmpty() ||
                infectedCountEditText.getText().toString().isEmpty() ||
                healthySleepTimeEditText.getText().toString().isEmpty() ||
                infectedSleepTimeEditText.getText().toString().isEmpty()) {
            Toast toast = Toast.makeText(this, "Fill parameters!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        threadsCount = new AtomicInteger(Integer.parseInt(threadsEditText.getText().toString()));
        timeLeft = Integer.parseInt(simulationTimeEditText.getText().toString());
        int infectionProbability = Integer.parseInt(infectionProbabilityEditText.getText().toString());
        int deathProbability = Integer.parseInt(deathProbabilityEditText.getText().toString());
        infectedCount = new AtomicInteger(Integer.parseInt(infectedCountEditText.getText().toString()));
        AtomicInteger healthyCount = new AtomicInteger(threadsCount.get() - infectedCount.get());
        int healthySleepTime = Integer.parseInt(healthySleepTimeEditText.getText().toString());
        int infectedSleepTime = Integer.parseInt(infectedSleepTimeEditText.getText().toString());

        threadsWorkingTextView.setText(getString(R.string.threads_working, threadsCount.get()));
        healthyTextView.setText(getString(R.string.threads_healthy, healthyCount.get()));
        infectedTextView.setText(getString(R.string.threads_infected, infectedCount.get()));
        deadTextView.setText(getString(R.string.threads_dead, deadCount.get()));


        int infected = infectedCount.get();
        int healthy = healthyCount.get();

        for (int i = 0; i < infected; i++) {
            threads.add(new MyAsyncTask(this, threadsWorkingTextView, healthyTextView, infectedTextView,
                    deadTextView, threadsCount, healthyCount, infectedCount, deadCount, healthySleepTime,
                    infectedSleepTime, infectionProbability, deathProbability, false));
        }

        for (int i = 0; i < healthy; i++) {
            threads.add(new MyAsyncTask(this, threadsWorkingTextView, healthyTextView, infectedTextView,
                    deadTextView, threadsCount, healthyCount, infectedCount, deadCount, healthySleepTime,
                    infectedSleepTime, infectionProbability, deathProbability, true));
        }

        for (MyAsyncTask thread : threads) {
            thread.execute();
        }

        inputLayout.setVisibility(View.GONE);
        displayLayout.setVisibility(View.VISIBLE);

        timer = new CountDownTimer(timeLeft * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished / 1000;
                long min = timeLeft / 60;
                long sec = timeLeft - min * 60;
                timerTextView.setText(getString(R.string.timer, min, sec > 9 ? "" : "0", sec));
            }

            @Override
            public void onFinish() {
                timerTextView.setText(getString(R.string.timer, 0, "0", 0));

                for (MyAsyncTask thread : threads) {
                    thread.cancel(true);
                }

                threads.clear();

                displayLayout.setVisibility(View.GONE);
                inputLayout.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    public void stop(View view) {
        if (displayLayout.getVisibility() == View.VISIBLE) {
            timer.cancel();

            for (MyAsyncTask thread : threads) {
                thread.cancel(true);
            }

            threads.clear();

            displayLayout.setVisibility(View.GONE);
            inputLayout.setVisibility(View.VISIBLE);
        }
    }

    public void pause(View view) {
        if (displayLayout.getVisibility() == View.VISIBLE) {
            stop(view);

            simulationTimeEditText.setText(String.format(Locale.getDefault(), "%d", timeLeft));
            threadsEditText.setText(String.format(Locale.getDefault(), "%d", threadsCount.get()));
            infectedCountEditText.setText(String.format(Locale.getDefault(), "%d", infectedCount.get()));
        }
    }
}