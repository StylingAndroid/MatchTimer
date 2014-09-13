package com.stylingandroid.matchtimer;

import android.content.Context;
import android.content.SharedPreferences;

public final class MatchTimer implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String KEY_START = "com.stylingandroid.matchtimer.KEY_START";
    private static final String KEY_CURRENT_STOPPAGE = "com.stylingandroid.matchtimer.KEY_CURRENT_STOPPAGE";
    private static final String KEY_TOTAL_STOPPAGES = "com.stylingandroid.matchtimer.KEY_TOTAL_STOPPAGES";
    private static final String KEY_END = "com.stylingandroid.matchtimer.KEY_END";
    private static final String PREFERENCES = "MatchTimer";
    public static final int MINUTE_MILLIS = 60000;

    private final SharedPreferences preferences;

    private long start;
    private long currentStoppage;
    private long totalStoppages;
    private long end;

    public static MatchTimer newInstance(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        long start = preferences.getLong(KEY_START, 0);
        long currentStoppage = preferences.getLong(KEY_CURRENT_STOPPAGE, 0);
        long totalStoppages = preferences.getLong(KEY_TOTAL_STOPPAGES, 0);
        long end = preferences.getLong(KEY_END, 0);
        return new MatchTimer(preferences, start, currentStoppage, totalStoppages, end);
    }

    private MatchTimer(SharedPreferences preferences, long start, long currentStoppage, long totalStoppages, long end) {
        this.preferences = preferences;
        this.start = start;
        this.currentStoppage = currentStoppage;
        this.totalStoppages = totalStoppages;
        this.end = end;
    }

    public void registerForUpdates() {
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    public void unregisterForUpdates() {
        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    public void save() {
        preferences.edit()
                .putLong(KEY_START, start)
                .putLong(KEY_CURRENT_STOPPAGE, currentStoppage)
                .putLong(KEY_TOTAL_STOPPAGES, totalStoppages)
                .putLong(KEY_END, end)
                .apply();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        long value = preferences.getLong(key, 0L);
        if (key.equals(KEY_START) && value > 0L) {
            start = value;
        } else if (key.equals(KEY_END)) {
            end = value;
        } else if (key.equals(KEY_CURRENT_STOPPAGE)) {
            currentStoppage = value;
        } else if (key.equals(KEY_TOTAL_STOPPAGES)) {
            totalStoppages = value;
        }
    }

    public long getElapsed() {
        if (isRunning()) {
            return System.currentTimeMillis() - start;
        }
        if (end > 0) {
            return end - start;
        }
        return 0;
    }

    public boolean isRunning() {
        return start > 0 && end == 0;
    }

    public boolean isPaused() {
        return currentStoppage > 0;
    }

    public int getElapsedMinutes() {
        return (int) ((System.currentTimeMillis() - start) / MINUTE_MILLIS);
    }

    public long getTotalStoppages() {
        long now = System.currentTimeMillis();
        if (isPaused()) {
            return totalStoppages + (now - currentStoppage);
        }
        return totalStoppages;
    }

    public long getPlayed() {
        return getElapsed() - getTotalStoppages();
    }

    public long getStartTime() {
        return start;
    }

    public void start() {
        if (end > 0) {
            start = System.currentTimeMillis() - (end - start);
            end = 0;
        } else {
            start = System.currentTimeMillis();
        }
        save();
    }

    public void stop() {
        if (isPaused()) {
            resume();
        }
        end = System.currentTimeMillis();
        save();
    }

    public void pause() {
        currentStoppage = System.currentTimeMillis();
        save();
    }

    public void resume() {
        if (currentStoppage > start) {
            totalStoppages += System.currentTimeMillis() - currentStoppage;
        }
        currentStoppage = 0L;
        save();
    }

    public void reset() {
        resetWithoutSave();
        save();
    }

    private void resetWithoutSave() {
        start = 0L;
        currentStoppage = 0L;
        totalStoppages = 0L;
        end = 0L;
    }
}
