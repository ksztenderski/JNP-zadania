package pl.edu.mimuw.students.Productivity;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

class TimerPrefUtil {
    private static String ALARM_SET_TIME_ID = "pl.edu.mimuw.students.Productivity.TimerFragment.backgrounded_time";
    private static String TIMER_STATE_ID = "pl.edu.mimuw.students.Productivity.TimerFragment.timer_state";

    static long getAlarmSetTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getLong(ALARM_SET_TIME_ID, 0);
    }

    static void setAlarmSetTime(long time, Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putLong(ALARM_SET_TIME_ID, time);
        editor.apply();
    }

    static TimerFragment.TimerState getTimerState(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int ordinal = preferences.getInt(TIMER_STATE_ID, 0);
        return TimerFragment.TimerState.values()[ordinal];
    }

    static void setTimerState(TimerFragment.TimerState timerState, Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putInt(TIMER_STATE_ID, timerState.ordinal());
        editor.apply();
    }
}
