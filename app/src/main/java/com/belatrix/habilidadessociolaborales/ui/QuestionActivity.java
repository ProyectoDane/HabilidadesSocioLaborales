package com.belatrix.habilidadessociolaborales.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.belatrix.habilidadessociolaborales.R;
import com.belatrix.habilidadessociolaborales.common.BaseActivity;
import com.belatrix.habilidadessociolaborales.common.C;
import com.belatrix.habilidadessociolaborales.datamodel.Log;
import com.belatrix.habilidadessociolaborales.datamodel.Question;
import com.belatrix.habilidadessociolaborales.datamodel.Scenario;
import com.belatrix.habilidadessociolaborales.datamodel.Session;
import com.belatrix.habilidadessociolaborales.datamodel.User;
import com.belatrix.habilidadessociolaborales.datamodelmanagers.LogManager;
import com.belatrix.habilidadessociolaborales.datamodelmanagers.ScenarioManager;
import com.belatrix.habilidadessociolaborales.datamodelmanagers.SessionManager;
import com.belatrix.habilidadessociolaborales.datamodelmanagers.UserManager;
import com.belatrix.habilidadessociolaborales.ui.adapters.QuestionsPagerAdapter;
import com.belatrix.habilidadessociolaborales.ui.adapters.ScenarioArrayAdapter;
import com.belatrix.habilidadessociolaborales.utils.ImageUtils;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.Calendar;
import java.util.List;

public class QuestionActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private List<Scenario> mScenarios = null;
    private List<Question> mQuestions = null;

    private ListView mScenariosListView = null;

    private TextView mScenariosNavigatorTitleTextView = null;
    private TextView mUserTextView = null;
    private ImageView mBackImageView = null;

    private RelativeLayout mLandingLayout = null;

    private int mContextualColor;
    private long mCurrentScenarioId;
    private long sessionId;

    private ViewPager mQuestionsViewPager = null;

    private User mCurrentUser;

    private Session mCurrentSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        long userId = getIntent().getExtras().getLong("userid");
        sessionId = getIntent().getExtras().getLong("sessionid");
        mCurrentScenarioId = getIntent().getExtras().getLong(C.EXTRA_SCENARIO_ID);
        mCurrentUser = UserManager.getInstance(this).getUser(userId);
        mCurrentSession = SessionManager.getInstance(this).fetchSessionById(sessionId);

        initializeUI();
        populateData();
    }

    private void initializeUI() {
        mScenariosListView = (ListView) findViewById(R.id.scenariosListView);
        mScenariosNavigatorTitleTextView = (TextView) findViewById(R.id.scenariosNavigatorTitleTextView);
        mBackImageView = (ImageView) findViewById(R.id.backImageView);
        mQuestionsViewPager = (ViewPager) findViewById(R.id.questionsViewPager);

        mLandingLayout = (RelativeLayout) findViewById(R.id.landing);

        mBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mUserTextView = (TextView) findViewById(R.id.userNameTextView);
        mUserTextView.setText(mCurrentUser.getName());

        RoundedImageView mAvatarFrameImageView = (RoundedImageView) findViewById(R.id.avatarFrameImageView);
        if (mCurrentUser.getImage() != null) {
            mAvatarFrameImageView.setImageBitmap(BitmapFactory.decodeByteArray(mCurrentUser.getImage(), 0, mCurrentUser.getImage().length));
        } else {
            mAvatarFrameImageView.setImageDrawable(getResources().getDrawable(R.drawable.test_avatar));
        }
    }

    private void populateData() {
        mContextualColor = getResources().getColor(ScenarioManager.getInstance(this).getContextualColorForScenario(mCurrentScenarioId));
        mScenarios = ScenarioManager.getInstance(this).fetchScenarios();

        mScenariosNavigatorTitleTextView.setTextColor(mContextualColor);

        mUserTextView.setTextColor(mContextualColor);

        //setting background color to arrows
        GradientDrawable backImageBg = (GradientDrawable) getResources().getDrawable(R.drawable.round_btn_green);
        backImageBg.setColor(mContextualColor);
        mBackImageView.setBackgroundDrawable(backImageBg);
        backImageBg.invalidateSelf();

        populateScenarios();
        populateQuestions();
    }

    private void populateScenarios() {
        ScenarioArrayAdapter scenarioArrayAdapter = new ScenarioArrayAdapter(this, mScenarios, mContextualColor, mCurrentScenarioId);
        mScenariosListView.setAdapter(scenarioArrayAdapter);
    }

    private void populateQuestions() {
        final Scenario scenario = ScenarioManager.getInstance(this).fetchScenarioById(mCurrentScenarioId);
        mQuestions = scenario.getQuestions();

        QuestionPageFragment.OnQuestionAnsweredListener mOnQuestionAnsweredListener = new QuestionPageFragment.OnQuestionAnsweredListener() {
            @Override
            public void onValidAnswer(final String answer) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log log = new Log();
                        log.setDate(Calendar.getInstance().getTime());
                        log.setSessionId(sessionId);
                        log.setUser(mCurrentUser.getId());
                        log.setScenarioId(scenario.getId());
                        log.setScenarioName(scenario.getName());
                        log.setQuestion(mQuestions.get(mQuestionsViewPager.getCurrentItem()).getQuestion());
                        log.setQuestionId(mQuestions.get(mQuestionsViewPager.getCurrentItem()).getId());
                        log.setAnswer(mQuestions.get(mQuestionsViewPager.getCurrentItem()).getValidAnswer());
                        log.setAnswered(answer);
                        log.setIsCorrect(true);
                        LogManager.getInstance(QuestionActivity.this).saveLog(log);
                        //mQuestionsViewPager.setCurrentItem(mQuestionsViewPager.getCurrentItem() + 1);
                    }
                }, 1000);
            }

            @Override
            public void onWrongAnswer(final String answer) {
                Log log = new Log();
                log.setDate(Calendar.getInstance().getTime());
                log.setSessionId(sessionId);
                log.setUser(mCurrentUser.getId());
                log.setScenarioId(scenario.getId());
                log.setScenarioName(scenario.getName());
                log.setQuestion(mQuestions.get(mQuestionsViewPager.getCurrentItem()).getQuestion());
                log.setQuestionId(mQuestions.get(mQuestionsViewPager.getCurrentItem()).getId());
                log.setAnswer(mQuestions.get(mQuestionsViewPager.getCurrentItem()).getValidAnswer());
                log.setAnswered(answer);
                log.setIsCorrect(false);
                LogManager.getInstance(QuestionActivity.this).saveLog(log);
            }
        };

        QuestionPageFragment.OnPageMoveListener mOnPageMoveListener = new QuestionPageFragment.OnPageMoveListener() {
            @Override
            public void onNextPage() {
                mQuestionsViewPager.setCurrentItem(mQuestionsViewPager.getCurrentItem() + 1);
            }

            @Override
            public void onPreviousPage() {
                mQuestionsViewPager.setCurrentItem(mQuestionsViewPager.getCurrentItem() - 1);
            }
        };

        QuestionsPagerAdapter mQuestionsPagerAdapter = new QuestionsPagerAdapter(getSupportFragmentManager());
        mQuestionsPagerAdapter.setScenario(scenario);
        mQuestionsPagerAdapter.setSession(mCurrentSession);
        mQuestionsPagerAdapter.setOnQuestionAnsweredListener(mOnQuestionAnsweredListener);
        mQuestionsPagerAdapter.setOnPageMoveListener(mOnPageMoveListener);

        mQuestionsViewPager.setOnPageChangeListener(this);
        mQuestionsViewPager.setAdapter(mQuestionsPagerAdapter);

        setQuestionBackground(0);//setting background for first question
    }

    private void setQuestionBackground(int questionPosition) {
        Question question = mQuestions.get(questionPosition);

        Bitmap background = question.getBitmapImage(this) != null ? question.getBitmapImage(this) : ScenarioManager.getInstance(this).fetchScenarioById(
                mCurrentScenarioId).getBitmapImage(this);
        if (background != null) {
            mLandingLayout.setBackgroundDrawable(
                    new BitmapDrawable(getResources(), ImageUtils.getInstance(this).getBlurredBitmap(background, 3f)));
        } else {
            mLandingLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.test_question_bg));
        }
    }

    public void onPageScrollStateChanged(int arg0) {
    }

    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    public void onPageSelected(int position) {
        setQuestionBackground(position);
    }
}
