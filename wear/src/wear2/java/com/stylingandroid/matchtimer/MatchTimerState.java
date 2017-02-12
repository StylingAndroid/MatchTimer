package com.stylingandroid.matchtimer;

import static java.lang.System.currentTimeMillis;

class MatchTimerState {
    private long start;
    private long stop;
    private long pauseStart;
    private long paused;
    private long stopped;

    void reset() {
        start = 0;
        stop = 0;
        pauseStart = 0;
        paused = 0;
        stopped = 0;
    }

    long getStart() {
        return start;
    }

    void setStart(long start) {
        this.start = start;
    }

    long getStop() {
        return stop;
    }

    void setStop(long stop) {
        this.stop = stop;
    }

    long getPauseStart() {
        return pauseStart;
    }

    void setPauseStart(long pauseStart) {
        this.pauseStart = pauseStart;
    }

    long getPaused() {
        return paused;
    }

    void addPaused(long currentPause) {
        this.paused += currentPause;
    }

    long getStopped() {
        return stopped;
    }

    void addStopped(long currentStoppage) {
        this.stopped += currentStoppage;
    }

    long getCurrentTime() {
        return currentTimeMillis();
    }
}
