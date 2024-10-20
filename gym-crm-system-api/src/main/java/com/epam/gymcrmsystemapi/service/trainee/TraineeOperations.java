package com.epam.gymcrmsystemapi.service.trainee;

import com.epam.gymcrmsystemapi.model.trainee.request.TraineeChangeTrainersSetRequest;
import com.epam.gymcrmsystemapi.model.trainee.request.TraineeMergeRequest;
import com.epam.gymcrmsystemapi.model.trainee.request.TraineeSaveRequest;
import com.epam.gymcrmsystemapi.model.trainee.response.TraineeRegistrationResponse;
import com.epam.gymcrmsystemapi.model.trainee.response.TraineeResponse;
import com.epam.gymcrmsystemapi.model.trainer.response.TrainerResponse;
import com.epam.gymcrmsystemapi.model.user.OverrideLoginRequest;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TraineeOperations {

    TraineeRegistrationResponse create(TraineeSaveRequest request);

    Page<TraineeResponse> list(Pageable pageable);

    Optional<TraineeResponse> findById(long id);

    Optional<TraineeResponse> findByUsername(String username);

    TraineeResponse mergeById(long id, TraineeMergeRequest request);

    TraineeResponse mergeByUsername(String username, TraineeMergeRequest request);

    TraineeResponse changeStatusById(long id, UserStatus status);

    TraineeResponse changeStatusByUsername(String username, UserStatus status);

    TraineeResponse changeLoginDataById(long id, OverrideLoginRequest request);

    TraineeResponse changeLoginDataByUsername(String username, OverrideLoginRequest request);

    List<TrainerResponse> changeTraineeSetOfTrainers(TraineeChangeTrainersSetRequest request);

    void deleteById(long id);

    void deleteByUsername(String username);

}
