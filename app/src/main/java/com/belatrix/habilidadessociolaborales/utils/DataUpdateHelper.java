package com.belatrix.habilidadessociolaborales.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.belatrix.habilidadessociolaborales.common.C;
import com.belatrix.habilidadessociolaborales.datamodel.Question;
import com.belatrix.habilidadessociolaborales.datamodel.Scenario;
import com.belatrix.habilidadessociolaborales.datamodelmanagers.ScenarioManager;
import com.belatrix.habilidadessociolaborales.internet.RestClient;

import java.util.Date;
import java.util.List;

public class DataUpdateHelper {

    public final static String TAG = DataUpdateHelper.class.getSimpleName();

    private Context mContext = null;

    private boolean mScenarioRequestFinished = false;
    private boolean mQuestionsRequestFinished = false;

    private RestClient mRestClient = null;

    private UpdateRequestFinished mHandler = null;

    private GDriveJsonParser mJsonParser;

    private int mRemainingScenarioRequests = -1;
    private int mRemainingQuestionRequests = -1;

    public DataUpdateHelper(Context context){
        mContext = context;
        mJsonParser = new GDriveJsonParser();
    }

    public long getLastTimeUpdated(){
        return PreferencesHelper.getInstance(mContext).getPreferences().getLong(C.PREFERENCES_LAST_TIME_DATABASE_UPDATED, 0L);
    }

    public void updateData(UpdateRequestFinished handler){
        mHandler = handler;
        mRestClient = RestClient.getInstance(mContext);
        mRestClient.get(C.SCENARIO_URL, null, new RestClient.SyncHandler() {
            @Override
            public void onSuccess(Object result) {
                Log.d(TAG, "Request finished...");
                String serverResult = result.toString();
                mJsonParser.parseScenariosJson(serverResult);
                Log.d(TAG, "Scenarios parsed. Downloading scenario images");
                downloadScenarioImages(mJsonParser.getScenarios());
            }

            @Override
            public void onFailure(Error error) {
                Log.d(TAG, "Error: " + error.getMessage());
            }
        });

        mRestClient.get(C.QUESTIONS_URL, null, new RestClient.SyncHandler() {
            @Override
            public void onSuccess(Object result) {
                Log.d(TAG, "Request finished...");
                String serverResult = result.toString();
                mJsonParser.parseQuestionsJson(serverResult);
                Log.d(TAG, "Questions parsed. Downloading question images");
                downloadQuestionImages(mJsonParser.getQuestions());
            }

            @Override
            public void onFailure(Error error) {
                Log.d(TAG, "Error: " + error.getMessage());
            }
        });
        Log.d(TAG, "Request started...");
    }

    private void checkUpdateCompleted(){
        if (mScenarioRequestFinished && mQuestionsRequestFinished){
            List<Scenario> scenarios = mJsonParser.getScenarios();
            List<Question> questions = mJsonParser.getQuestions();

            ScenarioManager mScenarioManager = ScenarioManager.getInstance(mContext);

            mScenarioManager.saveScenariosAndQuestions(scenarios, questions);

            PreferencesHelper.getInstance(mContext).savePreference(C.PREFERENCES_LAST_TIME_DATABASE_UPDATED, new Date());

            mHandler.onRequestFinished(mJsonParser.getScenarios());
        }else{
            Log.d(TAG, "Checking update (scenario - questions) " + mScenarioRequestFinished + " - " + mQuestionsRequestFinished);
        }
    }

    private void downloadScenarioImages(List<Scenario> scenarios){
        mRemainingScenarioRequests = scenarios.size();
        for (final Scenario scenario : scenarios) {
            if (scenario.getImageUrl() != null && !scenario.getImageUrl().equals("")) {
                mRestClient.getImage(scenario.getImageUrl(), new RestClient.ImageHandler() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        //TODO blur image before saving it
                        //bitmap = ImageUtils.getInstance(mContext).getBlurredBitmap(bitmap);

                        scenario.setImage(ImageUtils.getInstance(mContext).bitmapToByteArray(bitmap));
                        mRemainingScenarioRequests--;
                        Log.d(TAG, "   One scenario requests less. Remaining: " + mRemainingScenarioRequests);
                        if (mRemainingScenarioRequests == 0) {
                            mScenarioRequestFinished = true;
                            checkUpdateCompleted();
                        }
                    }

                    @Override
                    public void onErrorResponse(Exception e) {
                        Log.e(TAG, "Error: " + e + " for scenario " + scenario.getName());
                        e.printStackTrace();
                        mRemainingScenarioRequests--;
                        if (mRemainingScenarioRequests == 0) {
                            mScenarioRequestFinished = true;
                            checkUpdateCompleted();
                        }
                    }
                });
            }else{
                mRemainingScenarioRequests--;
                if (mRemainingScenarioRequests == 0) {
                    mScenarioRequestFinished = true;
                    checkUpdateCompleted();
                }
            }
        }
    }

    private void downloadQuestionImages(List<Question> questions) {
        mRemainingQuestionRequests = questions.size();
        for (final Question question : questions) {
            if (question.getImageUrl() != null && !question.getImageUrl().equals("")) {
                mRestClient.getImage(question.getImageUrl(), new RestClient.ImageHandler() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        //bitmap = ImageUtils.getInstance(mContext).getBlurredBitmap(bitmap);
                        question.setImage(ImageUtils.getInstance(mContext).bitmapToByteArray(bitmap));
                        mRemainingQuestionRequests--;
                        Log.d(TAG, "   One question requests less. Remaining: " + mRemainingQuestionRequests);
                        if (mRemainingQuestionRequests == 0) {
                            mQuestionsRequestFinished = true;
                            checkUpdateCompleted();
                        }
                    }

                    @Override
                    public void onErrorResponse(Exception e) {
                        Log.e(TAG, "Error: " + e);
                        e.printStackTrace();
                        mRemainingQuestionRequests--;
                        if (mRemainingQuestionRequests == 0) {
                            mQuestionsRequestFinished = true;
                            checkUpdateCompleted();
                        }
                    }
                });
            }else{
                mRemainingQuestionRequests--;
                if (mRemainingQuestionRequests == 0) {
                    mQuestionsRequestFinished = true;
                    checkUpdateCompleted();
                }
            }
        }
    }

    public interface UpdateRequestFinished{
        void onRequestFinished(List<Scenario> scenarios);
        void onError(Exception e);
    }
}
