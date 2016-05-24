package com.nosad.sample.entity;

import com.nosad.sample.utils.common.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Novosad on 2/17/16.
 */
public class User {
    private String id;
    private String name;
    private String email;
    private int distance = 0;
    private int immunity = 10;
    private int mentality = 10;
    private int experience = 0;
    private int level = 1;
    private double mViewRadius   = 100.0; // 50.0 meters for now
    private double mActionRadius =  10.0; // 10.0 meters for now

    public User() {

    }

    public User(String email, String name, String id) {
        this.email = email;
        this.name = name;
        this.id = id;
    }

    public double getViewRadius() { return mViewRadius; }

    public double getActionRadius() { return  mActionRadius; }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getImmunity() {
        return immunity + (level + 10); // TODO: This calculation should happen in DB
    }

    public void setImmunity(int immunity) {
        this.immunity = immunity;
    }

    public int getMentality() {
        return mentality + (level + 10);
    }

    public void setMentality(int mentality) {
        this.mentality = mentality;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getJSONUserInfo() {
        JSONObject obj = new JSONObject();
        try {
            obj.put(Constants.OBJECT_TYPE_IDENTIFIER, Constants.TYPE_USER);
            obj.put(Constants.USER_ID, id);
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }

        return obj.toString();
    }
}
