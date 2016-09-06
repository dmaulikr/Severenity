package com.severenity.view.Dialogs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.NetworkResponse;
import com.severenity.App;
import com.severenity.R;
import com.severenity.engine.network.RequestCallback;
import com.severenity.view.fragments.clans.TeamFragment;

import org.json.JSONObject;

/**
 * Created by Andriy on 9/5/2016.
 */
public class TeamInfoDialog extends DialogFragment implements View.OnClickListener {

    private TeamFragment mTeamFragment;
    private String mTeamID;

    public static TeamInfoDialog newInstance(String teamID) {
        TeamInfoDialog frag = new TeamInfoDialog();
        Bundle arg = new Bundle();
        arg.putString("teamID", teamID);
        frag.setArguments(arg);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = getArguments();
        mTeamID = args.getString("teamID");

        View view = inflater.inflate(R.layout.dialog_team_info, null);
        setCancelable(false);

        mTeamFragment = new TeamFragment(mTeamID);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.teamInfoFragment, mTeamFragment, "TeamInfoFragment");
        transaction.commit();

        ((Button)view.findViewById(R.id.cancelButton)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.joinButton)).setOnClickListener(this);
        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.remove(mTeamFragment);
        transaction.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancelButton:
                super.dismiss();
                break;

            case R.id.joinButton:
                //App.getTeamManager().joinUserToTeam(mTeamID, App.getUserManager().getCurrentUser().getId(), teamJoiningCallback);
                break;
            }
    }

    /**
     * the callback method which is for handling response to joint the team request.
     */
    private RequestCallback teamJoiningCallback = new RequestCallback() {
        @Override
        public void onResponseCallback(JSONObject response) {

        }

        @Override
        public void onErrorCallback(NetworkResponse response) {

        }
    };
}
