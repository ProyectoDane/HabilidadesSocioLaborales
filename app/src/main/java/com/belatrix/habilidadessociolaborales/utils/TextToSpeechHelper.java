package com.belatrix.habilidadessociolaborales.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

public class TextToSpeechHelper {

    public static boolean enabled = false;

    private static TextToSpeechHelper sInstance = null;
    private static Context sContext = null;

    private TextToSpeech mTextToSpeech = null;

    private TextToSpeechHelper() {
        initializeTTS();
    }

    public static TextToSpeechHelper getInstance(Context context) {
        sContext = context;
        if (sInstance == null) {
            sInstance = new TextToSpeechHelper();
        }

        return sInstance;
    }

    private void initializeTTS() {
        mTextToSpeech = new TextToSpeech(sContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                mTextToSpeech.setLanguage(new Locale("ES","AR"));
            }
        });
    }

    public void readText(String textToBeRead) {
        if (!enabled) return;
        mTextToSpeech.speak(textToBeRead, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void readText(View v) {
        if (!enabled) return;

        String textToRead = "";
        if (v instanceof TextView) {
            textToRead = ((TextView) v).getText().toString();
        } /*else if (v instanceof EditText) {
            textToRead = ((EditText) v).getText().toString();
        }*/

        if (!"".equals(textToRead)) {
            readText(textToRead.toLowerCase());
        }
    }

/*    public void terminate() {
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }
    }*/
}
