package com.belatrix.habilidadessociolaborales.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.belatrix.habilidadessociolaborales.R;
import com.belatrix.habilidadessociolaborales.datamodel.Log;
import com.belatrix.habilidadessociolaborales.datamodel.Question;
import com.belatrix.habilidadessociolaborales.datamodel.Scenario;
import com.belatrix.habilidadessociolaborales.datamodel.Session;
import com.belatrix.habilidadessociolaborales.datamodelmanagers.LogManager;
import com.belatrix.habilidadessociolaborales.datamodelmanagers.ScenarioManager;
import com.belatrix.habilidadessociolaborales.utils.TextToSpeechHelper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QuestionPageFragment extends Fragment {

    private ViewGroup mRootView;

    private Session mSession;
    private Scenario mScenario;
    private Question mQuestion;
    private int mQuestionOrder;

    private OnQuestionAnsweredListener mOnQuestionAnsweredListener;
    private OnPageMoveListener mOnPageMoveListener;

    private TextView mQuestionTextView = null;
    private TextView mPreviousQuestionTextView = null;
    private TextView mNextQuestionTextView = null;
    private ListView mAnswersListView = null;
    private RelativeLayout mQuestionRelativeLayoutSpeakerButton;
    private ImageView mQuestionSpeakerButton;

    private RelativeLayout mBackBtnBg;
    private RelativeLayout mNextBtnBg;
    private ImageView mNextBtnBgImageView;
    private ImageView mBackBtnBgImageView;

    private int mContextualColor = -1;

    public void setScenario(Scenario scenario, int questionOrder) {
        mScenario = scenario;
        mQuestionOrder = questionOrder;
        mQuestion = mScenario.getQuestions().get(mQuestionOrder);
    }

    public void setSession(Session session) {
        mSession = session;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_question_page, container, false);

        initializeUI();
        populateData();

        mQuestionSpeakerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextToSpeechHelper textToSpeechHelper = TextToSpeechHelper.getInstance(getActivity());
                textToSpeechHelper.readText(mQuestionTextView);
            }
        });

        if(TextToSpeechHelper.enabled) {
            mQuestionSpeakerButton.setVisibility(View.VISIBLE);
        } else {
            mQuestionSpeakerButton.setVisibility(View.INVISIBLE);
        }

        mNextBtnBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnPageMoveListener.onNextPage();
            }
        });

        mBackBtnBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnPageMoveListener.onPreviousPage();
            }
        });

        return mRootView;
    }

    private void initializeUI() {
        mQuestionTextView = (TextView) mRootView.findViewById(R.id.questionTextView);
        mPreviousQuestionTextView = (TextView) mRootView.findViewById(R.id.previousQuestionTextView);
        mNextQuestionTextView = (TextView) mRootView.findViewById(R.id.nextQuestionTextView);
        mNextBtnBgImageView = (ImageView) mRootView.findViewById(R.id.nextBtnBgImageView);
        mBackBtnBgImageView = (ImageView) mRootView.findViewById(R.id.backBtnBgImageView);
        mAnswersListView = (ListView) mRootView.findViewById(R.id.answersListView);
        mQuestionRelativeLayoutSpeakerButton = (RelativeLayout) mRootView.findViewById(R.id.questionRelativeLayoutSpeakerButton);
        mQuestionSpeakerButton = (ImageView) mRootView.findViewById(R.id.questionSpeakerButton);

        mNextBtnBg = (RelativeLayout) mRootView.findViewById(R.id.nextBtnBg);
        mBackBtnBg = (RelativeLayout) mRootView.findViewById(R.id.backBtnBg);

        if(mQuestionOrder == 0) {
            mBackBtnBg.setVisibility(View.INVISIBLE);
            mPreviousQuestionTextView.setVisibility(View.INVISIBLE);
            mNextBtnBg.setVisibility(View.VISIBLE);
            mNextQuestionTextView.setVisibility(View.VISIBLE);
        } else if (mQuestionOrder == mScenario.getQuestions().size()-1) {
            mNextBtnBg.setVisibility(View.INVISIBLE);
            mNextQuestionTextView.setVisibility(View.INVISIBLE);
            mBackBtnBg.setVisibility(View.VISIBLE);
            mPreviousQuestionTextView.setVisibility(View.VISIBLE);
        }
    }

    private void populateData() {
        if (mQuestion != null) {
            if (mContextualColor == -1) {
                mContextualColor = getActivity().getResources().getColor(
                        ScenarioManager.getInstance(getActivity()).getContextualColorForScenario(mQuestion.getScenario().getId()));
            }

            mQuestionTextView.setText(mQuestion.getQuestion());
            mQuestionTextView.setBackgroundColor(mContextualColor);
            mPreviousQuestionTextView.setText(String.format("%d/%d",(mQuestionOrder + 1),mScenario.getQuestions().size()));
            mQuestionRelativeLayoutSpeakerButton.setBackgroundColor(mContextualColor);

            mNextQuestionTextView.setTextColor(mContextualColor);
            mPreviousQuestionTextView.setTextColor(mContextualColor);

            setContextualColorToButtons(mQuestion.getScenario().getId().intValue());

            String[] answersArr = {mQuestion.getValidAnswer(), mQuestion.getInvalidAnswer1(), mQuestion.getInvalidAnswer2(), mQuestion.getInvalidAnswer3()};

            List<String> answers = Arrays.asList(answersArr);
            Collections.shuffle(answers);

            AnswersArrayAdapter adapter = new AnswersArrayAdapter(getActivity(), answers, true);

            mAnswersListView.setAdapter(adapter);
        }
    }

    public class AnswersArrayAdapter extends ArrayAdapter<String> {
        private Context mContext;
        boolean enabled = true;

        public AnswersArrayAdapter(Context context, List<String> answers, boolean enabled) {
            super(context, R.layout.item_answer, answers);
            this.mContext = context;
            this.enabled = enabled;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_answer, null);

                AnswerItemViewHolder viewHolder = new AnswerItemViewHolder();
                viewHolder.answerValueTextView = (TextView) convertView.findViewById(R.id.answerTextView);
                viewHolder.answerStatusImageView = (ImageView) convertView.findViewById(R.id.answerStatusImageView);
                viewHolder.answerSpeakerImageView = (ImageView) convertView.findViewById(R.id.itemAnswerSpeakerButton);

                switch (mQuestion.getScenario().getId().intValue()) {
                    case 1:
                        viewHolder.answerSpeakerImageView.setImageResource(R.drawable.speaker_green);
                        break;
                    case 2:
                        viewHolder.answerSpeakerImageView.setImageResource(R.drawable.speaker_magenta);
                        break;
                    case 3:
                        viewHolder.answerSpeakerImageView.setImageResource(R.drawable.speaker_blue);
                        break;
                    case 4:
                        viewHolder.answerSpeakerImageView.setImageResource(R.drawable.speaker_purple);
                        break;
                    case 5:
                        viewHolder.answerSpeakerImageView.setImageResource(R.drawable.speaker_green);
                        break;
                    case 6:
                        viewHolder.answerSpeakerImageView.setImageResource(R.drawable.speaker_magenta);
                        break;
                }

                if (TextToSpeechHelper.enabled) {
                    viewHolder.answerSpeakerImageView.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.answerSpeakerImageView.setVisibility(View.INVISIBLE);
                }

                convertView.setTag(viewHolder);
            }

            final AnswerItemViewHolder holder = (AnswerItemViewHolder) convertView.getTag();
            final String currentAnswer = getItem(position);

            holder.answerValueTextView.setText(currentAnswer);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (areAllItemsEnabled()) {
                        if (holder.answerValueTextView.getText().equals(mQuestion.getValidAnswer())) {

                            holder.answerValueTextView.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.answer_correct_bg));
                            holder.answerStatusImageView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.right_answer));

                            mOnQuestionAnsweredListener.onValidAnswer(currentAnswer);
                            setEnabled(false);
                        } else {
                            holder.answerValueTextView.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.answer_incorrect_bg));
                            holder.answerStatusImageView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.wrong_answer));
                            mOnQuestionAnsweredListener.onWrongAnswer(currentAnswer);
                        }
                    }
                }
            });

            holder.answerValueTextView.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.answer_nr_bg));
            holder.answerStatusImageView.setImageDrawable(null);

            List<Log> logs = LogManager.getInstance(mContext).fetchLogsByQuestion(mSession.getId(), mScenario.getId(), mQuestion.getId());

            if(logs != null) {
                for(int i = 0;i < logs.size();i++) {
                    if(logs.get(i).getAnswered().equals(currentAnswer)) {
                        if(logs.get(i).getIsCorrect()) {
                            holder.answerValueTextView.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.answer_correct_bg));
                            holder.answerStatusImageView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.right_answer));
                            setEnabled(false);
                        } else {
                            holder.answerValueTextView.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.answer_incorrect_bg));
                            holder.answerStatusImageView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.wrong_answer));
                        }
                    }
                }
            }

            final TextView answerValueTextView = holder.answerValueTextView;
            final ImageView answerSpeakerImageView = holder.answerSpeakerImageView;

            answerSpeakerImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextToSpeechHelper textToSpeechHelper = TextToSpeechHelper.getInstance(getContext());
                    textToSpeechHelper.readText(answerValueTextView);
                }
            });
            convertView.setEnabled(enabled);

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        public void setEnabled (boolean enabled) {
            this.enabled = enabled;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return enabled;
        }
    }

    public void setOnQuestionAnsweredListener(OnQuestionAnsweredListener onQuestionAnsweredListener) {
        mOnQuestionAnsweredListener = onQuestionAnsweredListener;
    }

    static class AnswerItemViewHolder {
        public TextView answerValueTextView;
        public ImageView answerStatusImageView;
        public ImageView answerSpeakerImageView;
    }

    public interface OnQuestionAnsweredListener {
        void onValidAnswer(final String answer);
        void onWrongAnswer(final String answer);
    }

    public interface OnPageMoveListener {
        void onNextPage();
        void onPreviousPage();
    }

    private void setContextualColorToButtons(int scenarioId) {
        switch (scenarioId) {
            case 1:
                mQuestionSpeakerButton.setImageResource(R.drawable.speaker_rounded_green);
                mNextBtnBgImageView.setBackgroundResource(R.drawable.round_btn_green);
                mBackBtnBgImageView.setBackgroundResource(R.drawable.round_btn_green);
                break;
            case 2:
                mQuestionSpeakerButton.setImageResource(R.drawable.speaker_rounded_magenta);
                mNextBtnBgImageView.setBackgroundResource(R.drawable.round_btn_magenta);
                mBackBtnBgImageView.setBackgroundResource(R.drawable.round_btn_magenta);
                break;
            case 3:
                mQuestionSpeakerButton.setImageResource(R.drawable.speaker_rounded_blue);
                mNextBtnBgImageView.setBackgroundResource(R.drawable.round_btn_blue);
                mBackBtnBgImageView.setBackgroundResource(R.drawable.round_btn_blue);
                break;
            case 4:
                mQuestionSpeakerButton.setImageResource(R.drawable.speaker_rounded_purple);
                mNextBtnBgImageView.setBackgroundResource(R.drawable.round_btn_purple);
                mBackBtnBgImageView.setBackgroundResource(R.drawable.round_btn_purple);
                break;
            case 5:
                mQuestionSpeakerButton.setImageResource(R.drawable.speaker_rounded_green);
                mNextBtnBgImageView.setBackgroundResource(R.drawable.round_btn_green);
                mBackBtnBgImageView.setBackgroundResource(R.drawable.round_btn_green);
                break;
            case 6:
                mQuestionSpeakerButton.setImageResource(R.drawable.speaker_rounded_magenta);
                mNextBtnBgImageView.setBackgroundResource(R.drawable.round_btn_magenta);
                mBackBtnBgImageView.setBackgroundResource(R.drawable.round_btn_magenta);
                break;
        }
    }

    public void setOnPageMoveListener(OnPageMoveListener mOnPageMoveListener) {
        this.mOnPageMoveListener = mOnPageMoveListener;
    }
}
