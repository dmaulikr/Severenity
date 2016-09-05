package com.severenity.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andriy on 8/31/2016.
 */
public class Team {
    private String mTeamName;
    private User mModerator;
    private List<User> mTeamMembers = new ArrayList<>();
    private String mTeamID;

    public Team(){};

    public String getName() {return this.mTeamName; }
    public void setName(String name) {this.mTeamName = name; }

    public String getTeamID() {return this.mTeamID; }
    public void setTeamID(String teamID) {this.mTeamID = teamID; }

    public User getModerator() {return this.mModerator; }
    public void setModerator(User moderator) {this.mModerator = moderator; }

    public void addMember(User member) {this.mTeamMembers.add(member); }
    public List<User> getMembers() {return this.mTeamMembers; }
}
