package com.severenity.engine.managers.data;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.severenity.App;
import com.severenity.engine.network.RequestCallback;
import com.severenity.entity.quest.team.TeamQuest;
import com.severenity.utils.common.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Novosad on 5/30/17.
 */

public class TeamQuestManager extends DataManager {
    Realm realm;

    public TeamQuestManager(Context context) {
        super(context);
        realm = Realm.getDefaultInstance();
    }

    public void addTeamQuest(final JSONObject teamQuest) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                realm.createOrUpdateObjectFromJson(TeamQuest.class, teamQuest);
            }
        });
    }

    public void getTeamQuests() {
        App.getRestManager().createRequest(Constants.REST_API_TEAM_QUESTS, Request.Method.GET, null, new RequestCallback() {
            @Override
            public void onResponseCallback(final JSONObject response) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        try {
                            if ("success".equals(response.getString("result"))) {
                                realm.createOrUpdateAllFromJson(TeamQuest.class, response.getJSONArray("data"));
                            } else {
                                Log.e(Constants.TAG, response.getJSONObject("data").toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onErrorCallback(NetworkResponse response) {
                if (response != null) {
                    Log.e(Constants.TAG, response.toString());
                }
            }
        });
    }
}
