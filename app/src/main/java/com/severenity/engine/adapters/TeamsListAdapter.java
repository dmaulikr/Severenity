package com.severenity.engine.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.severenity.R;
import com.severenity.entity.Team;
import com.severenity.utils.common.Constants;

import java.util.Collection;
import java.util.List;

/**
 * Created by Andriy on 8/31/2016.
 */
public class TeamsListAdapter extends CustomSearchAdapterBase<Team> {

    public TeamsListAdapter(Context ctx) {
        super(ctx, R.layout.teams_item_list);
    }

    public <T> void addList(List<T> teams) {
        mItemList.addAll((Collection<? extends Team>) teams);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View result = convertView;

        if (result == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            result = inflater.inflate(R.layout.teams_item_list, parent, false);
        }

        TextView number = (TextView) result.findViewById(R.id.recordNumber);
        number.setText(Integer.toString(position + 1));

        Team team = getItem(position);
        if (team == null){
            Log.e(Constants.TAG, "Null object in the TeamListAdapter.");
            return null;
        }

        TextView tmName = (TextView) result.findViewById(R.id.teamName);
        tmName.setText(team.getName());

        TextView membersCount = (TextView) result.findViewById(R.id.membersCount);
        membersCount.setText(Integer.toString(team.getMembers().size()));

        return result;
    }
}
