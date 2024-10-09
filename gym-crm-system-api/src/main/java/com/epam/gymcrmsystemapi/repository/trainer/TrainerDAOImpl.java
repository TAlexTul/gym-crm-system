package com.epam.gymcrmsystemapi.repository.trainer;

import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.repository.storage.TrainerStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TrainerDAOImpl implements TrainerDAO {

    @Autowired
    private TrainerStorage trainerStorage;

    @Override
    public Trainer save(Trainer trainer) {
        Map<Long, Trainer> storage = trainerStorage.getTrainerStorage();

        Long maxUserId = storage.values().stream()
                .map(Trainer::getUser)
                .map(User::getId)
                .max(Long::compareTo)
                .orElse(0L);

        Long maxTraineeId = storage.keySet().stream()
                .max(Long::compareTo)
                .orElse(0L);

        trainer.getUser().setId(++maxUserId);
        trainer.setId(++maxTraineeId);

        return storage.put(maxTraineeId, trainer);
    }

    @Override
    public Page<Trainer> findAll(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        Map<Long, Trainer> storage = trainerStorage.getTrainerStorage();

        List<Trainer> trainers = new ArrayList<>(storage.values());

        if (pageable.getSort().isSorted()) {
            Comparator<Trainer> comparator = pageable.getSort().stream()
                    .map(order -> {
                        Comparator<Trainer> comp = Comparator.comparing(Trainer::getId);
                        return order.isAscending() ? comp : comp.reversed();
                    })
                    .reduce(Comparator::thenComparing)
                    .orElseThrow(IllegalArgumentException::new);

            trainers.sort(comparator);
        }

        int totalElements = trainers.size();

        if (pageNumber * pageSize >= totalElements) {
            return new PageImpl<>(Collections.emptyList(), pageable, totalElements);
        }

        int fromIndex = pageNumber * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalElements);

        List<Trainer> pageOfTrainees = trainers.subList(fromIndex, toIndex);

        return new PageImpl<>(pageOfTrainees, pageable, totalElements);
    }

    @Override
    public Optional<Trainer> findById(long id) {
        return Optional.ofNullable(trainerStorage.getTrainerStorage().get(id));
    }

    @Override
    public Optional<Trainer> findByFirstNameAndLastName(String firstName, String lastName) {
        return trainerStorage.getTrainerStorage().values().stream()
                .filter(t -> t.getUser().getFirstName().equals(firstName) && t.getUser().getLastName().equals(lastName))
                .findFirst();
    }

    @Override
    public boolean existByFirstNameAndLastName(String firstName, String lastName) {
        return trainerStorage.getTrainerStorage().values().stream()
                .anyMatch(t -> t.getUser().getFirstName().equals(firstName) && t.getUser().getLastName().equals(lastName));
    }

    @Override
    public Trainer changeById(long id, Trainer trainer) {
        return trainerStorage.getTrainerStorage().put(id, trainer);
    }

    @Override
    public void deleteById(long id) {
        trainerStorage.getTrainerStorage().remove(id);
    }
}
