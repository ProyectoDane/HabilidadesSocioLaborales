package com.belatrix.habilidadessociolaborales.common;

import android.support.v4.app.FragmentActivity;

import com.belatrix.habilidadessociolaborales.HabilidadesSocioLaboralesApp;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public abstract class BaseActivity extends FragmentActivity {

    private static Tracker sTracker = null;

    @Override
    protected void onStart() {
        super.onStart();

        if (sTracker == null){
            sTracker = ((HabilidadesSocioLaboralesApp)getApplication()).getTracker();
        }

        sTracker.setScreenName(this.getClass().getSimpleName());
        sTracker.send(new HitBuilders.AppViewBuilder().build());
    }
}
