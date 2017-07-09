package com.severenity.engine.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.severenity.R;
import com.severenity.entity.user.User;
import com.severenity.utils.common.Constants;
import com.severenity.view.fragments.ProfileFragment;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Created by Novosad on 8/4/2016.
 */

public class UsersListAdapter extends CustomListArrayAdapterBase<User> {
    private Fragment fragment;

    public UsersListAdapter(Context ctx, Fragment fragment) {
        super(ctx, R.layout.usersearch_item_list);

        this.fragment = fragment;
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
        number.setText(String.format(Locale.US, "%d", position + 1));

        final User user = getItem(position);
        if (user == null){
            Log.e(Constants.TAG, "Null object in the UserSearchAdapter.");
            return result;
        }

        TextView usrName = (TextView) result.findViewById(R.id.userName);
        usrName.setText(user.getName());

        TextView userExp = (TextView) result.findViewById(R.id.userExp);
        userExp.setText(String.format(Locale.US, "%d", user.getExperience()));

        ImageView userInfo = (ImageView) result.findViewById(R.id.ivUserInfo);
        userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileFragment profileFragment = ProfileFragment.newInstance(user.getId());
                FragmentManager manager = fragment.getActivity().getSupportFragmentManager();
                profileFragment.show(manager, "userProfileInfo");
            }
        });

        return result;
    }

}

