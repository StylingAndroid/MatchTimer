package com.stylingandroid.matchtimer;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

class ButtonAnimator {
    private static final int DURATION = 200;
    private final ImageView toggleStateButton;
    private final ImageView resetStateButton;
    private final View buttonBackground;
    private final ViewGroup parent;

    private boolean areShowing = false;

    ButtonAnimator(ImageView toggleStateButton, ImageView resetStateButton, View buttonBackground, ViewGroup parent) {
        this.toggleStateButton = toggleStateButton;
        this.resetStateButton = resetStateButton;
        this.buttonBackground = buttonBackground;
        this.parent = parent;
    }

    void toggle() {
        if (areShowing) {
            hide();
        } else {
            show();
        }
    }

    void hideInstantly() {
        areShowing = false;
        float delta = buttonBackground.getHeight();
        if (delta == 0f) {
            ViewTreeObserver viewTreeObserver = parent.getViewTreeObserver();
            viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    ViewTreeObserver viewTreeObserver = parent.getViewTreeObserver();
                    viewTreeObserver.removeOnPreDrawListener(this);
                    float delta = buttonBackground.getHeight();
                    offsetViews(delta);
                    return false;
                }
            });
        } else {
            offsetViews(delta);
        }
    }

    void offsetViews(float offset) {
        buttonBackground.setTranslationY(offset);
        toggleStateButton.setTranslationY(offset);
        resetStateButton.setTranslationY(offset);
    }

    private void hide() {
        areShowing = false;
        float delta = buttonBackground.getHeight();
        animateView(buttonBackground, delta);
        animateView(toggleStateButton, delta);
        animateView(resetStateButton, delta);
    }

    private void show() {
        areShowing = true;
        animateView(buttonBackground, 0);
        animateView(toggleStateButton, 0);
        animateView(resetStateButton, 0);
    }

    private void animateView(View view, float value) {
        view.animate().translationY(value).setDuration(DURATION).start();
    }

    void setState(TimerState state) {
        switch (state) {
            case PLAYING:
                toggleStateButton.setImageResource(R.drawable.ic_pause);
                resetStateButton.setImageResource(R.drawable.ic_stop);
                break;
            case PAUSED:
                toggleStateButton.setImageResource(R.drawable.ic_play);
                resetStateButton.setImageResource(R.drawable.ic_stop);
                break;
            default:
                toggleStateButton.setImageResource(R.drawable.ic_play);
                resetStateButton.setImageResource(R.drawable.ic_reset);
        }
    }

    boolean isVisible() {
        return areShowing;
    }
}
