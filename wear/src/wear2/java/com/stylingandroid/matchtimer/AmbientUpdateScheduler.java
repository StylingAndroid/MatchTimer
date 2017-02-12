package com.stylingandroid.matchtimer;

import android.app.AlarmManager;
import android.app.PendingIntent;
class AmbientUpdateScheduler implements UpdateScheduler {
    private final OffsetCalculator calculator = new OffsetCalculator();

    private final MatchTimer matchTimer;
    private final AlarmManager alarmManager;
    private final PendingIntent uiUpdatePendingIntent;

    AmbientUpdateScheduler(MatchTimer matchTimer, AlarmManager alarmManager, PendingIntent uiUpdatePendingIntent) {
        this.matchTimer = matchTimer;
        this.alarmManager = alarmManager;
        this.uiUpdatePendingIntent = uiUpdatePendingIntent;
    }

    @Override
    public void cancelAll() {
        alarmManager.cancel(uiUpdatePendingIntent);
    }

    @Override
    public void scheduleNextUpdate() {
        long offset = getNextUpdateOffset();

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, offset + matchTimer.getCurrentTime(), uiUpdatePendingIntent);
    }

    private long getNextUpdateOffset() {
        long nextElapsed = calculator.getMillisToNextMinuteBoundary(matchTimer.getElapsed());
        long nextOther;
        if (matchTimer.isPaused()) {
            nextOther = calculator.getMillisToNextMinuteBoundary(matchTimer.getStoppages());
        } else {
            nextOther = calculator.getMillisToNextMinuteBoundary(matchTimer.getPlayed());
        }
        return Math.min(nextElapsed, nextOther);
    }
}
