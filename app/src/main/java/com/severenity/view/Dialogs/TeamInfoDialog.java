package com.severenity.view.Dialogs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.severenity.App;
import com.severenity.R;
import com.severenity.engine.network.RequestCallback;
import com.severenity.entity.User;
import com.severenity.utils.Utils;
import com.severenity.utils.common.Constants;
import com.severenity.view.fragments.clans.TeamFragment;
import com.severenity.view.fragments.clans.pages.TeamEventsListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Andriy on 9/5/2016.
 */
public class TeamInfoDialog extends DialogFragment implements
        View.OnClickListener, TeamEventsListener {

    private TeamFragment mTeamFragment;
    private String mTeamID;
    private static TeamInfoDialog mDialog;

    // listener that might handle event once team is created
    private TeamEventsListener mListener;

    public void setListener(TeamEventsListener listener) {
        mListener = listener;
    }

    public static TeamInfoDialog newInstance(String teamID) {
        mDialog = new TeamInfoDialog();
        Bundle arg = new Bundle();
        arg.putString("teamID", teamID);
        mDialog.setArguments(arg);
        return mDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = getArguments();
        mTeamID = args.getString("teamID");

        View view = inflater.inflate(R.layout.dialog_team_info, null);
        setCancelable(false);

        mTeamFragment = new TeamFragment(mTeamID, this);

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
                App.getTeamManager().joinUserToTeam(mTeamID, App.getUserManager().getCurrentUser().getId(), teamJoiningCallback);
                break;
            }
    }

    /**
     * the callback method which is for handling response to joint the team request.
     */
    private RequestCallback teamJoiningCallback = new RequestCallback() {
        @Override
        public void onResponseCallback(JSONObject response) {
            try {
                if (response.getString("result").equals("success")) {
                    User user = Utils.createUserFromJSON(response.getJSONObject("data"));
                    App.getUserManager().updateCurrentUserLocallyWithUser(user);
                    mDialog.dismiss();
                    if (mListener != null) {
                        mListener.OnTeamJoined();
                    }
                } else {
                    // TODO: Error handling
                    String err = response.getString("data");
                    Log.e(Constants.TAG, "joining team fail: " + err);
                    Toast.makeText(getContext(), err, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onErrorCallback(NetworkResponse response) {
            // TODO: Error handling
            Log.e(Constants.TAG, "Joining to team fail: " + (response == null ? "" : response.toString()));
        }
    };

    @Override
    public void OnTeamCreated() {

    }

    @Override
    public void OnTeamJoined() {

    }

    @Override
    public void OnTeamLeft() {
        dismiss();
        if (mListener != null) {
            mListener.OnTeamLeft();
        }
    }
}
