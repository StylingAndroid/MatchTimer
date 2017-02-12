package com.stylingandroid.matchtimer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class MatchTimerTest {

    private MatchTimer matchTimer;
    private long now;

    @Mock MatchTimerState state;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        now = System.currentTimeMillis();

        matchTimer = new MatchTimer(state);
    }

    @Test
    public void givenANewMatchTimer_whenWeCallIsStarted_thenFalseIsReturned() {

        boolean actual = matchTimer.isStarted();

        assertThat(actual).isFalse();
    }

    @Test
    public void givenANewMatchTimer_whenWeCallIsStopped_thenTrueIsReturned() {

        boolean actual = matchTimer.isStopped();

        assertThat(actual).isTrue();
    }

    @Test
    public void givenANewMatchTimer_whenWeCallIsPaused_thenFalseIsReturned() {

        boolean actual = matchTimer.isPaused();

        assertThat(actual).isFalse();
    }

    @Test
    public void givenAStartedMatchTimer_whenWeCallIsStarted_thenTrueIsReturned() {
        when(state.getStart()).thenReturn(now);
        when(state.getStop()).thenReturn(0L);
        when(state.getPauseStart()).thenReturn(0L);

        boolean actual = matchTimer.isStarted();

        assertThat(actual).isTrue();
    }

    @Test
    public void givenAStartedMatchTimer_whenWeCallIsStopped_thenFalseIsReturned() {
        when(state.getStart()).thenReturn(now);
        when(state.getStop()).thenReturn(0L);
        when(state.getPauseStart()).thenReturn(0L);

        boolean actual = matchTimer.isStopped();

        assertThat(actual).isFalse();
    }

    @Test
    public void givenAStartedMatchTimer_whenWeCallIsPaused_thenFalseIsReturned() {
        matchTimer.start();

        boolean actual = matchTimer.isPaused();

        assertThat(actual).isFalse();
    }

    @Test
    public void givenAPausedMatchTimer_whenWeCallIsStarted_thenFalseIsReturned() {
        matchTimer.start();
        matchTimer.pause();

        boolean actual = matchTimer.isStarted();

        assertThat(actual).isFalse();
    }

    @Test
    public void givenAPausedMatchTimer_whenWeCallIsStopped_thenFalseIsReturned() {
        when(state.getStart()).thenReturn(now - 1000L);
        when(state.getStop()).thenReturn(0L);
        when(state.getPauseStart()).thenReturn(now);

        matchTimer.pause();

        boolean actual = matchTimer.isStopped();

        assertThat(actual).isFalse();
    }

    @Test
    public void givenAPausedMatchTimer_whenWeCallIsPaused_thenTrueIsReturned() {
        when(state.getStart()).thenReturn(now - 1000L);
        when(state.getStop()).thenReturn(0L);
        when(state.getPauseStart()).thenReturn(now);

        matchTimer.pause();

        boolean actual = matchTimer.isPaused();

        assertThat(actual).isTrue();
    }

    @Test
    public void givenAStoppedMatchTimer_whenWeCallIsStarted_thenFalseIsReturned() {
        matchTimer.start();
        matchTimer.stop();

        boolean actual = matchTimer.isStarted();

        assertThat(actual).isFalse();
    }

    @Test
    public void givenAStoppedMatchTimer_whenWeCallIsStopped_thenTrueIsReturned() {
        matchTimer.start();
        matchTimer.stop();

        boolean actual = matchTimer.isStopped();

        assertThat(actual).isTrue();
    }

    @Test
    public void givenAStoppedMatchTimer_whenWeCallIsPaused_thenFalseIsReturned() {
        matchTimer.start();
        matchTimer.stop();

        boolean actual = matchTimer.isPaused();

        assertThat(actual).isFalse();
    }

    @Test
    public void givenAStoppedMatchTimer_whenWeToggleState_thenTheNewStateIsStarted() {
        when(state.getStart()).thenReturn(0L);
        when(state.getStop()).thenReturn(0L);
        when(state.getPauseStart()).thenReturn(0L);

        matchTimer.toggleState();

        verify(state).setStart(anyLong());
    }

    @Test
    public void givenAStoppedMatchTimer_whenWeToggleState_thenTheNewStateIsNotPaused() {
        matchTimer.toggleState();

        boolean actual = matchTimer.isPaused();

        assertThat(actual).isFalse();
    }

    @Test
    public void givenAStartedMatchTimer_whenWeToggleState_thenTheNewStateIsNotStarted() {
        matchTimer.start();
        matchTimer.toggleState();

        boolean actual = matchTimer.isStarted();

        assertThat(actual).isFalse();
    }

    @Test
    public void givenAStartedMatchTimer_whenWeToggleState_thenTheNewStateIsPaused() {
        when(state.getStart()).thenReturn(now);
        when(state.getStop()).thenReturn(0L);
        when(state.getPauseStart()).thenReturn(0L);

        matchTimer.toggleState();

        verify(state).setPauseStart(anyLong());
    }

    @Test
    public void givenAStartedMatchTimer_whenWeToggleState_thenTheNewStateIsNotStopped() {
        when(state.getStart()).thenReturn(now);
        when(state.getStop()).thenReturn(0L);
        when(state.getPauseStart()).thenReturn(0L);

        matchTimer.toggleState();

        verify(state, never()).setStop(anyLong());
    }

    @Test
    public void givenAPausedMatchTimer_whenWeToggleState_thenTheNewStateIsStarted() {
        when(state.getStart()).thenReturn(now - 100L);
        when(state.getStop()).thenReturn(0L);
        when(state.getPauseStart()).thenReturn(now);

        matchTimer.toggleState();

        verify(state).setPauseStart(eq(0L));
    }

    @Test
    public void givenAPausedMatchTimer_whenWeToggleState_thenThePausedDurationIsUpdated() {
        when(state.getStart()).thenReturn(now - 100L);
        when(state.getStop()).thenReturn(0L);
        when(state.getPauseStart()).thenReturn(now);

        matchTimer.toggleState();

        verify(state).addPaused(anyLong());
    }

    @Test
    public void givenAPausedMatchTimer_whenWeToggleState_thenTheNewStateIsNotPaused() {
        matchTimer.start();
        matchTimer.pause();
        matchTimer.toggleState();

        boolean actual = matchTimer.isPaused();

        assertThat(actual).isFalse();
    }

    @Test
    public void givenAPausedMatchTimer_whenWeToggleState_thenTheNewStateIsNotStopped() {
        when(state.getStart()).thenReturn(now - 1000L);
        when(state.getStop()).thenReturn(0L);
        when(state.getPauseStart()).thenReturn(now);

        matchTimer.toggleState();

        boolean actual = matchTimer.isStopped();

        assertThat(actual).isFalse();
    }

    @Test
    public void givenAStartedMatchTimer_whenWeResetState_thenTheNewStateIsNotStarted() {
        matchTimer.start();
        matchTimer.resetState();

        boolean actual = matchTimer.isStarted();

        assertThat(actual).isFalse();
    }

    @Test
    public void givenAStartedMatchTimer_whenWeResetState_thenTheNewStateIsNotPaused() {
        matchTimer.start();
        matchTimer.resetState();

        boolean actual = matchTimer.isPaused();

        assertThat(actual).isFalse();
    }

    @Test
    public void givenAStartedMatchTimer_whenWeResetState_thenTheNewStateIsStopped() {
        when(state.getStart()).thenReturn(now);
        when(state.getStop()).thenReturn(0L);
        when(state.getPauseStart()).thenReturn(0L);

        matchTimer.resetState();

        verify(state).setStop(anyLong());
    }

    @Test
    public void givenAPausedMatchTimer_whenWeResetState_thenTheNewStateIsNotStarted() {
        matchTimer.start();
        matchTimer.pause();
        matchTimer.resetState();

        boolean actual = matchTimer.isStarted();

        assertThat(actual).isFalse();
    }

    @Test
    public void givenAPausedMatchTimer_whenWeResetState_thenTheNewStateIsResumed() {
        when(state.getStart()).thenReturn(now - 1000L);
        when(state.getStop()).thenReturn(0L);
        when(state.getPauseStart()).thenReturn(now);

        matchTimer.resetState();

        verify(state).setPauseStart(eq(0L));
    }

    @Test
    public void givenAStoppedMatchTimer_whenWeResetState_thenTheNewStateIsNotStarted() {
        matchTimer.start();
        matchTimer.stop();
        matchTimer.resetState();

        boolean actual = matchTimer.isStarted();

        assertThat(actual).isFalse();
    }

    @Test
    public void givenAStoppedMatchTimer_whenWeResetState_thenTheNewStateIsNotPaused() {
        matchTimer.start();
        matchTimer.stop();
        matchTimer.resetState();

        boolean actual = matchTimer.isPaused();

        assertThat(actual).isFalse();
    }

    @Test
    public void givenAStoppedMatchTimer_whenWeResetState_thenTheNewStateIsStopped() {
        matchTimer.start();
        matchTimer.stop();
        matchTimer.resetState();

        boolean actual = matchTimer.isStopped();

        assertThat(actual).isTrue();
    }

    @Test
    public void givenARunningMatchTimer_whenWeRequestTheState_thenPlayingIsReturned() {
        when(state.getStart()).thenReturn(now);
        when(state.getStop()).thenReturn(0L);
        when(state.getPauseStart()).thenReturn(0L);

        TimerState timerState = matchTimer.getState();

        assertThat(timerState).isEqualTo(TimerState.PLAYING);
    }

    @Test
    public void givenAPausedMatchTimer_whenWeRequestTheState_thenPausedIsReturned() {
        when(state.getStart()).thenReturn(now - 1000L);
        when(state.getStop()).thenReturn(0L);
        when(state.getPauseStart()).thenReturn(now);

        TimerState timerState = matchTimer.getState();

        assertThat(timerState).isEqualTo(TimerState.PAUSED);
    }

    @Test
    public void givenAStoppedMatchTimer_whenWeRequestTheState_thenStoppedIsReturned() {
        when(state.getStart()).thenReturn(now - 1000L);
        when(state.getStop()).thenReturn(now);
        when(state.getPauseStart()).thenReturn(0L);

        TimerState timerState = matchTimer.getState();

        assertThat(timerState).isEqualTo(TimerState.STOPPED);
    }
}
