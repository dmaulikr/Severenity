package com.severenity.view.fragments.clans.pages;

/**
 * Created by Andriy on 9/6/2016.
 *
 * This interface describes the events that can happen with the team.
 * team can be created, user can joint team, user can leave team
 */
public interface TeamEventsListener {

    // indicates tha the team was created
    public void OnTeamCreated();

    // indicates that user joined the team
    public void OnTeamJoined();

    // indicates that user has left the team
    public void OnTeamLeft();
}
