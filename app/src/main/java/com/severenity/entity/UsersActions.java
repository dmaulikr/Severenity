package com.severenity.entity;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.severenity.App;
import com.severenity.R;
import com.severenity.utils.common.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Andriy on 6/28/2016.
 */
public class UsersActions implements View.OnClickListener {
    public enum ActionsType {
        ActionsTypeUnknown,
        ActionsOnUser,
        ActionsOnPlace,
    }

    private LinearLayout mUserActions;
    private ActionsType  mActionsType;
    private String       mDataID;

    public UsersActions(View view) {
        mUserActions = (LinearLayout)view.findViewById(R.id.userActions);
        mUserActions.findViewById(R.id.btnCapturePlace).setOnClickListener(this);
        mUserActions.findViewById(R.id.btnCapturePlayer).setOnClickListener(this);
        mUserActions.findViewById(R.id.btnAttack).setOnClickListener(this);
        mUserActions.findViewById(R.id.btnDefend).setOnClickListener(this);
        mUserActions.findViewById(R.id.btnInvisibility).setOnClickListener(this);
        mActionsType = ActionsType.ActionsTypeUnknown;
        mDataID = "";
    }

    public void showActionPanel(ActionsType type, Context context) {

        if (mActionsType != ActionsType.ActionsTypeUnknown) {
            if (mActionsType != type) {
                hideActionPanel(context);
            }
        }

        setType(type);

        switch (type) {
            case ActionsOnUser: {
                if (mUserActions.getVisibility() == View.INVISIBLE) {
                    (mUserActions.findViewById(R.id.btnCapturePlayer)).setVisibility(View.VISIBLE);
                    (mUserActions.findViewById(R.id.btnDefend)).setVisibility(View.VISIBLE);
                    (mUserActions.findViewById(R.id.btnInvisibility)).setVisibility(View.VISIBLE);
                    (mUserActions.findViewById(R.id.btnAttack)).setVisibility(View.VISIBLE);
                    (mUserActions.findViewById(R.id.btnCapturePlace)).setVisibility(View.GONE);
                    mUserActions.setVisibility(View.VISIBLE);
                    Animation anim = AnimationUtils.loadAnimation(context, R.anim.user_actions_slide_in);
                    mUserActions.startAnimation(anim);
                }
                break;
            }
            case ActionsOnPlace: {
                if (mUserActions.getVisibility() == View.INVISIBLE) {
                    (mUserActions.findViewById(R.id.btnAttack)).setVisibility(View.VISIBLE);
                    (mUserActions.findViewById(R.id.btnDefend)).setVisibility(View.VISIBLE);
                    (mUserActions.findViewById(R.id.btnInvisibility)).setVisibility(View.VISIBLE);
                    (mUserActions.findViewById(R.id.btnCapturePlace)).setVisibility(View.VISIBLE);
                    (mUserActions.findViewById(R.id.btnCapturePlayer)).setVisibility(View.GONE);
                    mUserActions.setVisibility(View.VISIBLE);
                    Animation anim = AnimationUtils.loadAnimation(context, R.anim.user_actions_slide_in);
                    mUserActions.startAnimation(anim);
                }
                break;
            }
            default:
                Log.w(Constants.TAG, "Unknown Actions type.");
                break;
        }
    }

    public void hideActionPanel(Context context) {

        if (mUserActions.getVisibility() == View.VISIBLE) {

            Animation anim = AnimationUtils.loadAnimation(context, R.anim.user_actions_slide_out);
            mUserActions.startAnimation(anim);
            mUserActions.setVisibility(View.INVISIBLE);
        }

    }

    private void setType(ActionsType type) {
        mActionsType = type;
    }

    public ActionsType getType() {
        return mActionsType;
    }

    public boolean isActionsDisplaying() {
        return (mUserActions.getVisibility() == View.VISIBLE);
    }

    public void setCapturedItemID(String id) {
        mDataID = id;
    }

    @Override
    public void onClick(View view) {

        if (mDataID.isEmpty()) {
            Log.e(Constants.TAG, "Unknown destination object ID.");
            return;
        }

        switch (view.getId()) {
            case R.id.btnCapturePlace: {
                // adds Logged-in user into Place's owners list
                JSONObject data = new JSONObject();
                try {
                    data.put("userId", App.getUserManager().getCurrentUser().getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                App.getWebSocketManager().sendPlaceUpdateToServer(mDataID, Constants.UsersActions.CAPTURE, data);
                mDataID = "";
                break;
            }

            case R.id.btnAttack: {
                App.getWebSocketManager().sendUserActionToServer(mDataID, Constants.UsersActions.ATTACK);
                mDataID = "";
                break;
            }

            case R.id.btnCapturePlayer:
            case R.id.btnDefend:
            case R.id.btnInvisibility:
                Toast.makeText(mUserActions.getContext(), "This signal is under development. Stay tuned!", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
