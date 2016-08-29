package com.severenity.view.Dialogs;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Andriy on 8/25/2016.
 */
public class CreateTeamDialog extends DialogFragment implements View.OnClickListener {

    private TextView mTeamNameView;

    public interface OnTeamCreatedListener {
        void OnTeamCreated();
    }

    // listener that might handle event once team is created
    private OnTeamCreatedListener mListener;

    public static CreateTeamDialog newInstance() {
        CreateTeamDialog frag = new CreateTeamDialog();
        return frag;
    }

    public void setListener(OnTeamCreatedListener listener) {
        mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_create_team, null);

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
                    mListener.OnTeamCreated();
                } else {
                    // TODO: Error handling
                    Toast.makeText(getContext(), "Team creation fail", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onErrorCallback(NetworkResponse response) {
            //TODO: Error handling
        }
    };

    private RequestCallback teamFindCallback = new RequestCallback() {
        @Override
        public void onResponseCallback(JSONObject response) {
            try {
                if (response.getString("result").equals("success")) {
                    JSONObject team = new JSONObject(response.getString("data"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onErrorCallback(NetworkResponse response) {
            // TODO: Error handling
        }
    };
}
