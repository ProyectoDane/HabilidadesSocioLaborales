package com.belatrix.habilidadessociolaborales.utils;

import android.util.Log;

import com.belatrix.habilidadessociolaborales.datamodel.Question;
import com.belatrix.habilidadessociolaborales.datamodel.Scenario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GDriveJsonParser {

    private final static String TAG = GDriveJsonParser.class.getSimpleName();

    private final static String PREFIX_TO_REMOVE = "gsx$";

    private List<Question> mQuestions = new ArrayList<Question>();
    private List<Scenario> mScenarios = new ArrayList<Scenario>();

    public GDriveJsonParser(){

    }

    public void parseQuestionsJson(String jsonFromServer){
        String jsonString = jsonFromServer.replace(PREFIX_TO_REMOVE, "");
        try {
            JSONObject json = new JSONObject(jsonString);
            JSONArray responseData = json.getJSONObject("response_data").getJSONObject("feed").getJSONArray("entry");

            Log.d(TAG, "Response data: " + responseData.toString());

            if (responseData.length() > 0){
                parseQuestions(responseData);
            }
        } catch (JSONException e){
            Log.w(TAG, "An error occured when parsing json file: " + e);
            e.printStackTrace();
        }
    }

    public void parseScenariosJson(String jsonFromServer) {
        String jsonString = jsonFromServer.replace(PREFIX_TO_REMOVE, "");
        try {
            JSONObject json = new JSONObject(jsonString);
            JSONArray responseData = json.getJSONObject("response_data").getJSONObject("feed").getJSONArray("entry");

            Log.d(TAG, "Response data: " + responseData.toString());

            if (responseData.length() > 0) {
                parseScenarios(responseData);
            }
        } catch (JSONException e) {
            Log.w(TAG, "An error occured when parsing json file: " + e);
            e.printStackTrace();
        }
    }

    private void parseQuestions(JSONArray questions){
        for (int i = 0; i < questions.length(); i++) {
            try {
                JSONObject json = questions.getJSONObject(i);

                Question question = new Question();
                question.setId(json.getJSONObject("questionid").getLong("$t"));
                question.setScenarioId(json.getJSONObject("scenarioid").getLong("$t"));
                question.setQuestion(json.getJSONObject("question").getString("$t").toUpperCase());
                question.setImageUrl(json.getJSONObject("imageurl").getString("$t"));
                question.setValidAnswer(json.getJSONObject("validanswer").getString("$t").toUpperCase());
                question.setInvalidAnswer1(json.getJSONObject("answer1").getString("$t").toUpperCase());
                question.setInvalidAnswer2(json.getJSONObject("answer2").getString("$t").toUpperCase());
                question.setInvalidAnswer3(json.getJSONObject("answer3").getString("$t").toUpperCase());

                mQuestions.add(question);

                Log.d(TAG, "    Adding question: " + question.getQuestion());
            } catch (JSONException e) {
                Log.w(TAG, "An error occurred when parsing question " + i + ": " + e);
                e.printStackTrace();
            }
        }
    }

    private void parseScenarios(JSONArray scenarios){
        for (int i = 0; i < scenarios.length(); i++) {
            try {
                JSONObject json = scenarios.getJSONObject(i);

                Scenario scenario = new Scenario();
                scenario.setId(json.getJSONObject("scenarioid").getLong("$t"));
                scenario.setName(json.getJSONObject("name").getString("$t").toUpperCase());
                scenario.setDependencies(json.getJSONObject("dependencies").getString("$t"));
                scenario.setImageUrl(json.getJSONObject("imageurl").getString("$t"));

                mScenarios.add(scenario);
                Log.d(TAG, "    Adding scenario: " + scenario.getName());
            } catch (JSONException e) {
                Log.w(TAG, "An error occurred when parsing scenario " + i + ": " + e);
                e.printStackTrace();
            }
        }
    }

    public List<Question> getQuestions(){
        return mQuestions;
    }

    public List<Scenario> getScenarios() {
        return mScenarios;
    }
}
