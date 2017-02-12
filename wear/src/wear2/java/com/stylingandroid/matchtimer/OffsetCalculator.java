package com.stylingandroid.matchtimer;

import java.util.concurrent.TimeUnit;

import static android.text.format.DateUtils.MINUTE_IN_MILLIS;
import static android.text.format.DateUtils.SECOND_IN_MILLIS;

final class OffsetCalculator {

    long getMillisToNextMinuteBoundary(long time) {
        return MINUTE_IN_MILLIS - getMinuteMillisRemainder(time);
    }

    private long getMinuteMillisRemainder(long time) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
        return TimeUnit.MILLISECONDS.toMillis(time) - TimeUnit.MINUTES.toMillis(minutes);
    }

    long getMillisToNextSecondBoundary(long time) {
        return SECOND_IN_MILLIS - getSecondMillisRemainder(time);
    }

    private long getSecondMillisRemainder(long time) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(time);
        return TimeUnit.MILLISECONDS.toMillis(time) - TimeUnit.SECONDS.toMillis(seconds);
    }

}
