package com.solid.solidbackend.repositories.apprepository;

import com.solid.solidbackend.entities.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {

    List<Assessment> findAllByUserId(Long userId);


    List<Assessment> findAllByUserIdAndActivity_Id(Long userId, Long activityId);

}
