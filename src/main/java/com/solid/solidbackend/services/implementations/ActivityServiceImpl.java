package com.solid.solidbackend.services.implementations;

import com.solid.solidbackend.entities.*;
import com.solid.solidbackend.exceptions.NoActivityFoundException;
import com.solid.solidbackend.exceptions.UserNotFoundException;
import com.solid.solidbackend.repositories.apprepository.*;
import com.solid.solidbackend.services.ActivityService;
import com.solid.solidbackend.services.MentorActivityService;
import com.solid.solidbackend.services.TeamActivityService;
import com.solid.solidbackend.services.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final MentorActivityService mentorActivityService;
    private final UserRepository userRepository;
    private final TeamActivityRepository teamActivityRepository;
    private final TeamMembershipRepository teamMembershipRepository;
    private final TeamActivityService teamActivityService;

    public ActivityServiceImpl(ActivityRepository activityRepository, MentorActivityRepository mentorActivityRepository,
                               MentorActivityService mentorActivityService, UserRepository userRepository,
                               TeamActivityRepository teamActivityRepository, TeamMembershipRepository teamMembershipRepository, TeamActivityService teamActivityService) {

        this.activityRepository = activityRepository;
        this.mentorActivityService = mentorActivityService;

        this.userRepository = userRepository;
        this.teamActivityRepository = teamActivityRepository;
        this.teamMembershipRepository = teamMembershipRepository;

        this.teamActivityService = teamActivityService;
    }

    @Override
    public Activity createActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    @Override
    public Activity getActivityByName(String activityName) {

        return activityRepository.findActivityByName(activityName).orElseThrow(
                () -> new NoActivityFoundException("No activity was found with name: " + activityName)
        );

    }

    @Override
    public List<Activity> getUserActivities(String userName) {
        // Get user's team
        User user = userRepository.findByName(userName).get();//TODO error checking
        Team team = teamMembershipRepository.findTeamByUserId(user.getId());

        // Use team's id to retrieve all activities
        List<TeamActivity> teamActivities = teamActivityRepository.findAllActivitiesByTeamId(team.getId());
        return teamActivities.stream().map(TeamActivity::getActivity).toList();
    }

    @Override
    public MentorActivity addNewMentorToActivity(String activityName, User newMentor, String dueDate) {

        // search for the activity to see if it already exists
        Optional<Activity> existingActivity = activityRepository.findActivityByName(activityName);

        // if it does, we add the new mentor to it, else we create a new activity and add the mentor to it
        if (existingActivity.isPresent()) {
            return mentorActivityService.linkMentorWithActivity(newMentor, existingActivity.get());
        } else {
            Activity newActivity = new Activity();
            newActivity.setName(activityName);
            newActivity.setCreator(newMentor);
            newActivity.setDeadline(dueDate);

            // we do this because we need an activity object with an id already set
            Activity joinedActivity = activityRepository.save(newActivity);

            return mentorActivityService.linkMentorWithActivity(newMentor, joinedActivity);

        }

    }

    @Override
    public Activity joinActivity(String userName, String activityName) {
        // we get the activity that the lead wants to join
        Activity joinedActivity = activityRepository.findActivityByName(activityName).orElseThrow(
                () -> new NoActivityFoundException("Activity with name: " + activityName + " was not found")
        );

        // we get the user for its id to use at linking
        User teamLead = userRepository.findByName(userName).orElseThrow(
                () -> new UserNotFoundException("User with name: " + userName + " was not found")
        );

        // we get the team that the lead is in
        Team joiningTeam = teamMembershipRepository.findTeamByUserId(teamLead.getId());

        // we make a new link between the activity and the team of the lead
        teamActivityService.joinActivityByTeam(joinedActivity, joiningTeam);

        return joinedActivity;
    }

}
