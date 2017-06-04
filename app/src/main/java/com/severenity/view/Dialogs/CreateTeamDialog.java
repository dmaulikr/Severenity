package com.severenity.view.Dialogs;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.severenity.App;
import com.severenity.R;
import com.severenity.engine.network.RequestCallback;
import com.severenity.utils.common.Constants;
import com.severenity.view.fragments.clans.pages.TeamEventsListener;

import org.json.JSONException;
import org.json.JSONObject;

import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_TEAM;

/**
 * Created by Andriy on 8/25/2016.
 */
public class CreateTeamDialog extends DialogFragment implements View.OnClickListener {

    private TextView mTeamNameView;

    // listener that might handle event once team is created
    private TeamEventsListener mListener;

    public static CreateTeamDialog newInstance() {
        CreateTeamDialog frag = new CreateTeamDialog();
        return frag;
    }

    public void setListener(TeamEventsListener listener) {
        mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_create_team, null);
        setCancelable(false);

        ((Button)view.findViewById(R.id.btnCreateTeam)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.btnCancel)).setOnClickListener(this);

        mTeamNameView = (TextView)view.findViewById(R.id.etTeamName);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCancel:
                super.dismiss();
                return;

            case R.id.btnCreateTeam:
                String teamName = mTeamNameView.getText().toString();
                if (teamName.isEmpty()) {
                    return;
                }

                App.getTeamManager().createTeam(teamName, App.getUserManager().getCurrentUser(), teamCreationCallBack);
                return;
        }
    }

    /**
     *  An object that handles createTeam callback response
     */
    private RequestCallback teamCreationCallBack = new RequestCallback() {
        @Override
        public void onResponseCallback(JSONObject response) {
            try {
                if (response.getString("result").equals("success")) {
                    String teamName = response.getString("data");
                    App.getUserManager().updateCurrentUser(new String[]{COLUMN_TEAM}, new String[]{teamName});
                    mListener.onTeamCreated();
                } else {
                    // TODO: Error handling
                    String err = response.getString("data");
                    Toast.makeText(getContext(), "Team creation fail: " + err, Toast.LENGTH_SHORT).show();
                    Log.e(Constants.TAG, "Team creation fail: " + err);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onErrorCallback(NetworkResponse response) {
            //TODO: Error handling
            Log.e(Constants.TAG, "Team creation fail: " + (response == null ? "" : response.toString()));
        }
    };

    private RequestCallback teamFindCallback = new RequestCallback() {
        @Override
        public void onResponseCallback(JSONObject response) {
            try {
                if (response.getString("result").equals("success")) {
                    JSONObject team = new JSONObject(response.getString("data"));
                } else {
                    // TODO: Error handling
                    String err = response.getString("data");
                    Log.e(Constants.TAG, "find team fail: " + err);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onErrorCallback(NetworkResponse response) {
            // TODO: Error handling
            Log.e(Constants.TAG, "Team creation fail: " + response.toString());
        }
    };
}
