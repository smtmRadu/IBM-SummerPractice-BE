package com.solid.solidbackend.Controllers;

import com.solid.solidbackend.Entities.Activity;
import com.solid.solidbackend.Entities.Assessment;
import com.solid.solidbackend.Entities.User;
import com.solid.solidbackend.Enums.Role;
import com.solid.solidbackend.Exceptions.InvalidUserException;
import com.solid.solidbackend.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/depricated")
public class TeamLeaderController{
//    private final UserService userService;
//    @Autowired
//    public TeamLeaderController(UserService userService) {
//        this.userService = userService;
//    }
//
//
//    /**
//     * Searches for the team of the member, afterwards for activity_memberships, then selects all activities
//     * @return
//     */
//    @GetMapping("/activities/{userId}")
//    public ResponseEntity<List<Activity>> getActivities(@PathVariable Long userId)
//    {
//        return null;
//    }
//
//    /**
//     * Get all assessments of a teamleader;
//     * @param
//     * @return
//     */
//    @GetMapping("/activities/{activityId}/{userId}/users")
//    public ResponseEntity<List<Assessment>> getAssessments(@PathVariable Long userId)
//    {
//        return null;
//    }
//
//    /**
//     * Joins an activity as a team leader
//     * @param mentor
//     * @return
//     */
//    @PostMapping(path = "/activities/join/{activityId}")
//    public ResponseEntity<Activity> joinActivity(@PathVariable Long activityId, @RequestBody User mentor)
//    {
//        return null;
//    }
//


}