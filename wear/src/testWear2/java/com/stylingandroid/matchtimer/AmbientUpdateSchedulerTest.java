package com.stylingandroid.matchtimer;

import android.app.AlarmManager;
import android.app.PendingIntent;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class AmbientUpdateSchedulerTest {

    @Parameters(name = "elapsed = {0}; played = {1}; time = {2}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {1, 2, 59998},
                {2, 1, 59998}
        });
    }

    @Mock
    MatchTimer matchTimer;

    @Mock
    AlarmManager alarmManager;

    @Mock
    PendingIntent uiUpdatePendingIntent;


    private final long elapsed;
    private final long played;
    private final long expected;

    private AmbientUpdateScheduler calculator;

    public AmbientUpdateSchedulerTest(long elapsed, long played, long expected) {
        this.elapsed = elapsed;
        this.played = played;
        this.expected = expected;
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(matchTimer.getElapsed()).thenReturn(elapsed);
        when(matchTimer.getPlayed()).thenReturn(played);

        calculator = new AmbientUpdateScheduler(matchTimer, alarmManager, uiUpdatePendingIntent);
    }

    @Test
    public void getNextUpdateOffset() throws Exception {
        calculator.scheduleNextUpdate();

        verify(alarmManager).setExact(eq(AlarmManager.RTC_WAKEUP), eq(expected), eq(uiUpdatePendingIntent));
    }
}
