package com.severenity.view.fragments.clans;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.severenity.App;
import com.severenity.R;
import com.severenity.engine.adapters.UsersListAdapter;
import com.severenity.engine.network.RequestCallback;
import com.severenity.entity.Team;
import com.severenity.entity.user.User;
import com.severenity.utils.Utils;
import com.severenity.utils.common.Constants;
import com.severenity.view.custom.CustomListView;
import com.severenity.view.dialogs.CustomAlertDialog;
import com.severenity.view.fragments.clans.pages.TeamEventsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Novosad on 8/25/2016.
 */
public class TeamFragment extends Fragment implements CustomAlertDialog.ButtonClickListener,
        AdapterView.OnItemLongClickListener,
        View.OnClickListener {

    private TextView          mTeamModerator;
    private TextView          mTeamName;
    private CustomListView    mUsersInTeamList;
    private String            mTeamID;
    private TeamFragment      TeamFragmentInstance = this;
    private CustomAlertDialog mMoveUserFromTeamDialog = null;
    private String            mUserIdToDelete;
    private Button            mLeaveButton;
    private FrameLayout       mLeaveButtonLayout;
    private boolean           mIsSelfRemoved = false;
    private ImageButton       mTeamLogoButton;
    private TeamLogoFragment  mTeamLogoFragment;
    private int               mPermissionCheck;

    private static final String ARGUMENT_TEAM_ID = "teamId";

    // listener that might handle event once team is created
    private TeamEventsListener mListener;

    public TeamFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param teamId - team id of the team to display.
     * @return A new instance of fragment {@link TeamFragment}.
     */
    public static TeamFragment newInstance(String teamId) {
        TeamFragment fragment = new TeamFragment();
        Bundle args = new Bundle();
        args.putString(ARGUMENT_TEAM_ID, teamId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clans_team, container, false);

        mTeamLogoFragment = new TeamLogoFragment();
        mPermissionCheck  = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        mTeamModerator = view.findViewById(R.id.teamModeratorText);
        mUsersInTeamList = view.findViewById(R.id.usersInTeamList);
        mLeaveButton = view.findViewById(R.id.leaveTeamButton);
        mLeaveButtonLayout = view.findViewById(R.id.leaveTeam);
        mLeaveButtonLayout = view.findViewById(R.id.leaveTeam);
        mTeamLogoButton = view.findViewById(R.id.teamLogoBtn);
        mTeamName = view.findViewById(R.id.teamNameText);

        UsersListAdapter searchAdapter = new UsersListAdapter(getContext(), this);
        mUsersInTeamList.setChoiceMode(CustomListView.CHOICE_MODE_SINGLE);
        mUsersInTeamList.setSelector(R.drawable.item_selector);
        mUsersInTeamList.setAdapter(searchAdapter);
        mUsersInTeamList.showLoadSpinner(false);

        mLeaveButton.setOnClickListener(this);
        mTeamLogoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    mTeamLogoFragment.show(getFragmentManager(),"TeamLogoFragment");
                }
        });

        mTeamID = getArguments().getString(ARGUMENT_TEAM_ID);

        if (!App.getUserManager().getCurrentUser().getTeamId().equals(mTeamID)) {
            mLeaveButtonLayout.setVisibility(View.GONE);
        }

        requestTeamInfo();

        loadImageFromStorage(mTeamLogoButton,mPermissionCheck);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadImageFromStorage(mTeamLogoButton,mPermissionCheck);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            requestTeamInfo();
        }
    }

    private void requestTeamInfo() {
        if (mTeamID == null || mTeamID.isEmpty()) {
            return;
        }

        App.getTeamManager().getTeam(mTeamID, new RequestCallback() {
            @Override
            public void onResponseCallback(JSONObject response) {
                try {
                    if (response.getString("result").equals("success")) {
                        Team team = Utils.createTeamFromJSON(response.getJSONObject("data"));
                        if (team == null) {
                            Log.e(Constants.TAG, "Was not able to create user from JSON:" + response.getJSONObject("data"));
                            return;
                        }
                        mTeamModerator.setText(team.getModerator().getName());
                        if (App.getUserManager().getCurrentUser().getId().equals(team.getModerator().getId())) {
                            Log.i(Constants.TAG, "Current user is moderator for the team. Give ability to delete users");
                            mUsersInTeamList.setLongClickable(true);
                            mUsersInTeamList.setOnItemLongClickListener(TeamFragmentInstance);
                        }
                        mTeamModerator.setHint(team.getModerator().getId());
                        mTeamName.setText(team.getName());
                        mUsersInTeamList.clearData();
                        mUsersInTeamList.addNewData(team.getMembers());
                    } else {
                        Log.e(Constants.TAG, "Getting team by name fails");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorCallback(NetworkResponse response) {
                Log.e(Constants.TAG, "Getting team by name fails");
            }
        });
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        User user = (User)mUsersInTeamList.getItemAtPosition(i);
        String moderatorID = mTeamModerator.getHint().toString();
        if (!user.getId().equals(moderatorID)) {
            mUserIdToDelete = user.getId();
            mMoveUserFromTeamDialog = CustomAlertDialog.newInstance(R.string.deleteUser, this);
            mMoveUserFromTeamDialog.setCancelable(false);
            FragmentManager fm = getFragmentManager();
            mMoveUserFromTeamDialog.show(fm, "userRemoveDialog");
            return true;
        }

        return false;
    }

    @Override
    public void OnOkButtonClick() {
        App.getTeamManager().removeUserFromTeam(mUserIdToDelete, mTeamID, teamLeaveCallback);
    }

    @Override
    public void OnCancelButtonClick() {
        mIsSelfRemoved = false;
        mUserIdToDelete = "";
        if (mMoveUserFromTeamDialog != null) {
            mMoveUserFromTeamDialog.dismiss();
            mMoveUserFromTeamDialog = null;
        }
    }

    /**
     * the callback method which is for handling response to joint the team request.
     */
    private RequestCallback teamLeaveCallback = new RequestCallback() {
        @Override
        public void onResponseCallback(JSONObject response) {
            try {
                if ("success".equalsIgnoreCase(response.getString("result"))) {
                    JSONObject data = response.getJSONObject("data");
                    String userId = data.getString("userId");
                    Log.i(Constants.TAG, "User removed from the team");
                    requestTeamInfo();
                    mMoveUserFromTeamDialog.dismiss();
                    mUserIdToDelete = "";
                    if (mIsSelfRemoved) {
                        mListener.onTeamLeft();
                        App.getUserManager().updateCurrentUserTeam("", userId);
                    }
                } else {
                    // TODO: Error handling
                    String err = response.getString("data");
                    Log.e(Constants.TAG, "removing user from team fails: " + err);
                    Toast.makeText(getContext(), err, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onErrorCallback(NetworkResponse response) {
            // TODO: Error handling
            Log.e(Constants.TAG, "removing user from team fails: " + (response == null ? "" : response.toString()));
        }
    };

    @Override
    public void onClick(View v) {
        mIsSelfRemoved = true;
        mUserIdToDelete = App.getUserManager().getCurrentUser().getId();
        mMoveUserFromTeamDialog = CustomAlertDialog.newInstance(mIsSelfRemoved ?
                                R.string.leaveTeam : R.string.deleteUser, this);
        mMoveUserFromTeamDialog.setCancelable(false);
        FragmentManager fm = getFragmentManager();
        mMoveUserFromTeamDialog.show(fm, "userRemoveDialog");
    }

    public void setListener(TeamEventsListener listener) {
        mListener = listener;
    }

    protected void loadImageFromStorage(ImageButton imageButton, int permission) {
        File directory = new File(Environment.getExternalStorageDirectory().toString() +
                "/logos_directory");
        if (!directory.exists() || permission == PackageManager.PERMISSION_DENIED) {
            imageButton.setImageResource(R.drawable.ic_launcher_small);
        } else {
            try {
                File imagePath = new File(directory + "/teamLogo.png");
                Bitmap decoder = BitmapFactory.decodeStream(new FileInputStream(imagePath));
                imageButton.setImageBitmap(decoder);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}

