package com.solid.solidbackend.services;

import com.solid.solidbackend.dtos.TeamGradeDTO;
import com.solid.solidbackend.entities.Team;
import com.solid.solidbackend.dtos.TeamDetailsDTO;
import com.solid.solidbackend.entities.User;
import com.solid.solidbackend.exceptions.TeamExistsException;

import java.util.List;

public interface TeamService {

    Team getTeamByName(String name);

    User addUserToTeam(String username, String teamName);

    TeamDetailsDTO getTeamDetailsFromAnActivity(String activityName, String teamName);

    List<TeamGradeDTO> getActivityTeamsWithTheirGrades(String activityName);

    List<User> getMembers(String teamName);

    void removeMemberFromTeam(String leaderName, String removedMemberName, String teamName);

    Team createTeam(String teamName, User teamLeader) throws TeamExistsException;

    Team getTeamByUserId(Long userId);
}
