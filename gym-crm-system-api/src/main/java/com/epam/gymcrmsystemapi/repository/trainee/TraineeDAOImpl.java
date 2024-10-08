package com.epam.gymcrmsystemapi.repository.trainee;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.repository.storage.TraineeStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TraineeDAOImpl implements TraineeDAO {

    @Autowired
    private TraineeStorage traineeStorage;

    @Override
    public Trainee save(Trainee trainee) {
        Map<Long, Trainee> storage = traineeStorage.getTraineeStorage();

        Long maxUserId = storage.values().stream()
                .map(Trainee::getUser)
                .map(User::getId)
                .max(Long::compareTo)
                .orElse(0L);

        Long maxTraineeId = storage.keySet().stream()
                .max(Long::compareTo)
                .orElse(0L);

        trainee.getUser().setId(++maxUserId);
        trainee.setId(++maxTraineeId);

        return storage.put(maxTraineeId, trainee);
    }

    @Override
    public Page<Trainee> findAll(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        Map<Long, Trainee> storage = traineeStorage.getTraineeStorage();

        List<Trainee> trainees = new ArrayList<>(storage.values());

        if (pageable.getSort().isSorted()) {
            Comparator<Trainee> comparator = pageable.getSort().stream()
                    .map(order -> {
                        Comparator<Trainee> comp = Comparator.comparing(Trainee::getId);
                        return order.isAscending() ? comp : comp.reversed();
                    })
                    .reduce(Comparator::thenComparing)
                    .orElseThrow(IllegalArgumentException::new);

            trainees.sort(comparator);
        }

        int totalElements = trainees.size();

        if (pageNumber * pageSize >= totalElements) {
            return new PageImpl<>(Collections.emptyList(), pageable, totalElements);
        }

        int fromIndex = pageNumber * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalElements);

        List<Trainee> pageOfTrainees = trainees.subList(fromIndex, toIndex);

        return new PageImpl<>(pageOfTrainees, pageable, totalElements);
    }

    @Override
    public Optional<Trainee> findById(long id) {
        return Optional.ofNullable(traineeStorage.getTraineeStorage().get(id));
    }

    @Override
    public Optional<Trainee> findByFirstNameAndLastName(String firstName, String lastName) {
        return traineeStorage.getTraineeStorage().values().stream()
                .filter(t -> t.getUser().getFirstName().equals(firstName) && t.getUser().getLastName().equals(lastName))
                .findFirst();
    }

    @Override
    public boolean existByFirstNameAndLastName(String firstName, String lastName) {
        return traineeStorage.getTraineeStorage().values().stream()
                .anyMatch(t -> t.getUser().getFirstName().equals(firstName) && t.getUser().getLastName().equals(lastName));
    }

    @Override
    public Trainee mergeById(long id, Trainee trainee) {
        return traineeStorage.getTraineeStorage().put(id, trainee);
    }

    @Override
    public Trainee changeStatusById(long id, Trainee trainee) {
        return traineeStorage.getTraineeStorage().put(id, trainee);
    }

    @Override
    public Trainee changePasswordById(long id, Trainee trainee) {
        return traineeStorage.getTraineeStorage().put(id, trainee);
    }

    @Override
    public void deleteById(long id) {
        traineeStorage.getTraineeStorage().remove(id);
    }
}
