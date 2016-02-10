package com.nosad.sample.model;

/**
 * Created by Novosad on 2/8/16.
 */
public class Sample {
    private String name;
    private int avatarId;
    private String description;

    public Sample(String name, int avatarId) {
        this.name = name;
        this.avatarId = avatarId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(int avatarId) {
        this.avatarId = avatarId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
