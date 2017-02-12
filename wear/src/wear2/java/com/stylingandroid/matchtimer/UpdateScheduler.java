package com.stylingandroid.matchtimer;

interface UpdateScheduler {
    void cancelAll();
    void scheduleNextUpdate();
}
