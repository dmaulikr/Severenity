package com.nosad.sample.entity;

/**
 * Created by Novosad on 5/9/16.
 */
public class Quest {
    private long id;
    private String title;
    private String description;
    private long experience;
    private long credits;

    public Quest() {}

    public Quest(long id, String title, String description, long experience, long credits) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.experience = experience;
        this.credits = credits;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getExperience() {
        return experience;
    }

    public void setExperience(long experience) {
        this.experience = experience;
    }

    public long getCredits() {
        return credits;
    }

    public void setCredits(long credits) {
        this.credits = credits;
    }
}
