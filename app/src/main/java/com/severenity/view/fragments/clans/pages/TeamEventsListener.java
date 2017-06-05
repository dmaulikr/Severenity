package com.severenity.view.fragments.clans.pages;

/**
 * Created by Novosad on 9/6/2016.
 *
 * This interface describes the events that can happen with the team.
 * team can be created, user can joint team, user can leave team
 */
public interface TeamEventsListener {
    // indicates tha the team was created
    void onTeamCreated();

    // indicates that user joined the team
    void onTeamJoined();

    // indicates that user has left the team
    void onTeamLeft();
}
