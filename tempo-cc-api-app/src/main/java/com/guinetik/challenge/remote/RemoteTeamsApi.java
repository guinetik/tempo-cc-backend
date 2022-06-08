package com.guinetik.challenge.remote;

import com.guinetik.challenge.model.Team;
import com.guinetik.challenge.model.TeamDetails;
import feign.Param;
import feign.RequestLine;

import java.util.List;

public interface RemoteTeamsApi extends RemoteApi {

    @RequestLine("GET /teams")
    List<Team> getAllTeams();

    @RequestLine("GET /teams/{teamId}")
    TeamDetails getTeamById(@Param("teamId") String teamId);
}
