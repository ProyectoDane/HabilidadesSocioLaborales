package com.belatrix.habilidadessociolaborales.ui;

import android.content.Intent;
import android.os.Bundle;

import com.belatrix.habilidadessociolaborales.R;
import com.belatrix.habilidadessociolaborales.common.BaseActivity;
import com.belatrix.habilidadessociolaborales.datamodel.Scenario;
import com.belatrix.habilidadessociolaborales.utils.DataUpdateHelper;
import com.belatrix.habilidadessociolaborales.utils.LoadingIndicatorHelper;

import java.util.List;

public class SplashScreenActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        initializeUI();
    }

    private void initializeUI() {
        DataUpdateHelper dataUpdateHelper = new DataUpdateHelper(this);

        if (dataUpdateHelper.getLastTimeUpdated() == 0L) { //update from service if there are not previous update
            LoadingIndicatorHelper.getInstance().showIndicator(this);
            dataUpdateHelper.updateData(new DataUpdateHelper.UpdateRequestFinished() {
                @Override
                public void onRequestFinished(List<Scenario> scenarios) {
                    LoadingIndicatorHelper.getInstance().hideIndicator(SplashScreenActivity.this);
                    goToMainActivity();
                }

                @Override
                public void onError(Exception e) {
                    LoadingIndicatorHelper.getInstance().hideIndicator(SplashScreenActivity.this);
                    e.printStackTrace();
                }
            });
        } else {
            goToMainActivity();
        }
    }

    private void goToMainActivity() {
        Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
        startActivity(i);
    }
}
