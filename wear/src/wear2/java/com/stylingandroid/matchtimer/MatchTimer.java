package com.stylingandroid.matchtimer;

import java.util.concurrent.TimeUnit;

class MatchTimer {
    private static final long DURATION = TimeUnit.MINUTES.toMillis(45);

    private final MatchTimerState state;

    MatchTimer(MatchTimerState state) {
        this.state = state;
    }

    void start() {
        if (isStopped()) {
            if (state.getStart() == 0) {
                state.setStart(getCurrentTime());
            }
            state.setPauseStart(0);
            if (state.getStop() > 0) {
                state.addStopped(getCurrentTime() - state.getStop());
            }
            state.setStop(0);
        }
    }

    void stop() {
        if (isPaused()) {
            resume();
        }
        if (isStarted()) {
            state.setStop(getCurrentTime());
        }
    }

    void pause() {
        if (isStarted()) {
            state.setPauseStart(getCurrentTime());
        }
    }

    private void resume() {
        if (isPaused()) {
            state.addPaused(getCurrentTime() - state.getPauseStart());
            state.setPauseStart(0);
        }
    }

    private void reset() {
        state.reset();
    }

    void toggleState() {
        if (isStopped()) {
            start();
        } else if (isStarted()) {
            pause();
        } else if (isPaused()) {
            resume();
        }
    }

    TimerState getState() {
        if (isStarted()) {
            return TimerState.PLAYING;
        } else if (isPaused()) {
            return TimerState.PAUSED;
        }
        return TimerState.STOPPED;
    }

    void resetState() {
        if (isStopped()) {
            reset();
        } else {
            stop();
        }
    }

    long getElapsed() {
        if (state.getStart() > 0) {
            return getStoppedMillis() - state.getStart();
        }
        return 0;
    }

    private long getStoppedMillis() {
        if (state.getStop() > 0) {
            return state.getStop() - state.getStopped();
        }
        return state.getCurrentTime() - state.getStopped();
    }

    long getStoppages() {
        long totalPaused = state.getPaused();
        if (state.getPauseStart() > 0) {
            totalPaused += getCurrentTime() - state.getPauseStart();
        }
        return totalPaused;
    }

    long getPlayed() {
        return getElapsed() - getStoppages();
    }

    long getRemaining() {
        if (isStopped()) {
            return 0;
        }
        return state.getStart() + DURATION - getCurrentTime();
    }

    long getRemainingToPlay() {
        if (!isStarted()) {
            return 0;
        }
        return getRemaining() + getStoppages();
    }

    boolean isStarted() {
        return state.getStart() > 0 && !isPaused() && !isStopped();
    }

    boolean isStopped() {
        return state.getStart() <= 0 || state.getStop() > 0;
    }

    boolean isPaused() {
        return state.getPauseStart() > 0;
    }

    boolean hasStoppages() {
        return state.getStopped() + state.getPauseStart() > 0;
    }

    long getCurrentTime() {
        return state.getCurrentTime();
    }
}
