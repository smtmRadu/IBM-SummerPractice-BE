package com.solid.solidbackend.services.implementations;

import com.solid.solidbackend.dtos.TeamDetailsDTO;
import com.solid.solidbackend.dtos.TeamGradeDTO;
import com.solid.solidbackend.entities.*;
import com.solid.solidbackend.enums.Role;
import com.solid.solidbackend.exceptions.*;
import com.solid.solidbackend.repositories.apprepository.*;
import com.solid.solidbackend.services.TeamService;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;
    private final TeamActivityRepository teamActivityRepository;
    private final TeamMembershipRepository teamMembershipRepository;
    private final UserRepository userRepository;
    private final AssessmentRepository assessmentRepository;
    private final ActivityRepository activityRepository;

    @Autowired
    public TeamServiceImpl(TeamRepository teamRepository,
                           TeamActivityRepository teamActivityRepository,
                           TeamMembershipRepository teamMembershipRepository,
                           UserRepository userRepository,
                           AssessmentRepository assessmentRepository,
                           ActivityRepository activityRepository) {
        this.teamRepository = teamRepository;
        this.teamActivityRepository = teamActivityRepository;
        this.teamMembershipRepository = teamMembershipRepository;
        this.userRepository = userRepository;
        this.assessmentRepository = assessmentRepository;

        this.activityRepository = activityRepository;
    }

    @Override
    public Team getTeamByName(String name) {
        Optional<Team> teamOptional = teamRepository.findByName(name);

        if (teamOptional.isEmpty())
            throw new TeamNotFoundException(name);

        return teamOptional.get();
    }


    @Override
    public Team createTeam(String teamName, User teamLeader) throws TeamExistsException {
        Optional<Team> teamOptional = teamRepository.findByName(teamName);

        if (teamOptional.isPresent())
            throw new TeamExistsException(teamName);

        Team team = new Team(teamName, teamLeader);
        return teamRepository.save(team);
    }


    @Override
    public TeamDetailsDTO getTeamDetailsFromAnActivity(String activityName, String teamName) {
        // Fetch activity by name
        Activity activity = activityRepository.findActivityByName(activityName)
                .orElseThrow(() -> new ActivityNotFoundException(activityName));

        // Fetch team by name
        Team team = teamRepository.findByName(teamName)
                .orElseThrow(() -> new TeamNotFoundException(teamName));

        List<User> members = getMembers(teamName);

        // for each member, i get all assessments. From there i extract all grades and attendances.
        List<Float> gradesOfMembers = new LinkedList<>();
        List<Integer> attendancesOfMembers = new LinkedList<>();
        for (var member : members) {
            List<Assessment> memberAssessments = assessmentRepository.findAllByUserIdAndActivity_Id(member.getId(), activity.getId());

            float meanOfAllGrades = 0f;
            int attendanceCount = 0;
            for (var ass : memberAssessments) {
                // Check if the grade is greater than 10 and set it to 10 if needed
                float grade = ass.getGrade() > 10f ? 10f : ass.getGrade();
                meanOfAllGrades += grade;
                if (ass.isAttended())
                    attendanceCount++;
            }
            if (!memberAssessments.isEmpty()) // check for division by 0
                meanOfAllGrades /= memberAssessments.size();

            // Round the mean grade to one decimal place
            meanOfAllGrades = Math.round(meanOfAllGrades * 10) / 10.0f;

            gradesOfMembers.add(meanOfAllGrades);
            attendancesOfMembers.add(attendanceCount);
        }

        Float teamGrade = 0f;
        for (var memberGrade : gradesOfMembers) {
            teamGrade += memberGrade == null ? 0f : memberGrade;
        }
        if (!gradesOfMembers.isEmpty()) // check for division by 0
            teamGrade /= gradesOfMembers.size();

        // Round the team grade to one decimal place
        teamGrade = Math.round(teamGrade * 10) / 10.0f;

        return new TeamDetailsDTO(members, gradesOfMembers, attendancesOfMembers, teamGrade);
    }



    @Override
    public  List<TeamGradeDTO> getActivityTeamsWithTheirGrades(String activityName)
    {
        // it doesn t extract any memberships
        var memberships = teamActivityRepository.findAllByActivityName(activityName);

        List<Team> teams = memberships.stream().map(TeamActivity::getTeam).collect(Collectors.toList());

        List<TeamGradeDTO> teamGradeDTOS = new LinkedList<>();
        for (var team :
                teams) {
            TeamDetailsDTO teamDetailsDTO = getTeamDetailsFromAnActivity(activityName, team.getName());
            teamGradeDTOS.add(new TeamGradeDTO(team, teamDetailsDTO.teamGrade));
        }

        return teamGradeDTOS;
    }


    @Override
    public List<User> getMembers(String teamName) {
        List<TeamMembership> memberships = teamMembershipRepository.findAllTeamMembershipsByTeamName(teamName);
        return memberships.stream().map(TeamMembership::getUser).collect(Collectors.toList());
    }

    @Override
    public void removeMemberFromTeam(String leaderName, String removedMemberName, String teamName) {
        User leader = userRepository.findByName(leaderName).orElseThrow(
                () -> new UserNotFoundException(leaderName));
        if (leader.getRole() != Role.TEAM_LEADER) throw new RoleNotAllowedException("TEAM LEADERS");

        User removedMember = userRepository.findByName(removedMemberName).orElseThrow(
                () -> new UserNotFoundException(removedMemberName));

        List<User> members = getMembers(teamName);
        if (!members.contains(leader)){
            throw new TeamMembershipNotFoundException("You do not belong to this team");
        }

        if (members.contains(removedMember)) {
            TeamMembership teamMembership = teamMembershipRepository.findByUserId(removedMember.getId());
            teamMembershipRepository.delete(teamMembership);
        } else {
            throw new TeamMembershipNotFoundException("User is not a member of this team, so cannot be deleted.");
        }
    }



    @Override
    public User addUserToTeam(String username, String teamName) {
        // Find the Team object by name
        Team joinedTeam = teamRepository.findByName(teamName).orElseThrow(
                () -> new TeamNotFoundException("Team with name '" + teamName + "' not found.")
        );
        // Check if user is already part of the team
        Optional<TeamMembership> teamMembership = teamMembershipRepository.findByTeamAndUser(joinedTeam.getId(), username);
        if (teamMembership.isPresent()) {
            throw new TeamMembershipExistsException("User with username '" + username + "' already joined the team '" + teamName + "'.");
        }
        // Take the whole user object
        User newUser = userRepository.findByName(username).orElseThrow(
                () -> new UserNotFoundException(username)
        );
        // Create a new TeamMembership
        TeamMembership tm = new TeamMembership(joinedTeam, newUser);
        teamMembershipRepository.save(tm);

        return newUser;
    }


    @Override
    public Team getTeamByUserId(Long userId) {

        return teamMembershipRepository.findTeamByUserId(userId).orElseThrow(
                () -> new TeamMembershipNotFoundException("No team membership found for user with ID:" + userId)
        );
    }
}
