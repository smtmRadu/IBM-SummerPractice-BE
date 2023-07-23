package com.solid.solidbackend.services;

import com.solid.solidbackend.entities.TeamActivity;
import com.solid.solidbackend.entities.User;
import com.solid.solidbackend.entities.Team;
import com.solid.solidbackend.entities.TeamMembership;
import com.solid.solidbackend.exceptions.TeamMembershipExistsException;
import com.solid.solidbackend.repositories.apprepository.TeamActivityRepository;
import com.solid.solidbackend.repositories.apprepository.TeamMembershipRepository;
import com.solid.solidbackend.repositories.apprepository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMembershipRepository teamMembershipRepository;
    private final TeamActivityRepository teamActivityRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository, TeamMembershipRepository teamMembershipRepository, TeamActivityRepository teamActivityRepository) {
        this.teamRepository = teamRepository;
        this.teamMembershipRepository = teamMembershipRepository;
        this.teamActivityRepository = teamActivityRepository;
    }

    public Team getTeamByName(String name)
    {
        // get the team
        // initialize transient fields
        Team team = null;

        // Setup teamMembers


        // Setup teamGrade
        // List<Assessment> assessments = assessmentRepository.findAllByUserId()
        // team.teamMembers.forEach(x -> assessments.addAll(x.getAllAssessments()));
        // team.teamGrade = 0f;
        // for (var ass :
        //         assessments) {
        //     team.teamGrade += ass.getGrade();
        // }
        // team.teamGrade /= assessments.size();

        return team;
    }

    public User addUserToTeam(User user, String teamName)
    {
        // Find the Team object by name
        var teamOptional = teamRepository.findByName(teamName);
        Team team = teamOptional.get();

        // If exists, throw successfull exception
        var teamMembershipOptional = teamMembershipRepository.findById(team.getId());
        if(teamMembershipOptional.isPresent())
        {
            throw new TeamMembershipExistsException("User already joined the team.");
        }

        // else it doesn't exist, create new TeamMemebership

        TeamMembership tm = new TeamMembership(team, user);
        teamMembershipRepository.save(tm);

        return user;
    }

    public List<Team> getTeamsByActivity(String activityName) {
        List<TeamActivity> teamActivities = teamActivityRepository.findAllTeamsByActivityName(activityName);
        return teamActivities.stream().map(TeamActivity::getTeam).toList();
    }

    public List<User> getTeamMembers(Long teamId){
        List<TeamMembership> teamMemberships = teamMembershipRepository.getAllMembers(teamId);
        return teamMemberships.stream().map(TeamMembership::getUser).toList();
    }
}