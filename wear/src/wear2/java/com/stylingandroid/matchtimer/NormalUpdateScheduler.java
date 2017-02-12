package com.stylingandroid.matchtimer;

import android.view.View;

class NormalUpdateScheduler implements UpdateScheduler, Runnable {
    private final OffsetCalculator calculator = new OffsetCalculator();

    private final MatchTimer matchTimer;
    private final View view;
    private final Updater updater;

    NormalUpdateScheduler(MatchTimer matchTimer, View view, Updater updater) {
        this.matchTimer = matchTimer;
        this.view = view;
        this.updater = updater;
    }

    @Override
    public void cancelAll() {
        view.removeCallbacks(this);
    }

    @Override
    public void scheduleNextUpdate() {
        long offset = getNextUpdateOffset();
        view.postDelayed(this, offset);
    }

    private long getNextUpdateOffset() {
        long nextElapsed = calculator.getMillisToNextSecondBoundary(matchTimer.getElapsed());
        long nextOther;
        if (matchTimer.isPaused()) {
            nextOther = calculator.getMillisToNextSecondBoundary(matchTimer.getStoppages());
        } else {
            nextOther = calculator.getMillisToNextSecondBoundary(matchTimer.getPlayed());
        }
        long nextTime = calculator.getMillisToNextMinuteBoundary(matchTimer.getCurrentTime());
        return Math.min(nextElapsed, Math.min(nextOther, nextTime));
    }

    @Override
    public void run() {
        cancelAll();
        updater.updateNormalText();
        scheduleNextUpdate();
    }
}
