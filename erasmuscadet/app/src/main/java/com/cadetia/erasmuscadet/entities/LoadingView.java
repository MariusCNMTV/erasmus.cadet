package com.cadetia.erasmuscadet.entities;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.RawRes;

import com.airbnb.lottie.LottieAnimationView;
import com.cadetia.erasmuscadet.R;

public class LoadingView extends FrameLayout {

    private LottieAnimationView loadingAnimationView;
    private View darkOverlay;

    public LoadingView(Context context) {
        super(context);
        init(context);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.loading_animation, this, true);

        loadingAnimationView = findViewById(R.id.loadingAnimationView);
        darkOverlay = findViewById(R.id.darkOverlay);
    }

    public void startLoadingAnimation(@RawRes int animationRes, boolean useDarkOverlay) {
        darkOverlay.bringToFront();
        loadingAnimationView.bringToFront();
        loadingAnimationView.setAnimation(animationRes);
        loadingAnimationView.playAnimation();
        loadingAnimationView.setVisibility(View.VISIBLE);

        if (useDarkOverlay) {
            darkOverlay.setVisibility(View.VISIBLE);
        } else {
            darkOverlay.setVisibility(View.GONE);
        }

        setVisibility(View.VISIBLE);
    }

    public void stopLoadingAnimation() {
        loadingAnimationView.cancelAnimation();
        loadingAnimationView.setVisibility(View.GONE);
        darkOverlay.setVisibility(View.GONE);
        setVisibility(View.GONE);
    }}