package com.belatrix.habilidadessociolaborales.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.belatrix.habilidadessociolaborales.R;
import com.belatrix.habilidadessociolaborales.datamodel.Log;
import com.belatrix.habilidadessociolaborales.datamodelmanagers.ScenarioManager;

import java.text.SimpleDateFormat;
import java.util.List;

public class LogAdapter  extends ArrayAdapter<Log> {

    private Context mContext;
    private List<Log> mLogsList;

    public LogAdapter(Context context, List<Log> list) {
        super(context, R.layout.item_list_log, list);
        this.mContext = context;
        this.mLogsList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LogViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_log, null);
            viewHolder = new LogViewHolder();

            viewHolder.questionTextView = (TextView) convertView.findViewById(R.id.questionTextView);
            viewHolder.correctAnswerTextView = (TextView) convertView.findViewById(R.id.correctAnswerTextView);
            viewHolder.answerTextView = (TextView) convertView.findViewById(R.id.answerTextView);
            viewHolder.scenarioTextView = (TextView) convertView.findViewById(R.id.scenarioTextView);
            viewHolder.dateTextView = (TextView) convertView.findViewById(R.id.dateTextView);
            viewHolder.correct = (ImageView) convertView.findViewById(R.id.correctImageView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (LogViewHolder) convertView.getTag();
        }

        viewHolder.questionTextView.setText(mLogsList.get(position).getQuestion());
        viewHolder.correctAnswerTextView.setText(mLogsList.get(position).getAnswer());
        viewHolder.answerTextView.setText(mLogsList.get(position).getAnswered());
        viewHolder.scenarioTextView.setText(mLogsList.get(position).getScenarioName());

        viewHolder.dateTextView.setText(new SimpleDateFormat("dd-MM-yyyy HH:mm").format(mLogsList.get(position).getDate()));

        if(mLogsList.get(position).getScenarioId() != null) {
            viewHolder.scenarioTextView.setTextColor(mContext.getResources().getColor(ScenarioManager.getInstance(mContext).getContextualColorForScenario(mLogsList.get(position).getScenarioId())));
        } else {
            viewHolder.scenarioTextView.setTextColor(mContext.getResources().getColor(android.R.color.white));
        }

        if(mLogsList.get(position).getAnswer() != null && mLogsList.get(position).getAnswered() != null) {
            if (mLogsList.get(position).getAnswer().equals(mLogsList.get(position).getAnswered())) {
                viewHolder.correct.setImageDrawable(mContext.getResources().getDrawable(R.drawable.right_answer));
            } else {
                viewHolder.correct.setImageDrawable(mContext.getResources().getDrawable(R.drawable.wrong_answer));
            }
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return mLogsList.size();
    }

    static class LogViewHolder {
        public TextView questionTextView;
        public TextView correctAnswerTextView;
        public TextView answerTextView;
        public ImageView correct;
        public TextView dateTextView;
        public TextView scenarioTextView;
    }
}
