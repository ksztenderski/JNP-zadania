package com.example.plague;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class MyAsyncTask extends AsyncTask<Void, Void, Boolean> {
    private WeakReference<TextView> threadsWorkingTextView, healthyTextView, infectedTextView, deadTextView;
    private WeakReference<AtomicInteger> threadsCount, healthyCount, infectedCount, deadCount;
    private int healthySleepTime, infectedSleepTime;
    private int infectionProbability, deathProbability;
    private boolean isHealthy;
    private WeakReference<Context> context;

    public MyAsyncTask(Context context, TextView threadsWorkingTextView, TextView healthyTextView,
                       TextView infectedTextView, TextView deadTextView, AtomicInteger threadsCount,
                       AtomicInteger healthyCount, AtomicInteger infectedCount, AtomicInteger deadCount,
                       int healthySleepTime, int infectedSleepTime, int infectionProbability,
                       int deathProbability, boolean isHealthy) {
        this.threadsWorkingTextView = new WeakReference<>(threadsWorkingTextView);
        this.healthyTextView = new WeakReference<>(healthyTextView);
        this.infectedTextView = new WeakReference<>(infectedTextView);
        this.deadTextView = new WeakReference<>(deadTextView);
        this.threadsCount = new WeakReference<>(threadsCount);
        this.healthyCount = new WeakReference<>(healthyCount);
        this.infectedCount = new WeakReference<>(infectedCount);
        this.deadCount = new WeakReference<>(deadCount);
        this.healthySleepTime = healthySleepTime * 1000;
        this.infectedSleepTime = infectedSleepTime * 1000;
        this.infectionProbability = infectionProbability;
        this.deathProbability = deathProbability;
        this.isHealthy = isHealthy;
        this.context = new WeakReference<>(context);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        String HEALTHY_TAG = "healthy";
        String INFECTED_TAG = "infected";
        Random random = new Random();
        boolean isAlive = true;
        int contact, roll;

        while (isAlive) {
            if (isCancelled()) {
                break;
            }

            if (isHealthy) {
                Log.i(HEALTHY_TAG, "enter");
                if (infectedCount.get().get() > 0) {
                    contact = random.nextInt(infectedCount.get().get());
                    for (int i = 0; i < contact; i++) {
                        roll = random.nextInt(100);

                        if (roll < infectionProbability) {
                            publishProgress();
                            break;
                        }
                    }
                }

                try {
                    Thread.sleep(healthySleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                Log.i(INFECTED_TAG, "enter");
                try {
                    Thread.sleep(infectedSleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                roll = random.nextInt(100);

                if (roll < deathProbability) {
                    isAlive = false;
                } else {
                    publishProgress();
                }
            }
        }

        return isAlive;
    }

    @Override
    protected void onPostExecute(Boolean isAlive) {
        if (!isAlive) {
            int count = infectedCount.get().decrementAndGet();
            infectedTextView.get().setText(context.get().getResources().getString(R.string.threads_infected, count));
            count = deadCount.get().incrementAndGet();
            deadTextView.get().setText(context.get().getResources().getString(R.string.threads_dead, count));
            count = threadsCount.get().decrementAndGet();
            threadsWorkingTextView.get().setText(context.get().getResources().getString(R.string.threads_working, count));
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        int count;
        if (isHealthy) {
            count = healthyCount.get().decrementAndGet();
            healthyTextView.get().setText(context.get().getResources().getString(R.string.threads_healthy, count));
            count = infectedCount.get().incrementAndGet();
            infectedTextView.get().setText(context.get().getResources().getString(R.string.threads_infected, count));
        } else {
            count = infectedCount.get().decrementAndGet();
            infectedTextView.get().setText(context.get().getResources().getString(R.string.threads_infected, count));
            count = healthyCount.get().incrementAndGet();
            healthyTextView.get().setText(context.get().getResources().getString(R.string.threads_healthy, count));
        }

        isHealthy = !isHealthy;
    }
}