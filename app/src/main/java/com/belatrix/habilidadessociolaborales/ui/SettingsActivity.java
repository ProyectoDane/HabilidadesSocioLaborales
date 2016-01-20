package com.belatrix.habilidadessociolaborales.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.belatrix.habilidadessociolaborales.R;
import com.belatrix.habilidadessociolaborales.common.BaseActivity;
import com.belatrix.habilidadessociolaborales.datamodel.Scenario;
import com.belatrix.habilidadessociolaborales.datamodel.User;
import com.belatrix.habilidadessociolaborales.datamodelmanagers.UserManager;
import com.belatrix.habilidadessociolaborales.utils.DataUpdateHelper;
import com.belatrix.habilidadessociolaborales.utils.LoadingIndicatorHelper;
import com.belatrix.habilidadessociolaborales.utils.TextToSpeechHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class SettingsActivity extends BaseActivity {

    private long userId,sessionId;

    private TextView mLastUpdateValueTextView = null;
    private TextView soundTextView;
    private ImageButton soundImageButton;

    private DataUpdateHelper mDataUpdateHelper = null;
    private SimpleDateFormat mSimpleDateFormat = null;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        userId = getIntent().getExtras().getLong("userid");
        sessionId = getIntent().getExtras().getLong("sessionid");

        initializeUI();
        populateData();
    }

    private void initializeUI(){
        mLastUpdateValueTextView = (TextView)findViewById(R.id.lastUpdateValueTextView);
        soundTextView = (TextView) findViewById(R.id.soundTextView);
        soundImageButton = (ImageButton) findViewById(R.id.soundButton);

        mUser = UserManager.getInstance(this).getUser(userId);
        if(mUser.getSound()) {
            soundTextView.setText(getResources().getString(R.string.sound_off));
            soundImageButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_lock_silent_mode_off));
        } else {
            soundTextView.setText(getResources().getString(R.string.sound_on));
            soundImageButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_lock_silent_mode));
        }
    }

    private void populateData(){
        if (mDataUpdateHelper == null || mSimpleDateFormat == null){
            mDataUpdateHelper = new DataUpdateHelper(this);
            mSimpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            mSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT-3:00"));
        }

        Date updated = new Date(mDataUpdateHelper.getLastTimeUpdated());
        mLastUpdateValueTextView.setText(mSimpleDateFormat.format(updated));
    }

    public void updateData(View v){
        updateDataFromServer();
    }

    public void statistics(View v) {
        Intent i = new Intent(SettingsActivity.this,StatisticsActivity.class);
        i.putExtra("userid",userId);
        i.putExtra("sessionid",sessionId);
        startActivity(i);
    }

    public void aboutBelatrix(View view){
        Uri uri = Uri.parse("http://www.belatrixsf.com/");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void sound(View view) {
        if(mUser.getSound()) {
            soundTextView.setText(getResources().getString(R.string.sound_on));
            soundImageButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_lock_silent_mode));
            Toast.makeText(this,"Sonido desactivado",Toast.LENGTH_LONG).show();
        } else {
            soundTextView.setText(getResources().getString(R.string.sound_off));
            soundImageButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_lock_silent_mode_off));
            Toast.makeText(this, "Sonido activado", Toast.LENGTH_LONG).show();
        }
        mUser.setSound(!mUser.getSound());
        UserManager.getInstance(this).updateUser(mUser);
        TextToSpeechHelper.enabled = mUser.getSound();
    }

    private void updateDataFromServer(){
        LoadingIndicatorHelper.getInstance().showIndicator(this);
        mDataUpdateHelper.updateData(new DataUpdateHelper.UpdateRequestFinished() {
            @Override
            public void onRequestFinished(List<Scenario> scenarios) {
                populateData();
                LoadingIndicatorHelper.getInstance().hideIndicator(SettingsActivity.this);
                Toast.makeText(SettingsActivity.this,"Datos actualizados correctamente",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Exception e) {
                LoadingIndicatorHelper.getInstance().hideIndicator(SettingsActivity.this);
                Toast.makeText(SettingsActivity.this,"Se produjo un error, por favor vuelva a intentar",Toast.LENGTH_LONG).show();
            }
        });
    }
}
