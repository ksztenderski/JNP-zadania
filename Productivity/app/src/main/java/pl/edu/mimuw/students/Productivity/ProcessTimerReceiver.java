package pl.edu.mimuw.students.Productivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ProcessTimerReceiver extends BroadcastReceiver {
    public static String SAVED_TIMER_STATE = "pl.edu.mimuw.students.Productivity.ProcessTimerReceiver.saved_timer_state";

    @Override
    public void onReceive(Context context, Intent intent) {
        TimerFragment.TimerState timerState = TimerPrefUtil.getTimerState(context);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Intent newIntent = new Intent(context, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        newIntent.setAction(Intent.ACTION_MAIN);
        newIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(
                context, 0, newIntent, 0);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(context.getString(R.string.notification_chanel_id),
                    context.getString(R.string.notification_chanel_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Timer");
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, context.getString(R.string.notification_chanel_id))
                .setSmallIcon(R.drawable.ic_timer_white_24dp)
                .setContentIntent(contentIntent);
        switch (timerState) {
            case startNextTask:
                break;
            case taskCountdown:
                notificationBuilder = notificationBuilder.setContentTitle(context.getString(R.string.notification_task_end));
                break;
            case breakCountdown:
                notificationBuilder = notificationBuilder.setContentTitle(context.getString(R.string.notification_break_end));
                break;
        }
        Notification notification = notificationBuilder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR | Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(1, notification);
        TimerPrefUtil.setAlarmSetTime(0, context);
    }
}
