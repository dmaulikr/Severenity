package com.nosad.sample.entity;

/**
 * Created by Novosad on 2/17/16.
 */
public class User {
    private String id;
    private String name;
    private String email;
    private int distance = 0;
    private int immunity = 0;
    private int mentality = 0;
    private int experience = 0;
    private int level = 0;

    public User() {

    }

    public User(String email, String name, String id) {
        this.email = email;
        this.name = name;
        this.id = id;
    }

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

    public int getMentality() {
        return mentality;
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
}
