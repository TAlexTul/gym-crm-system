package com.epam.gymcrmsystemapi.repository.training;

import com.epam.gymcrmsystemapi.model.training.Training;
import com.epam.gymcrmsystemapi.repository.storage.TrainingStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TrainingDAOImpl implements TrainingDAO {

    @Autowired
    private TrainingStorage trainingStorage;

    @Override
    public Training save(Training training) {
        Map<Long, Training> storage = trainingStorage.getTrainingStorage();
        Long maxId = storage.values().stream()
                .map(Training::getId)
                .max(Long::compareTo)
                .orElse(0L);
        training.setId(++maxId);

        return storage.put(maxId, training);
    }

    @Override
    public Page<Training> findAll(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        Map<Long, Training> storage = trainingStorage.getTrainingStorage();

        List<Training> trainings = new ArrayList<>(storage.values());

        if (pageable.getSort().isSorted()) {
            Comparator<Training> comparator = pageable.getSort().stream()
                    .map(order -> {
                        Comparator<Training> comp = Comparator.comparing(Training::getId);
                        return order.isAscending() ? comp : comp.reversed();
                    })
                    .reduce(Comparator::thenComparing)
                    .orElseThrow(IllegalArgumentException::new);

            trainings.sort(comparator);
        }

        int totalElements = trainings.size();

        if (pageNumber * pageSize >= totalElements) {
            return new PageImpl<>(Collections.emptyList(), pageable, totalElements);
        }

        int fromIndex = pageNumber * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalElements);

        List<Training> pageOfTrainees = trainings.subList(fromIndex, toIndex);

        return new PageImpl<>(pageOfTrainees, pageable, totalElements);
    }

    @Override
    public Optional<Training> findById(long id) {
        return Optional.ofNullable(trainingStorage.getTrainingStorage().get(id));
    }
}
