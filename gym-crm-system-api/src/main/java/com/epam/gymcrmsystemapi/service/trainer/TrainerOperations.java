package com.epam.gymcrmsystemapi.service.trainer;

import com.epam.gymcrmsystemapi.model.trainer.request.TrainerSaveMergeRequest;
import com.epam.gymcrmsystemapi.model.trainer.response.TrainerResponse;
import com.epam.gymcrmsystemapi.model.user.OverridePasswordRequest;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TrainerOperations {

    TrainerResponse create(TrainerSaveMergeRequest request);

    Page<TrainerResponse> list(Pageable pageable);

    Optional<TrainerResponse> findById(long id);

    Optional<TrainerResponse> findByUsername(String username);

    TrainerResponse mergeById(long id, TrainerSaveMergeRequest request);

    TrainerResponse mergeByUsername(String username, TrainerSaveMergeRequest request);

    TrainerResponse changeStatusById(long id, UserStatus status);

    TrainerResponse changeStatusByUsername(String username, UserStatus status);

    TrainerResponse changePasswordById(long id, OverridePasswordRequest request);

    TrainerResponse changePasswordByUsername(String username, OverridePasswordRequest request);

    void deleteById(long id);

    void deleteByUsername(String username);

}
