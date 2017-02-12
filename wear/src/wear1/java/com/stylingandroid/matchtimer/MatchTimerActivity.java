package com.stylingandroid.matchtimer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

public class MatchTimerActivity extends Activity implements View.OnClickListener {
    public static final int MINUTE_MILLIS = 60000;
    public static final int SECOND_MILLIS = 1000;
    private Handler handler = null;

    private TextView elapsed;
    private TextView played;
    private TextView totalStoppages;
    private View timer;

    private MatchTimer matchTimer;

    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            update();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_timer);
        handler = new Handler();
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                elapsed = (TextView) stub.findViewById(R.id.elapsed);
                played = (TextView) stub.findViewById(R.id.played);
                totalStoppages = (TextView) stub.findViewById(R.id.all_stoppages);
                timer = stub.findViewById(R.id.timer);
                timer.setOnClickListener(MatchTimerActivity.this);
                updateWithoutTimer();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        matchTimer = MatchTimer.newInstance(this);
        matchTimer.registerForUpdates();
        update();
    }

    @Override
    protected void onPause() {
        matchTimer.unregisterForUpdates();
        handler.removeCallbacks(updateRunnable);
        super.onPause();
    }

    private void nextTimer() {
        handler.removeCallbacks(updateRunnable);
        handler.postDelayed(updateRunnable, SECOND_MILLIS);
    }

    private void update() {
        updateWithoutTimer();
        nextTimer();
    }

    private void updateWithoutTimer() {
        long playedMillis = matchTimer.getPlayed();
        long stoppedMillis = matchTimer.getTotalStoppages();
        if (elapsed != null) {
            elapsed.setText(format(playedMillis + stoppedMillis));
        }
        if (played != null) {
            played.setText(format(playedMillis));
        }
        if (totalStoppages != null) {
            totalStoppages.setText(format(stoppedMillis));
        }
    }

    private String format(long elapsedMillis) {
        long mins = elapsedMillis / MINUTE_MILLIS;
        long secs = (elapsedMillis - (mins * MINUTE_MILLIS)) / SECOND_MILLIS;
        return String.format(Locale.getDefault(), "%2d:%02d", mins, secs);
    }

    @Override
    public void onClick(View v) {
        if (matchTimer.isPaused()) {
           sendBroadcast(MatchTimerReceiver.RESUME_INTENT);
        } else if (matchTimer.isRunning()) {
            sendBroadcast(MatchTimerReceiver.PAUSE_INTENT);
        } else {
            sendBroadcast(MatchTimerReceiver.START_INTENT);
        }
    }
}
