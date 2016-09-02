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
     *                 response.data   = text reason
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
     * @param teamID   - provided ID of the team to be retrieved
     * @param callback - callback method for handling server response
     */
    public void getTeam(String teamID, RequestCallback callback) {
        App.getRestManager().createRequest(Constants.REST_API_TEAM_GET + "/" + teamID, Request.Method.GET, null, callback);
    }

    /**
     * Gets the list of teams from the server defined by range (pages)
     *
     * @param start - indicates start offset.
     * @param count - indicates count of records to be retrieved.
     */
    public void getTeamsAsPage(int start, int count, RequestCallback callback) {

        String req = Constants.REST_API_TEAM_ALL_RANGE + "/?pageOffset=" + start + "&pageLimit=" + count;
        App.getRestManager().createRequest(req, Request.Method.GET, null, callback);
    }

    /**
     * Removes user from the team
     *
     * @param userID - the ID of the user to be removed
     * @param teamID - the ID of the team from which user to be removed
     * @param callback - handling server responses
     */

    // TODO: to be done on the server
    public void removeUserFromTeam(String userID, String teamID, RequestCallback callback) {
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("userId", userID);
            requestObject.put("teamId", teamID);

            App.getRestManager().createRequest(Constants.REST_API_TEAM_REMOVE_USER, Request.Method.POST, requestObject, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
