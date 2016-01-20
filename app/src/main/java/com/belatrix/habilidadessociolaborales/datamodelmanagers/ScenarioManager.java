package com.belatrix.habilidadessociolaborales.datamodelmanagers;

import android.content.Context;
import android.util.Log;

import com.belatrix.habilidadessociolaborales.R;
import com.belatrix.habilidadessociolaborales.datamodel.Question;
import com.belatrix.habilidadessociolaborales.datamodel.QuestionDao;
import com.belatrix.habilidadessociolaborales.datamodel.Scenario;
import com.belatrix.habilidadessociolaborales.datamodel.ScenarioDao;

import java.util.ArrayList;
import java.util.List;

public class ScenarioManager extends BaseManager {

    public final static String TAG = ScenarioManager.class.getSimpleName();

    private static ScenarioManager sInstance = null;
    private ScenarioDao mScenarioDao;
    private QuestionDao mQuestionDao;

    public final static long MAX_COLOR_QTY = 4;

    private ScenarioManager(Context context) {
        super(context);
        mScenarioDao = getDaoSession().getScenarioDao();
        mQuestionDao = getDaoSession().getQuestionDao();
    }

    public static ScenarioManager getInstance(Context pContext) {
        if (sInstance == null) {
            sInstance = new ScenarioManager(pContext);
        }
        return sInstance;
    }

    public ArrayList<Scenario> fetchScenarios() {
        return (ArrayList<Scenario>) mScenarioDao.queryBuilder().orderAsc(ScenarioDao.Properties.Id).list();
    }

    public Scenario fetchScenarioById(Long scenarioId) {
        return mScenarioDao.load(scenarioId);
    }

    /*
    public ArrayList<Question> fetchQuestionsForScenario(long scenarioId) {

        ArrayList<Question> questions = (ArrayList<Question>) mQuestionDao.queryBuilder().where(
                QuestionDao.Properties.ScenarioId.eq(scenarioId)).orderAsc(QuestionDao.Properties.Id).list();

        return questions;
    }

    public long saveScenario(Scenario scenario) {
        return mScenarioDao.insertOrReplace(scenario);
    }*/

    public int getNumQuestionsForScenario(Scenario scenario) {
        return mQuestionDao._queryScenario_Questions(scenario.getId()).size();
    }

    public List<Scenario> saveScenariosAndQuestions(final List<Scenario> scenarios, final List<Question> questions) {

        //Log.d(TAG, "Saving scenarios and questions...");
        getDaoSession().runInTx(new Runnable() {
            @Override
            public void run() {
                if (scenarios != null) {
                    //Log.d(TAG, "   Saving scenarios");
                    for (Scenario scenario : scenarios) {
                        mScenarioDao.insertOrReplace(scenario);
                    }
                } else {
                    Log.d(TAG, "   Scenarios are null");
                }

                if (questions != null) {
                    //Log.d(TAG, "   Saving questions");
                    for (Question question : questions) {
                        mQuestionDao.insertOrReplace(question);
                    }
                } else {
                    Log.d(TAG, "   Questions are null");
                }
            }
        });
        return scenarios;
    }

    public int getContextualColorForScenario(long scenarioId) {
        long color = scenarioId % MAX_COLOR_QTY;
        int colorId = android.R.color.holo_red_dark;

        switch ((int) color) {
            case 0:
                colorId = R.color.purple;
                break;
            case 1:
                colorId = R.color.green;
                break;
            case 2:
                colorId = R.color.magenta;
                break;
            case 3:
                colorId = R.color.blue;
                break;
        }
        return colorId;
    }

    public long getNumScenarios() {
        return mScenarioDao.queryBuilder().buildCount().count();
    }
}
