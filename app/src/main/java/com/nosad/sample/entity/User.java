package com.nosad.sample.entity;

/**
 * Created by Novosad on 2/17/16.
 */
public class User {
    private int id;
    private String name;
    private String email;

    public User() {

    }

    public User(String email) {
        this.email = email;
    }

    public User(String email, String name, int id) {
        this.email = email;
        this.name = name;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
}
