package com.nosad.sample.engine.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nosad.sample.R;
import com.nosad.sample.entity.Quest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class is responsible for quests display in a list.
 *
 * Created by Novosad on 4/4/16.
 */
public class QuestsAdapter extends ArrayAdapter<Quest> {
    // Couple test quests
    private ArrayList<Quest> quests = new ArrayList<>(Arrays.asList(
            new Quest(1, "Fitness", "Walk 4 km during 1 hour.", 100, 10),
            new Quest(2, "Invasion", "Capture a monument.", 50, 20)
    ));
    private Context context;
    private int resource;

    public QuestsAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
    }

    public QuestsAdapter(Context context, int resource, List<Quest> objects) {
        this(context, resource);
        this.quests = new ArrayList<>(objects);
    }

    @Override
    public int getCount() {
        return quests.size();
    }

    @Override
    public Quest getItem(int position) {
        return quests.get(position);
    }

    @Override
    public int getPosition(Quest item) {
        int index = -1;

        for (int i = 0; i < quests.size(); i++) {
            if (quests.get(i).equals(item)) {
                index = i;
                break;
            }
        }

        return index;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Quest quests = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvQuestTitle);
        tvTitle.setText(quests.getTitle());

        TextView tvDescription = (TextView) convertView.findViewById(R.id.tvQuestDescription);
        tvDescription.setText(quests.getDescription());

        TextView tvExperience = (TextView) convertView.findViewById(R.id.tvExpAmountForQuest);
        tvExperience.setText(String.valueOf(quests.getExperience()));

        TextView tvCoins = (TextView) convertView.findViewById(R.id.tvCoinsAmountForQuest);
        tvCoins.setText(String.valueOf(quests.getCredits()));

        return convertView;
    }
}
