package com.nosad.sample.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Oleg Novosad on 8/26/2015.
 */
public class Message {
    private String type;
    private String data;

    public Message(String type, String data) {
        this.type = type;
        this.data = data;
    }

    public JSONObject toJSON() {
        try {
            JSONObject json = new JSONObject();
            json.put("type", type);
            json.put("data", data);

            return json;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
