package com.epam.gymcrmsystemapi.service.trainer;

import com.epam.gymcrmsystemapi.model.trainer.request.TrainerMergeRequest;
import com.epam.gymcrmsystemapi.model.trainer.request.TrainerSaveRequest;
import com.epam.gymcrmsystemapi.model.trainer.response.TrainerRegistrationResponse;
import com.epam.gymcrmsystemapi.model.trainer.response.TrainerResponse;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TrainerOperations {

    TrainerRegistrationResponse create(TrainerSaveRequest request);

    Page<TrainerResponse> list(Pageable pageable);

    List<TrainerResponse> listOfTrainersNotAssignedByTraineeUsername(String username);

    Optional<TrainerResponse> findById(long id);

    Optional<TrainerResponse> findByUsername(String username);

    TrainerResponse mergeById(long id, TrainerMergeRequest request);

    TrainerResponse mergeByUsername(String username, TrainerMergeRequest request);

    TrainerResponse changeStatusById(long id, UserStatus status);

    TrainerResponse changeStatusByUsername(String username, UserStatus status);

    void deleteById(long id);

    void deleteByUsername(String username);

}
