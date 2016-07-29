package com.belatrix.habilidadessociolaborales.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.belatrix.habilidadessociolaborales.R;
import com.belatrix.habilidadessociolaborales.datamodel.Scenario;
import com.belatrix.habilidadessociolaborales.utils.TextToSpeechHelper;

import java.util.List;

public class ScenarioArrayAdapter extends ArrayAdapter<Scenario> {

    private int mColor;
    private int mNeutralColor;//always white?
    private long mCurrentScenarioId;

    public ScenarioArrayAdapter(Context context, List<Scenario> objects, int color, long currentScenario) {
        super(context, R.layout.item_list_scenario, objects);
        mColor = color;
        mCurrentScenarioId = currentScenario;
        mNeutralColor = context.getResources().getColor(android.R.color.white);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            rowView = LayoutInflater.from(getContext()).inflate(R.layout.item_list_scenario, null);

            ScenarioItemViewHolder viewHolder = new ScenarioItemViewHolder();
            viewHolder.rowView = rowView;
            viewHolder.scenarioNameTextView = (TextView) rowView.findViewById(R.id.scenarioListItem);
            viewHolder.itemListSpeakerButton = (ImageView) rowView.findViewById(R.id.itemListSpeakerButton);

            rowView.setTag(viewHolder);
        }

        ScenarioItemViewHolder holder = (ScenarioItemViewHolder) rowView.getTag();

        Scenario currentScenario = getItem(position);

        holder.scenarioNameTextView.setText(currentScenario.getName());

        if (currentScenario.getId() == mCurrentScenarioId) {//if current scenario is the selected one
            holder.scenarioNameTextView.setBackgroundColor(mColor);
            holder.scenarioNameTextView.setTextColor(mNeutralColor);
        } else {
            holder.scenarioNameTextView.setBackgroundColor(mNeutralColor);
            holder.scenarioNameTextView.setTextColor(mColor);
        }

        switch ((int) mCurrentScenarioId) {
            case 1:
                holder.itemListSpeakerButton.setImageResource(R.drawable.speaker_rounded_green);
                break;
            case 2:
                holder.itemListSpeakerButton.setImageResource(R.drawable.speaker_rounded_magenta);
                break;
            case 3:
                holder.itemListSpeakerButton.setImageResource(R.drawable.speaker_rounded_blue);
                break;
            case 4:
                holder.itemListSpeakerButton.setImageResource(R.drawable.speaker_rounded_purple);
                break;
            case 5:
                holder.itemListSpeakerButton.setImageResource(R.drawable.speaker_rounded_green);
                break;
            case 6:
                holder.itemListSpeakerButton.setImageResource(R.drawable.speaker_rounded_magenta);
                break;
        }

        if(TextToSpeechHelper.enabled) {
            holder.itemListSpeakerButton.setVisibility(View.VISIBLE);
        } else {
            holder.itemListSpeakerButton.setVisibility(View.INVISIBLE);
        }

        final TextView scenarioNameTextView = holder.scenarioNameTextView;

        holder.itemListSpeakerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextToSpeechHelper textToSpeechHelper = TextToSpeechHelper.getInstance(getContext());
                textToSpeechHelper.readText(scenarioNameTextView);
            }
        });



        return rowView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    static class ScenarioItemViewHolder {
        public TextView scenarioNameTextView;
        public ImageView itemListSpeakerButton;
        public View rowView;
    }
}