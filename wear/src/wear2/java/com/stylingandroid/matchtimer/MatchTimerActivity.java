package com.stylingandroid.matchtimer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MatchTimerActivity extends WearableActivity {
    private static final String UI_UPDATE = "com.stylingandroid.matchtimer.UI_UPDATE";
    private static final String ALERT_ELAPSED = "com.stylingandroid.matchtimer.ALERT_ELAPSED";
    private static final String ALERT_PLAYED = "com.stylingandroid.matchtimer.ALERT_PLAYED";

    private ImageView background;
    private TextView currentTime;
    private TextView elapsed;
    private TextView played;
    private TextView stoppages;
    private List<View> labels;

    private int ambientTextColor;
    private int normalTextColor;

    private MatchTimer matchTimer;
    private Updater updater;

    private NormalUpdateScheduler normalUpdateScheduler;
    private AmbientUpdateScheduler ambientUpdateScheduler;
    private UpdateScheduler currentUpdateScheduler;

    private Alerter alerter;

    private ButtonAnimator buttonAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_timer);

        matchTimer = new MatchTimer(new MatchTimerState());

        ambientTextColor = ContextCompat.getColor(this, R.color.ambient_text);
        normalTextColor = ContextCompat.getColor(this, R.color.normal_text);

        ViewGroup parent = (ViewGroup) findViewById(R.id.parent);
        background = (ImageView) findViewById(R.id.background);
        currentTime = (TextView) findViewById(R.id.current_time);
        elapsed = (TextView) findViewById(R.id.total_elapsed);
        played = (TextView) findViewById(R.id.played_elapsed);
        stoppages = (TextView) findViewById(R.id.total_stoppages);
        ImageView toggleStateButton = (ImageView) findViewById(R.id.button_toggle_state);
        ImageView resetStateButton = (ImageView) findViewById(R.id.button_reset_state);

        labels = new ArrayList<>();
        labels.add(findViewById(R.id.label_played_elapsed));
        labels.add(findViewById(R.id.label_total_elapsed));
        labels.add(findViewById(R.id.label_total_stoppages));

        setupClickListeners(background, toggleStateButton, resetStateButton);
        buttonAnimator = createButtonAnimator(toggleStateButton, resetStateButton, parent);
        buttonAnimator.hideInstantly();

        updater = new Updater(matchTimer, currentTime, elapsed, played, stoppages);

        normalUpdateScheduler = new NormalUpdateScheduler(matchTimer, parent, updater);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent uiUpdateIntent = new Intent(UI_UPDATE);
        PendingIntent uiUpdatePendingIntent = PendingIntent.getActivity(this, 0, uiUpdateIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        ambientUpdateScheduler = new AmbientUpdateScheduler(matchTimer, alarmManager, uiUpdatePendingIntent);

        currentUpdateScheduler = normalUpdateScheduler;
        currentUpdateScheduler.scheduleNextUpdate();

        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        Intent alertElapsed = new Intent(ALERT_ELAPSED);
        PendingIntent pendingIntentElapsed = PendingIntent.getActivity(this, 0, alertElapsed, PendingIntent.FLAG_CANCEL_CURRENT);
        Intent alertPlayed = new Intent(ALERT_PLAYED);
        PendingIntent pendingIntentPlayed = PendingIntent.getActivity(this, 0, alertPlayed, PendingIntent.FLAG_CANCEL_CURRENT);
        alerter = new Alerter(matchTimer, alarmManager, vibrator, pendingIntentElapsed, pendingIntentPlayed);

        setAmbientEnabled();
    }

    private void setupClickListeners(View view, View toggleButton, View resetButton) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonAnimator.isVisible()) {
                    buttonAnimator.toggle();
                }
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                toggleButtons();
                return true;
            }
        });
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matchTimer.toggleState();
                toggleButtons();
                updateText();
                alerter.scheduleNextUpdate();
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matchTimer.resetState();
                toggleButtons();
                updateText();
                alerter.scheduleNextUpdate();
            }
        });
    }

    private ButtonAnimator createButtonAnimator(ImageView toggleStateButton, ImageView resetStateButton, ViewGroup parent) {
        View buttonBackground = findViewById(R.id.button_background);
        return new ButtonAnimator(toggleStateButton, resetStateButton, buttonBackground, parent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        switch (action) {
            case UI_UPDATE:
                updateText();
                currentUpdateScheduler.scheduleNextUpdate();
                break;
            case ALERT_ELAPSED:
                alerter.elapsedAlarm();
                break;
            case ALERT_PLAYED:
                alerter.playedAlarm();
                break;
            default:
                break;
        }
    }

    private void updateText() {
        if (isAmbient()) {
            updater.updateAmbientText();
        } else {
            updater.updateNormalText();
        }
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        currentUpdateScheduler.cancelAll();
        currentUpdateScheduler = ambientUpdateScheduler;
        currentUpdateScheduler.scheduleNextUpdate();
        background.setVisibility(View.GONE);
        buttonAnimator.hideInstantly();
        setAmbientText();
        updateText();
    }

    private void setNormalText() {
        currentTime.setTextColor(normalTextColor);
        elapsed.setTextColor(normalTextColor);
        played.setTextColor(normalTextColor);
        stoppages.setTextColor(normalTextColor);
        for (View label : labels) {
            label.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
        currentUpdateScheduler.cancelAll();
        currentUpdateScheduler = normalUpdateScheduler;
        currentUpdateScheduler.scheduleNextUpdate();
        background.setVisibility(View.VISIBLE);
        setNormalText();
        updateText();
    }

    private void setAmbientText() {
        currentTime.setTextColor(ambientTextColor);
        elapsed.setTextColor(ambientTextColor);
        played.setTextColor(ambientTextColor);
        stoppages.setTextColor(ambientTextColor);
        for (View label : labels) {
            label.setVisibility(View.GONE);
        }
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateText();
    }

    private void toggleButtons() {
        buttonAnimator.setState(matchTimer.getState());
        buttonAnimator.toggle();
    }
}
