package com.severenity.engine.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.severenity.App;
import com.severenity.R;
import com.severenity.entity.User;
import com.severenity.utils.common.Constants;
import com.severenity.view.Dialogs.ButtonClickListener;
import com.severenity.view.Dialogs.CustomAlertDialog;

import java.util.Collection;
import java.util.List;

/**
 * Created by Andriy on 8/4/2016.
 */

public class UsersListAdapter extends CustomListArrayAdapterBase<User> implements ButtonClickListener {

    private boolean mDoConsiderClicks = false;
    private String mModeratorID = null;

    public UsersListAdapter(Context ctx, boolean considerClick) {
        super(ctx, R.layout.usersearch_item_list);
        mDoConsiderClicks = considerClick;
    }

    @Override
    public <T> void addList(List<T> user) {
        mItemList.addAll((Collection<? extends User>) user);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View result = convertView;

        if (result == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            result = inflater.inflate(R.layout.usersearch_item_list, parent, false);
        }

        TextView number = (TextView) result.findViewById(R.id.recordNumber);
        number.setText(Integer.toString(position + 1));

        final User user = getItem(position);
        if (user == null){
            Log.e(Constants.TAG, "Null object in the UserSearchAdapter.");
            return null;
        }

        TextView usrName = (TextView) result.findViewById(R.id.userName);
        usrName.setText(user.getName());

        TextView userExp = (TextView) result.findViewById(R.id.userExp);
        userExp.setText(Integer.toString(user.getExperience()));

        if (mDoConsiderClicks) {
            if (mModeratorID != null ) {

                final ButtonClickListener thisFragment = this;
                result.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (mModeratorID.equals(user.getId())) {
                            return false;
                        } else {
                            CustomAlertDialog dialog = CustomAlertDialog.newInstance(R.string.deleteUser, thisFragment);
                            dialog.setCancelable(false);
                            FragmentManager manager = ((FragmentActivity) mContext).getSupportFragmentManager();
                            dialog.show(manager, "userAction");
                            return true;
                        }
                    }
                });
            }

            result.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            view.setBackgroundColor(Color.parseColor("#222222"));
                            // if moderator id is NULL it menats that the user which has
                            // selected team view is not moderator of that team, so no LongClick
                            // event will occur. In such case we need to return false here.
                            if (mModeratorID != null ) {
                                if (!mModeratorID.equals(user.getId())) {
                                    return false;
                                }
                                else {
                                    return true;
                                }
                            }
                        }
                        case MotionEvent.ACTION_UP: {
                            view.setBackgroundColor(Color.parseColor("#a1a1a1"));
                            return true;
                        }
                    }

                    return false;
                }
            });
        }

        return result;
    }

    public void setModeratorID(String moderatorID){
        mModeratorID = moderatorID;
    }

    @Override
    public void OnYesClicked() {
        Toast.makeText(getContext(), "going to remove user from the team", Toast.LENGTH_SHORT).show();
    }
}

