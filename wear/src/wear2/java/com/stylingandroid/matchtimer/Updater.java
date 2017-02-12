package com.stylingandroid.matchtimer;

import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

class Updater {
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static final long ROUNDING_MILLIS = 0;
    private static final String NORMAL_FORMAT = "%02d:%02d";
    private static final String AMBIENT_FORMAT = "%2d'";

    private final MatchTimer matchTimer;
    private final TextView currentTime;
    private final TextView elapsed;
    private final TextView played;
    private final TextView stoppages;

    Updater(MatchTimer matchTimer, TextView currentTime, TextView elapsed, TextView played, TextView stoppages) {
        this.matchTimer = matchTimer;
        this.currentTime = currentTime;
        this.elapsed = elapsed;
        this.played = played;
        this.stoppages = stoppages;
    }

    void updateAmbientText() {
        String format = matchTimer.isStopped() ? NORMAL_FORMAT : AMBIENT_FORMAT;
        updateText(format);
    }

    void updateNormalText() {
        updateText(NORMAL_FORMAT);
    }

    private void updateText(String format) {
        long elapsedTime = matchTimer.getElapsed();
        long playedTime = matchTimer.getPlayed();
        long stoppagesTime = matchTimer.getStoppages();
        Date now = new Date();
        currentTime.setText(TIME_FORMAT.format(now));
        updateTextView(elapsed, format, elapsedTime);
        if (matchTimer.isPaused()) {
            updateTextView(played, NORMAL_FORMAT, playedTime);
            updateTextView(stoppages, format, stoppagesTime);
        } else {
            updateTextView(played, format, playedTime);
            updateTextView(stoppages, NORMAL_FORMAT, stoppagesTime);
        }
    }

    private void updateTextView(TextView textView, String format, long duration) {
        textView.setText(String.format(Locale.getDefault(), format, getMinutes(duration), getSeconds(duration)));

    }

    private long getSeconds(long milliseconds) {
        return TimeUnit.MILLISECONDS.toSeconds(milliseconds + ROUNDING_MILLIS) - TimeUnit.MINUTES.toSeconds(getMinutes(milliseconds));
    }

    private long getMinutes(long milliseconds) {
        return TimeUnit.MILLISECONDS.toMinutes(milliseconds + ROUNDING_MILLIS);
    }
}
