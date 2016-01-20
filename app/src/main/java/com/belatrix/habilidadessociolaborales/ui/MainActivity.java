package com.belatrix.habilidadessociolaborales.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.belatrix.habilidadessociolaborales.R;
import com.belatrix.habilidadessociolaborales.common.BaseActivity;
import com.belatrix.habilidadessociolaborales.common.C;
import com.belatrix.habilidadessociolaborales.datamodel.Scenario;
import com.belatrix.habilidadessociolaborales.datamodel.Session;
import com.belatrix.habilidadessociolaborales.datamodel.User;
import com.belatrix.habilidadessociolaborales.datamodelmanagers.LogManager;
import com.belatrix.habilidadessociolaborales.datamodelmanagers.ScenarioManager;
import com.belatrix.habilidadessociolaborales.datamodelmanagers.SessionManager;
import com.belatrix.habilidadessociolaborales.datamodelmanagers.UserManager;
import com.belatrix.habilidadessociolaborales.utils.ImageUtils;
import com.belatrix.habilidadessociolaborales.utils.ProgressImageView;
import com.belatrix.habilidadessociolaborales.utils.TextToSpeechHelper;

import java.util.Calendar;
import java.util.List;


public class MainActivity extends BaseActivity {

    private long userId, sessionId;

    private User mCurrentUser;

    private LinearLayout mLinearLayoutScenariosContainer;
    private ImageView mSelectScenarioSpeakerButton;
    private TextView mSelectScenarioTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userId = getIntent().getExtras().getLong("userid");
        sessionId = getIntent().getExtras().getLong("sessionid");

        mCurrentUser = UserManager.getInstance(this).getUser(userId);

        TextToSpeechHelper.enabled = mCurrentUser.getSound();

        initializeUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadScenarios();
    }

    public void settingsClick(View v) {
        Intent i = new Intent(MainActivity.this, SettingsActivity.class);
        i.putExtra("userid", userId);
        i.putExtra("sessionid", sessionId);
        startActivity(i);
    }

    public void logoutClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.app_name));
        builder.setMessage(String.format("Seguro desea cerrar la sesión del usuario %s?",mCurrentUser.getName()));
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Session session = SessionManager.getInstance(MainActivity.this).fetchSessionById(sessionId);
                session.setEndDate(Calendar.getInstance().getTime());
                session.setClosed(true);
                SessionManager.getInstance(MainActivity.this).updateSession(session);
                finish();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void initializeUI() {
        RelativeLayout mLandingLayout = (RelativeLayout) findViewById(R.id.landing);
        mLandingLayout.setBackgroundDrawable(ImageUtils.getInstance(this).getBlurredImage(R.drawable.carpentry, 3f));

        mLinearLayoutScenariosContainer = (LinearLayout) findViewById(R.id.linearLayoutScenariosContainer);

        TextView mUserTextView = (TextView) findViewById(R.id.userNameTextView);

        mUserTextView.setText(mCurrentUser.getName());

        mSelectScenarioTitleTextView = (TextView) findViewById(R.id.selectScenarioTitleTextView);
        mSelectScenarioSpeakerButton = (ImageView) findViewById(R.id.selectScenarioSpeakerButton);

        mSelectScenarioSpeakerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextToSpeechHelper textToSpeechHelper = TextToSpeechHelper.getInstance(MainActivity.this);
                textToSpeechHelper.readText(mSelectScenarioTitleTextView);
            }
        });

        ImageView mAvatarFrameImageView = (ImageView) findViewById(R.id.avatarFrameImageView);
        if (mCurrentUser.getImage() != null) {
            mAvatarFrameImageView.setImageBitmap(BitmapFactory.decodeByteArray(mCurrentUser.getImage(), 0, mCurrentUser.getImage().length));
        } else {
            mAvatarFrameImageView.setImageDrawable(getResources().getDrawable(R.drawable.test_avatar));
        }
        mAvatarFrameImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
                intent.putExtra("userid", userId);
                intent.putExtra("sessionid", sessionId);
                startActivity(intent);
            }
        });
    }

    private void loadScenarios() {
        if(TextToSpeechHelper.enabled) {
            mSelectScenarioSpeakerButton.setVisibility(View.VISIBLE);
        } else {
            mSelectScenarioSpeakerButton.setVisibility(View.GONE);
        }

        List<Scenario> scenarios;
        ScenarioManager scenarioManager = ScenarioManager.getInstance(this);
        try {
            scenarios = scenarioManager.fetchScenarios();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_reading_database), Toast.LENGTH_LONG).show();
            scenarios = null;
        }

        if (mLinearLayoutScenariosContainer.getChildCount() > 0) {
            mLinearLayoutScenariosContainer.removeAllViews();
        }

        if (scenarios != null) {
            for (final Scenario scenario : scenarios) {
                final ViewGroup scenarioView = (ViewGroup) getLayoutInflater().inflate(R.layout.item_scenario, null, false);
                ((TextView) scenarioView.findViewById(R.id.scenarioTitleTextView)).setText(scenario.getName());
                if (scenario.getBitmapImage(this) != null) {
                    ((ImageView) scenarioView.findViewById(R.id.scenarioPictureImageView)).setImageDrawable(new BitmapDrawable(getResources(), ImageUtils.getInstance(this).getBlurredBitmap(scenario.getBitmapImage(this))));
                }

                // Speaker Button implementation
                final TextView scenarioTitleView = (TextView) scenarioView.findViewById(R.id.scenarioTitleTextView);

                ImageView speakerButtonImageView = (ImageView) scenarioView.findViewById(R.id.scenarioItemSpeakerButton);

                switch (scenario.getId().intValue()) {
                    case 1:
                        speakerButtonImageView.setImageResource(R.drawable.speaker_rounded_green);
                        break;
                    case 2:
                        speakerButtonImageView.setImageResource(R.drawable.speaker_rounded_magenta);
                        break;
                    case 3:
                        speakerButtonImageView.setImageResource(R.drawable.speaker_rounded_blue);
                        break;
                    case 4:
                        speakerButtonImageView.setImageResource(R.drawable.speaker_rounded_purple);
                        break;
                    case 5:
                        speakerButtonImageView.setImageResource(R.drawable.speaker_rounded_green);
                        break;
                    case 6:
                        speakerButtonImageView.setImageResource(R.drawable.speaker_rounded_magenta);
                        break;
                }

                speakerButtonImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextToSpeechHelper textToSpeechHelper = TextToSpeechHelper.getInstance(MainActivity.this);
                        textToSpeechHelper.readText(scenarioTitleView);
                    }
                });

                if(TextToSpeechHelper.enabled) {
                    speakerButtonImageView.setVisibility(View.VISIBLE);
                } else {
                    speakerButtonImageView.setVisibility(View.INVISIBLE);
                }

                (scenarioView.findViewById(R.id.scenarioColorLayout)).setBackgroundColor(getResources().getColor(scenarioManager.getContextualColorForScenario(scenario.getId())));

                RelativeLayout scenarioItem = ((RelativeLayout) scenarioView.findViewById(R.id.scenarioItem));
                scenarioItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openScenario(scenario);
                    }
                });

                ProgressImageView scenarioProgressImageView = (ProgressImageView) scenarioView.findViewById(R.id.scenarioProgressImageView);
                scenarioProgressImageView.setVisibility(View.VISIBLE);
                scenarioProgressImageView.setColor(getResources().getColor(scenarioManager.getContextualColorForScenario(scenario.getId())));

                int total = scenarioManager.getNumQuestionsForScenario(scenario);
                int correct = LogManager.getInstance(MainActivity.this).getCorrectAnswersByScenario(scenario,sessionId);

                if(total != 0) {
                    int percentage = correct * 100 / total;
                    scenarioProgressImageView.setPercentage(percentage);
                } else {
                    scenarioProgressImageView.setPercentage(0);
                }
                mLinearLayoutScenariosContainer.addView(scenarioView);
            }
        }
    }

    private void openScenario(Scenario scenario) {
        Intent i = new Intent(MainActivity.this, QuestionActivity.class);
        i.putExtra(C.EXTRA_SCENARIO_ID, scenario.getId());
        i.putExtra("userid",userId);
        i.putExtra("sessionid",sessionId);
        startActivity(i);
    }

}
