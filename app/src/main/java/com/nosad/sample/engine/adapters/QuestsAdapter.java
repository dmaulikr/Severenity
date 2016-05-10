package com.nosad.sample.engine.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nosad.sample.R;
import com.nosad.sample.entity.quest.Quest;

import java.util.ArrayList;
import java.util.List;

/**
 * Class is responsible for quests display in a list.
 *
 * Created by Novosad on 4/4/16.
 */
public class QuestsAdapter extends ArrayAdapter<Quest> {
    // Couple test quests
    private ArrayList<Quest> quests;

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
        Quest quest = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        ImageView ivQuestIcon = (ImageView) convertView.findViewById(R.id.ivQuestIcon);

        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvQuestTitle);
        tvTitle.setText(quest.getTitle());

        TextView tvDescription = (TextView) convertView.findViewById(R.id.tvQuestDescription);
        tvDescription.setText(quest.getDescription());

        TextView tvExperience = (TextView) convertView.findViewById(R.id.tvExpAmountForQuest);
        tvExperience.setText(String.valueOf(quest.getExperience()));

        TextView tvCoins = (TextView) convertView.findViewById(R.id.tvCoinsAmountForQuest);
        tvCoins.setText(String.valueOf(quest.getCredits()));

        if (quest.getStatus() == Quest.QuestStatus.Finished) {
            convertView.setBackgroundColor(Color.GRAY);
            tvTitle.setPaintFlags(tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tvDescription.setPaintFlags(tvDescription.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            ivQuestIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.menu_check, context.getTheme()));
        }

        return convertView;
    }
}
