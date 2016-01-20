package com.belatrix.habilidadessociolaborales;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class HabilidadesSocioLaboralesApp extends Application {

    private static Tracker sGATracker = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public synchronized Tracker getTracker() {

        if (sGATracker == null){
            sGATracker = GoogleAnalytics.getInstance(this).newTracker(R.xml.ga_tracker);
            sGATracker.enableExceptionReporting(true);
            sGATracker.enableAdvertisingIdCollection(true);
            sGATracker.enableAutoActivityTracking(true);
        }

        return sGATracker;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
