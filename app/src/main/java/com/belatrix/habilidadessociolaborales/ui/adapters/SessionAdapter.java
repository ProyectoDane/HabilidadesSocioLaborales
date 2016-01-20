package com.belatrix.habilidadessociolaborales.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.belatrix.habilidadessociolaborales.R;
import com.belatrix.habilidadessociolaborales.datamodel.Scenario;
import com.belatrix.habilidadessociolaborales.datamodel.Session;
import com.belatrix.habilidadessociolaborales.datamodelmanagers.LogManager;
import com.belatrix.habilidadessociolaborales.datamodelmanagers.ScenarioManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SessionAdapter extends ArrayAdapter<Session> {

    public static DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

    private Context mContext;
    private List<Session> mSessionList;

    public SessionAdapter(Context context, List<Session> list) {
        super(context, R.layout.item_list_session, list);
        this.mContext = context;
        this.mSessionList = list;
    }

    @Override
    public int getCount() {
        return mSessionList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Session session = mSessionList.get(position);

        SessionViewHolder holder;

        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_session,null);

            holder = new SessionViewHolder();

            holder.sessionTextView = (TextView) convertView.findViewById(R.id.sessionTextView);
            holder.statusTextView = (TextView) convertView.findViewById(R.id.statusTextView);
            holder.endDateTextView = (TextView) convertView.findViewById(R.id.endDateTextView);
            holder.percentageTextView = (TextView) convertView.findViewById(R.id.percentageTextView);

            convertView.setTag(holder);
        } else {
            holder = (SessionViewHolder) convertView.getTag();
        }

        holder.sessionTextView.setText(String.format("Sesión del %s",df.format(session.getStartDate())));

        if (session.getClosed()) {
            holder.statusTextView.setText(mContext.getResources().getString(R.string.finished));
        } else {
            holder.statusTextView.setText(mContext.getResources().getString(R.string.in_progress));
        }

        if(session.getEndDate() != null) {
            holder.endDateTextView.setText(df.format(session.getEndDate()));
        } else {
            holder.endDateTextView.setText("-");
        }

        ArrayList<Scenario> scenarios = ScenarioManager.getInstance(mContext).fetchScenarios();

        int total = 0, correct=0;

        for(int i=0;i<scenarios.size();i++){
            android.util.Log.i("aaaa","i"+i);
            try {
                total += ScenarioManager.getInstance(mContext).getNumQuestionsForScenario(scenarios.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
            correct += LogManager.getInstance(mContext).getCorrectAnswersByScenario(scenarios.get(i), session.getId());
        }

        if(total != 0) {
            int percentage = correct * 100 / total;
            holder.percentageTextView.setText(String.format("%d %%",percentage));
        } else {
            holder.percentageTextView.setText("0 %");
        }

        return convertView;
    }

    static class SessionViewHolder {
        public TextView sessionTextView;
        public TextView statusTextView;
        public TextView endDateTextView;
        public TextView percentageTextView;
    }
}
