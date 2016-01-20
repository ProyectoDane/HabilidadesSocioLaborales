package com.belatrix.habilidadessociolaborales.ui.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.belatrix.habilidadessociolaborales.R;
import com.belatrix.habilidadessociolaborales.common.BaseActivity;
import com.belatrix.habilidadessociolaborales.datamodelmanagers.UserManager;
import com.belatrix.habilidadessociolaborales.utils.TextToSpeechHelper;

/**
 * Created by magatsu on 4/1/2016.
 */
public class AboutBelatrixActivity extends BaseActivity{

    private Button mVisitUsButton;
    private Button mCloseDialogButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_activity_about_belatrix);
        mVisitUsButton = (Button) findViewById(R.id.visitUsButton);
        mCloseDialogButton = (Button) findViewById(R.id.closeDialogButton);

        mCloseDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mVisitUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://www.belatrixsf.com/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

    }
}
