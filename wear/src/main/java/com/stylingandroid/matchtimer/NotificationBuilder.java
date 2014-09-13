package com.stylingandroid.matchtimer;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

public class NotificationBuilder {
    private static final int ID_ACTIVITY = 1;
    private static final int ID_START = 2;
    private static final int ID_STOP = 3;
    private static final int ID_PAUSE = 4;
    private static final int ID_RESUME = 5;
    private static final int ID_RESET = 6;

    private final Context context;
    private final MatchTimer matchTimer;

    public NotificationBuilder(Context context, MatchTimer matchTimer) {
        this.context = context;
        this.matchTimer = matchTimer;
    }

    public Notification buildNotification() {
        Intent activityIntent = new Intent(context, MatchTimerNotificationActivity.class);
        PendingIntent activityPendingIntent = PendingIntent.getActivity(context, ID_ACTIVITY, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        NotificationCompat.WearableExtender extender = new NotificationCompat.WearableExtender();
        extender.setBackground(BitmapFactory.decodeResource(context.getResources(), R.drawable.bkg_football));
        extender.setDisplayIntent(activityPendingIntent);
        boolean ongoing = true;
        if (matchTimer.isPaused()) {
            buildPausedActions(extender);
        } else if (matchTimer.isRunning()) {
            buildRunningActions(extender);
        } else {
            buildStoppedActions(extender);
            ongoing = false;
        }
        builder.setContentTitle(buildNotificationTitle(matchTimer))
                .setSmallIcon(R.drawable.ic_football)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setOngoing(ongoing);
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.extend(extender);

        return builder.build();
    }

    private void buildStoppedActions(NotificationCompat.WearableExtender extender) {
        PendingIntent startPendingIntent = PendingIntent.getBroadcast(context, ID_START,
                MatchTimerReceiver.START_INTENT, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent resetPendingIntent = PendingIntent.getBroadcast(context, ID_RESET,
                MatchTimerReceiver.RESET_INTENT, PendingIntent.FLAG_UPDATE_CURRENT);
        extender.addAction(new NotificationCompat.Action.Builder(R.drawable.ic_play, "Start", startPendingIntent).build());
        extender.addAction(new NotificationCompat.Action.Builder(R.drawable.ic_reset, "Reset", resetPendingIntent).build());
    }

    private void buildRunningActions(NotificationCompat.WearableExtender extender) {
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(context, ID_PAUSE,
                MatchTimerReceiver.PAUSE_INTENT, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, ID_STOP,
                MatchTimerReceiver.STOP_INTENT, PendingIntent.FLAG_UPDATE_CURRENT);
        extender.addAction(new NotificationCompat.Action.Builder(R.drawable.ic_pause, "Pause", pausePendingIntent).build());
        extender.addAction(new NotificationCompat.Action.Builder(R.drawable.ic_stop, "Stop", stopPendingIntent).build());
    }

    private void buildPausedActions(NotificationCompat.WearableExtender extender) {
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, ID_STOP,
                MatchTimerReceiver.STOP_INTENT, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent resumePendingIntent = PendingIntent.getBroadcast(context, ID_RESUME,
                MatchTimerReceiver.RESUME_INTENT, PendingIntent.FLAG_UPDATE_CURRENT);
        extender.addAction(new NotificationCompat.Action.Builder(R.drawable.ic_play, "Resume", resumePendingIntent).build());
        extender.addAction(new NotificationCompat.Action.Builder(R.drawable.ic_stop, "Stop", stopPendingIntent).build());
    }

    private String buildNotificationTitle(MatchTimer timer) {
        if (timer.isPaused()) {
            return context.getString(R.string.title_paused, timer.getElapsedMinutes());
        } else if (timer.isRunning()) {
            return context.getString(R.string.title_running, timer.getElapsedMinutes());
        }
        return context.getString(R.string.title_stopped);
    }

}
