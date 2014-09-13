package com.stylingandroid.matchtimer;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.NotificationManagerCompat;

public class MatchTimerReceiver extends BroadcastReceiver {
    public static final String ACTION_START = "com.stylingandroid.matchtimer.ACTION_START";
    public static final String ACTION_STOP = "com.stylingandroid.matchtimer.ACTION_STOP";
    public static final String ACTION_PAUSE = "com.stylingandroid.matchtimer.ACTION_PAUSE";
    public static final String ACTION_RESUME = "com.stylingandroid.matchtimer.ACTION_RESUME";
    public static final String ACTION_RESET = "com.stylingandroid.matchtimer.ACTION_RESET";
    public static final String ACTION_UPDATE = "com.stylingandroid.matchtimer.ACTION_UPDATE";
    public static final String ACTION_ELAPSED_ALARM = "com.stylingandroid.matchtimer.ACTION_ELAPSED_ALARM";
    public static final String ACTION_FULL_TIME_ALARM = "com.stylingandroid.matchtimer.ACTION_FULL_TIME_ALARM";

    public static final Intent START_INTENT = new Intent(ACTION_START);
    public static final Intent STOP_INTENT = new Intent(ACTION_STOP);
    public static final Intent PAUSE_INTENT = new Intent(ACTION_PAUSE);
    public static final Intent RESUME_INTENT = new Intent(ACTION_RESUME);
    public static final Intent RESET_INTENT = new Intent(ACTION_RESET);
    private static final Intent UPDATE_INTENT = new Intent(ACTION_UPDATE);
    private static final Intent ELAPSED_ALARM = new Intent(ACTION_ELAPSED_ALARM);
    private static final Intent FULL_TIME_ALARM = new Intent(ACTION_FULL_TIME_ALARM);

    private static final int REQUEST_UPDATE = 1;
    private static final int REQUEST_ELAPSED = 2;
    private static final int REQUEST_FULL_TIME = 3;

    public static final int MINUTE_MILLIS = 60000;
    private static final long DURATION = 45 * MINUTE_MILLIS;

    private static final long[] ELAPSED_PATTERN = {0, 500, 250, 500, 250, 500};
    private static final long[] FULL_TIME_PATTERN = {0, 1000, 500, 1000, 500, 1000};
    public static final int NOTIFICATION_ID = 1;

    public static void setUpdate(Context context) {
        context.sendBroadcast(UPDATE_INTENT);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        MatchTimer timer = MatchTimer.newInstance(context);
        if (intent.getAction().equals(ACTION_START)) {
            start(context, timer);
        } else if (intent.getAction().equals(ACTION_STOP)) {
            stop(context, timer);
        } else if (intent.getAction().equals(ACTION_PAUSE)) {
            pause(context, timer);
        } else if (intent.getAction().equals(ACTION_RESUME)) {
            resume(context, timer);
        } else if (intent.getAction().equals(ACTION_RESET)) {
            reset(timer);
        } else if (intent.getAction().equals(ACTION_ELAPSED_ALARM)) {
            elapsedAlarm(context);
        } else if (intent.getAction().equals(ACTION_FULL_TIME_ALARM)) {
            fullTimeAlarm(context);
        }
        updateNotification(context, timer);
    }

    private void elapsedAlarm(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(ELAPSED_PATTERN, -1);
    }

    private void fullTimeAlarm(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(FULL_TIME_PATTERN, -1);
    }

    private void reset(MatchTimer timer) {
        timer.reset();
    }

    private void resume(Context context, MatchTimer timer) {
        timer.resume();
        long playedEnd = timer.getStartTime() + timer.getTotalStoppages() + DURATION;
        if (playedEnd > System.currentTimeMillis()) {
            setAlarm(context, REQUEST_FULL_TIME, FULL_TIME_ALARM, playedEnd);
        }
    }

    private void pause(Context context, MatchTimer timer) {
        timer.pause();
        cancelAlarm(context, REQUEST_FULL_TIME, FULL_TIME_ALARM);
        long elapsedEnd = timer.getStartTime() + DURATION;
        if (!isAlarmSet(context, REQUEST_ELAPSED, ELAPSED_ALARM) && elapsedEnd > System.currentTimeMillis()) {
            setAlarm(context, REQUEST_ELAPSED, ELAPSED_ALARM, elapsedEnd);
        }
    }

    private void stop(Context context, MatchTimer timer) {
        timer.stop();
        cancelAlarm(context, REQUEST_UPDATE, UPDATE_INTENT);
        cancelAlarm(context, REQUEST_ELAPSED, ELAPSED_ALARM);
        cancelAlarm(context, REQUEST_FULL_TIME, FULL_TIME_ALARM);
    }

    private void start(Context context, MatchTimer timer) {
        timer.start();
        long elapsedEnd = timer.getStartTime() + DURATION;
        setRepeatingAlarm(context, REQUEST_UPDATE, UPDATE_INTENT);
        if (timer.getTotalStoppages() > 0 && !timer.isPaused()) {
            long playedEnd = timer.getStartTime() + timer.getTotalStoppages() + DURATION;
            if (playedEnd > System.currentTimeMillis()) {
                setAlarm(context, REQUEST_FULL_TIME, FULL_TIME_ALARM, playedEnd);
            }
            if (elapsedEnd > System.currentTimeMillis()) {
                setAlarm(context, REQUEST_ELAPSED, ELAPSED_ALARM, elapsedEnd);
            }
        } else {
            if (elapsedEnd > System.currentTimeMillis()) {
                setAlarm(context, REQUEST_FULL_TIME, FULL_TIME_ALARM, elapsedEnd);
            }
        }
    }

    private void updateNotification(Context context, MatchTimer timer) {
        NotificationBuilder builder = new NotificationBuilder(context, timer);
        Notification notification = builder.buildNotification();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void setRepeatingAlarm(Context context, int requestCode, Intent intent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), MINUTE_MILLIS, pendingIntent);
    }

    private boolean isAlarmSet(Context context, int requestCode, Intent intent) {
        return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_NO_CREATE) != null;
    }

    private void setAlarm(Context context, int requestCode, Intent intent, long time) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }

    private void cancelAlarm(Context context, int requestCode, Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }
}
