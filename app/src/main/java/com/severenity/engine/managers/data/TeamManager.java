package com.severenity.engine.managers.data;

import android.content.Context;

import com.android.volley.Request;
import com.severenity.App;
import com.severenity.engine.network.RequestCallback;
import com.severenity.entity.User;
import com.severenity.utils.common.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Andriy on 8/27/2016.
 *
 * Team manager provides API for manipulating with the teams
 */

public class TeamManager {

    private Context mContext;

    public TeamManager(Context context) {
        mContext = context;
    }

    /**
     * Creates team.
     *
     * @param teamName - the name of the team to be created
     * @param creator  - user who is gaging to be moderator
     * @param callback - method that handles response. The response structre is next:
     *                 response.result = the result of the operation
     *                 response.reason = text reason
     */
    public void createTeam(String teamName, User creator, RequestCallback callback) {
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("userId", creator.getId());
            requestObject.put("name", teamName);

            App.getRestManager().createRequest(Constants.REST_API_TEAM_CREATE, Request.Method.POST, requestObject, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the team by it's name
     *
     * @param teamName - provided name of the team to be retrieved
     * @param callback - callback method for handling server response
     */
    public void getTeam(String teamName, RequestCallback callback) {
        App.getRestManager().createRequest(Constants.REST_API_TEAM_GET + "/" + teamName, Request.Method.GET, null, callback);
    }
}
