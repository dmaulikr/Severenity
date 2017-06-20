package com.severenity.engine.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.severenity.R;
import com.severenity.entity.Team;
import com.severenity.utils.common.Constants;
import com.severenity.view.dialogs.TeamInfoDialog;
import com.severenity.view.fragments.clans.pages.TeamEventsListener;

import java.util.List;

/**
 * Created by Novosad on 8/31/2016.
 */
public class TeamsListAdapter extends CustomListArrayAdapterBase<Team> {

    // event listener to be passed to the dialog
    private TeamEventsListener mListener;

    public TeamsListAdapter(Context ctx, TeamEventsListener listener) {
        super(ctx, R.layout.teams_item_list);
        mListener = listener;
    }

    public void addList(List teams) {
        mItemList.addAll(teams);
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

        final Team team = getItem(position);
        if (team == null) {
            Log.e(Constants.TAG, "Null object in the TeamListAdapter.");
            return result;
        }

        TextView tmName = (TextView) result.findViewById(R.id.teamName);
        tmName.setText(team.getName());

        TextView membersCount = (TextView) result.findViewById(R.id.membersCount);
        membersCount.setText(Integer.toString(team.getMembers().size()));

        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TeamInfoDialog dialog = TeamInfoDialog.newInstance(team.getTeamId());
                dialog.setListener(mListener);
                FragmentManager manager = ((FragmentActivity) mContext).getSupportFragmentManager();
                dialog.show(manager, "TeamInfo");
            }
        });

        return result;
    }
}
