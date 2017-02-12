package com.stylingandroid.matchtimer;

import android.view.View;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class NormalUpdateSchedulerTest {

    public NormalUpdateSchedulerTest(long elapsed, long played, long time, long expected) {
        this.elapsed = elapsed;
        this.played = played;
        this.time = time;
        this.expected = expected;
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {1, 2, 3, 998},
                {3, 2, 1, 997},
                {1, 3, 2, 997},
                {2, 3, 1, 997}
        });
    }

    @Mock MatchTimer matchTimer;
    @Mock View view;
    @Mock Updater updater;

    @Captor
    ArgumentCaptor longCaptor;

    private final long elapsed;
    private final long played;
    private final long time;
    private final long expected;

    private NormalUpdateScheduler calculator;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(matchTimer.getElapsed()).thenReturn(elapsed);
        when(matchTimer.getPlayed()).thenReturn(played);
        when(matchTimer.getCurrentTime()).thenReturn(time);

        calculator = new NormalUpdateScheduler(matchTimer, view, updater);
    }

    @Test
    public void getNextUpdateOffset() throws Exception {
        calculator.scheduleNextUpdate();

        verify(view).postDelayed(any(Runnable.class), eq(expected));
    }
}
