package com.stylingandroid.matchtimer;

import android.app.Activity;
import android.os.Bundle;

public class MatchTimerWearActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MatchTimerReceiver.setUpdate(this);
        finish();
    }
}
