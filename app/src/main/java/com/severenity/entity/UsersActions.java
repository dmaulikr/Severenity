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
 * Created by Novosad on 6/28/2016.
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
    private Context mContext;

    public UsersActions(View view, Context context) {
        mUserActions = (LinearLayout)view.findViewById(R.id.userActions);
        mUserActions.findViewById(R.id.btnCapturePlace).setOnClickListener(this);
        mUserActions.findViewById(R.id.btnAttack).setOnClickListener(this);
        mUserActions.findViewById(R.id.btnDefend).setOnClickListener(this);
        mUserActions.findViewById(R.id.btnInvisibility).setOnClickListener(this);
        mActionsType = ActionsType.ActionsTypeUnknown;
        mDataID = "";
        mContext = context;
    }

    public void showActionPanel(ActionsType type) {

        if (mActionsType != ActionsType.ActionsTypeUnknown) {
            if (mActionsType != type) {
                hideActionPanel(mContext);
            }
        }

        setType(type);

        switch (type) {
            case ActionsOnUser: {
                if (mUserActions.getVisibility() == View.INVISIBLE) {
                    (mUserActions.findViewById(R.id.btnDefend)).setVisibility(View.VISIBLE);
                    (mUserActions.findViewById(R.id.btnInvisibility)).setVisibility(View.VISIBLE);
                    (mUserActions.findViewById(R.id.btnAttack)).setVisibility(View.VISIBLE);
                    (mUserActions.findViewById(R.id.btnCapturePlace)).setVisibility(View.GONE);
                    mUserActions.setVisibility(View.VISIBLE);
                    Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.user_actions_slide_in);
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
                    mUserActions.setVisibility(View.VISIBLE);
                    Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.user_actions_slide_in);
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

    public void setSelectedItemId(String id) {
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
                    data.put("placeId", mDataID);
                    data.put("by", App.getUserManager().getCurrentUser().getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                App.getWebSocketManager().sendUserActionToServer(data, Constants.UsersActions.CAPTURE);
                mDataID = "";
                break;
            }

            case R.id.btnAttack: {
                if (getType() == ActionsType.ActionsOnPlace) {
                    Toast.makeText(mContext, "This signal cannot be performed on place yet as it is under development. Stay tuned!", Toast.LENGTH_LONG).show();
                    mDataID = "";
                    return;
                }

                JSONObject data = new JSONObject();
                try {
                    data.put("userId", mDataID);
                    data.put("by", App.getUserManager().getCurrentUser().getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                App.getWebSocketManager().sendUserActionToServer(data, Constants.UsersActions.ATTACK);
                mDataID = "";
                break;
            }

            case R.id.btnDefend:
            case R.id.btnInvisibility:
                Toast.makeText(mContext, "This signal is under development. Stay tuned!", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
