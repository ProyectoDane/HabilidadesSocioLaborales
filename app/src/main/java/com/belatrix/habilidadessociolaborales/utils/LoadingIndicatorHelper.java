package com.belatrix.habilidadessociolaborales.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.belatrix.habilidadessociolaborales.R;

public class LoadingIndicatorHelper {

    private static LoadingIndicatorHelper instance = null;

    private LoadingIndicatorHelper() {
    }

    public static LoadingIndicatorHelper getInstance() {
        if (instance == null) instance = new LoadingIndicatorHelper();

        return instance;
    }

    private void showIndicator(final Activity activity, float offsetY, float offsetX) {

        ViewGroup mainView = (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);

        View loadingIndicator = mainView.findViewById(R.id.loading_indicator);


        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(View.VISIBLE);

        } else {

            LayoutInflater inflater = LayoutInflater.from(activity);
            loadingIndicator = inflater.inflate(R.layout.loading_indicator, null);
            mainView.addView(loadingIndicator);
        }

        ProgressBar progressBar = (ProgressBar) mainView.findViewById(R.id.progressBar);

        if (progressBar.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) progressBar.getLayoutParams();
            p.setMargins(getPixelsFromDp(activity, offsetX), getPixelsFromDp(activity, offsetY), 0, 0);
            progressBar.requestLayout();
        }

        loadingIndicator.bringToFront();

    }

    public void showIndicator(final Activity activity) {
        showIndicator(activity, 0, 0);
    }

    public void hideIndicator(Activity activity) {
        ViewGroup mainView = (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);
        View loadingIndicator = mainView.findViewById(R.id.loading_indicator);
        if (loadingIndicator != null) loadingIndicator.setVisibility(View.GONE);
    }

    private int getPixelsFromDp(Context context, float dp) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

}
