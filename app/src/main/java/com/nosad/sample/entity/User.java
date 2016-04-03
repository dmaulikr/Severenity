package com.nosad.sample.entity;

/**
 * Created by Novosad on 2/17/16.
 */
public class User {
    private String id;
    private String name;
    private String email;
    private int steps;

    public User() {

    }

    public User(String email) {
        this.email = email;
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

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}
