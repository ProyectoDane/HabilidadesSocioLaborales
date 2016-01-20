package com.belatrix.habilidadessociolaborales.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.belatrix.habilidadessociolaborales.datamodel.Scenario;
import com.belatrix.habilidadessociolaborales.datamodel.Session;
import com.belatrix.habilidadessociolaborales.ui.QuestionPageFragment;

public class QuestionsPagerAdapter extends FragmentStatePagerAdapter {

    private Session mSession;
    private Scenario mScenario = null;

    private QuestionPageFragment.OnQuestionAnsweredListener mOnQuestionAnsweredListener;

    private QuestionPageFragment.OnPageMoveListener mOnPageMoveListener;

    public QuestionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setSession(Session session) {
        mSession = session;
    }

    public void setScenario(Scenario scenario) {
        mScenario = scenario;
    }

    public void setOnQuestionAnsweredListener(QuestionPageFragment.OnQuestionAnsweredListener onQuestionAnsweredListener) {
        mOnQuestionAnsweredListener = onQuestionAnsweredListener;
    }

    public void setOnPageMoveListener(QuestionPageFragment.OnPageMoveListener onPageMoveListener) {
        mOnPageMoveListener = onPageMoveListener;
    }

    @Override
    public Fragment getItem(int position) {
        QuestionPageFragment fragment = new QuestionPageFragment();

        fragment.setSession(mSession);
        fragment.setScenario(mScenario, position);
        fragment.setOnQuestionAnsweredListener(mOnQuestionAnsweredListener);
        fragment.setOnPageMoveListener(mOnPageMoveListener);

        return fragment;
    }

    @Override
    public int getCount() {
        return mScenario.getQuestions().size();
    }
}
