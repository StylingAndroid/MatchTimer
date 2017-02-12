package com.stylingandroid.matchtimer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.Vibrator;

import java.util.concurrent.TimeUnit;

class Alerter implements UpdateScheduler {
    private static final long[] ELAPSED_PATTERN = {0, 700, 250, 700, 250, 700};
    private static final long[] FULL_TIME_PATTERN = {0, 1300, 500, 1300, 500, 1300};
    private static final long TOTAL_TIME = TimeUnit.MINUTES.toMillis(45);
    private static final int NO_REPEAT = -1;

    private final MatchTimer matchTimer;
    private final AlarmManager alarmManager;
    private final Vibrator vibrator;
    private final PendingIntent alertElapsed;
    private final PendingIntent alertPlayed;

    Alerter(MatchTimer matchTimer, AlarmManager alarmManager, Vibrator vibrator, PendingIntent alertElapsed, PendingIntent alertPlayed) {
        this.matchTimer = matchTimer;
        this.alarmManager = alarmManager;
        this.vibrator = vibrator;
        this.alertElapsed = alertElapsed;
        this.alertPlayed = alertPlayed;
    }

    @Override
    public void cancelAll() {
        alarmManager.cancel(alertElapsed);
        alarmManager.cancel(alertPlayed);
    }

    @Override
    public void scheduleNextUpdate() {
        cancelAll();
        if (matchTimer.isStarted()) {
            scheduleElapsed();
            schedulePlayed();
        } else if (matchTimer.isPaused()) {
            scheduleElapsed();
        }
    }

    private void scheduleElapsed() {
        long time = matchTimer.getCurrentTime() + (TOTAL_TIME - matchTimer.getElapsed());
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, alertElapsed);
        //Log.v("TAG", "Elapsed scheduled for " + DateFormat.getTimeInstance().format(time));
    }

    private void schedulePlayed() {
        long time = matchTimer.getCurrentTime() + (TOTAL_TIME - matchTimer.getPlayed());
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, alertPlayed);
        //Log.v("TAG", "Played scheduled for " + DateFormat.getTimeInstance().format(time));
    }

    void elapsedAlarm() {
        vibrator.vibrate(ELAPSED_PATTERN, NO_REPEAT);
        //Log.v("TAG", "Elapsed Alarm");
    }

    void playedAlarm() {
        vibrator.vibrate(FULL_TIME_PATTERN, NO_REPEAT);
        //Log.v("TAG", "Played Alarm");
    }
}
