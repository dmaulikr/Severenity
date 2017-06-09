package com.severenity.entity;

import com.severenity.utils.common.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Novosad on 2/17/16.
 */
public class User {
    private String id;
    private String name;
    private String email;
    private int distance;
    private int immunity;
    private int energy;
    private int experience;
    private int level;
    private double viewRadius;
    private double actionRadius;
    private int credits;
    private int implantHP;
    private int maxImplantHP;
    private int maxImmunity;
    private int maxEnergy;
    private String createdDate;
    private String teamId;
    private String teamName;

    public User() {

    }

    public User(String email, String name, String id) {
        this.email = email;
        this.name = name;
        this.id = id;
    }

    public double getViewRadius() { return viewRadius; }

    public double getActionRadius() { return  actionRadius; }

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
        return immunity;
    }

    public void setImmunity(int immunity) {
        this.immunity = immunity;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
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

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public void setMaxEnergy(int maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    public int getMaxImmunity() {
        return maxImmunity;
    }

    public void setMaxImmunity(int maxImmunity) {
        this.maxImmunity = maxImmunity;
    }

    public int getImplantHP() {
        return implantHP;
    }

    public void setImplantHP(int implantHP) {
        this.implantHP = implantHP;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public void setViewRadius(double viewRadius) {
        this.viewRadius = viewRadius;
    }

    public void setActionRadius(double actionRadius) {
        this.actionRadius = actionRadius;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public int getMaxImplantHP() {
        return maxImplantHP;
    }

    public void setMaxImplantHP(int maxImplantHP) {
        this.maxImplantHP = maxImplantHP;
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

    /**
     * The team user belongs to.
     *
     * @return team id of the team user belongs to.
     */
    public String getTeamId() {
        if (this.teamId == null) {
            return "";
        }
        return this.teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        if (this.teamId == null) {
            return "";
        }
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}
