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

public class UsersListAdapter extends CustomListArrayAdapterBase<User> {

    public UsersListAdapter(Context ctx) {
        super(ctx, R.layout.usersearch_item_list);
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

        return result;
    }

}

