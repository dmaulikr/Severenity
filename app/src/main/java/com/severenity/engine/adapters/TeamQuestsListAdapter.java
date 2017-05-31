package com.severenity.engine.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.severenity.R;
import com.severenity.databinding.TeamQuestItemBinding;
import com.severenity.databinding.TeamQuestsGroupBinding;
import com.severenity.entity.quest.team.TeamQuest;
import com.severenity.entity.quest.team.TeamQuestPart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Novosad on 5/31/17.
 */

public class TeamQuestsListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<TeamQuest> teamQuests;
    private Map<String, List<TeamQuestPart>> parts;

    public TeamQuestsListAdapter(Context context, List<TeamQuest> teamQuests, Map<String, List<TeamQuestPart>> parts) {
        this.context = context;
        this.teamQuests = new ArrayList<>(teamQuests);
        this.parts = parts;
    }

    @Override
    public int getGroupCount() {
        return teamQuests.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return parts.get(teamQuests.get(groupPosition).getId()).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return teamQuests.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return parts.get(teamQuests.get(groupPosition).getId()).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        TeamQuestsGroupBinding teamQuestsGroupBinding;

        if (convertView == null) {
            teamQuestsGroupBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(context), R.layout.team_quests_group, parent, false);
            convertView = teamQuestsGroupBinding.getRoot();
        } else {
            teamQuestsGroupBinding = (TeamQuestsGroupBinding) convertView.getTag();
        }

        teamQuestsGroupBinding.setTeamQuest(teamQuests.get(groupPosition));
        convertView.setTag(teamQuestsGroupBinding);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        TeamQuestItemBinding teamQuestItemBinding;

        if (convertView == null) {
            teamQuestItemBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(context), R.layout.team_quest_item, parent, false);
            convertView = teamQuestItemBinding.getRoot();
        } else {
            teamQuestItemBinding = (TeamQuestItemBinding) convertView.getTag();
        }

        teamQuestItemBinding.setTeamQuestPart(parts.get(teamQuests.get(groupPosition).getId()).get(childPosition));
        convertView.setTag(teamQuestItemBinding);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
