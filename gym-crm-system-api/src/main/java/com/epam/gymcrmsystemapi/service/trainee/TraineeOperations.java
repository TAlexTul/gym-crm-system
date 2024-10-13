package com.epam.gymcrmsystemapi.service.trainee;

import com.epam.gymcrmsystemapi.model.trainee.request.TraineeSaveMergeRequest;
import com.epam.gymcrmsystemapi.model.trainee.response.TraineeResponse;
import com.epam.gymcrmsystemapi.model.user.OverridePasswordRequest;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TraineeOperations {

    TraineeResponse create(TraineeSaveMergeRequest request);

    Page<TraineeResponse> list(Pageable pageable);

    Optional<TraineeResponse> findById(long id);

    Optional<TraineeResponse> findByUsername(String username);

    TraineeResponse mergeById(long id, TraineeSaveMergeRequest request);

    TraineeResponse mergeByUsername(String username, TraineeSaveMergeRequest request);

    TraineeResponse changeStatusById(long id, UserStatus status);

    TraineeResponse changeStatusByUsername(String username, UserStatus status);

    TraineeResponse changePasswordById(long id, OverridePasswordRequest request);

    TraineeResponse changePasswordByUsername(String username, OverridePasswordRequest request);

    void deleteById(long id);

    void deleteByUsername(String username);

}
