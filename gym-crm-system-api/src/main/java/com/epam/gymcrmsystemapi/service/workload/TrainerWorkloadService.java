package com.epam.gymcrmsystemapi.service.workload;

import com.epam.gymcrmsystemapi.exceptions.TrainerExceptions;
import com.epam.gymcrmsystemapi.model.trainer.response.TrainerResponse;
import com.epam.gymcrmsystemapi.model.training.request.ProvidedTrainingSaveRequest;
import com.epam.gymcrmsystemapi.model.training.request.TrainingSaveRequest;
import com.epam.gymcrmsystemapi.model.training.response.TrainingResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class TrainerWorkloadService implements TrainerWorkloadOperations {

    private static final Logger log = LoggerFactory.getLogger(TrainerWorkloadService.class);

    @Value("${trainer-workload-api.name}")
    private String trainerWorkloadApiName;
    @Value("${trainer-workload-api.uri}")
    private String trainerWorkloadApiUri;

    private final DiscoveryClient discoveryClient;
    private final RestClient restClient;

    public TrainerWorkloadService(DiscoveryClient discoveryClient,
                                  RestClient.Builder restClientBuilder) {
        this.discoveryClient = discoveryClient;
        this.restClient = restClientBuilder.build();
    }

    @Override
    @CircuitBreaker(name = "add", fallbackMethod = "fallbackForAdd")
    public void invoke(TrainingSaveRequest request, TrainingResponse response, String encodedJwt) {
        TrainerResponse trainerResponse = response.trainers().stream().findFirst()
                .orElseThrow(() -> TrainerExceptions.trainerNotFound(request.trainerUsername()));

        var providedRequest = new ProvidedTrainingSaveRequest(
                request.trainerUsername(),
                trainerResponse.firstName(),
                trainerResponse.lastName(),
                trainerResponse.status(),
                response.trainingDate(),
                response.trainingDuration()
        );

        ServiceInstance serviceInstance = getServiceInstance();

        String uri = getUri(serviceInstance);

        restClient.post()
                .uri(uri)
                .headers(headers -> headers.setBearerAuth(encodedJwt))
                .body(providedRequest)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    @CircuitBreaker(name = "delete", fallbackMethod = "fallbackForDelete")
    public void invoke(TrainingResponse response, String encodedJwt) {
        List<String> trainerUsernames = response.trainers().stream()
                .map(TrainerResponse::username)
                .toList();

        ServiceInstance serviceInstance = getServiceInstance();

        String uri = getUri(serviceInstance);

        restClient.delete()
                .uri(uri, uriBuilder -> uriBuilder
                        .queryParam("trainerUsernames", String.join(",", trainerUsernames))
                        .queryParam("trainingDate", response.trainingDate())
                        .queryParam("trainingDuration", response.trainingDuration())
                        .build())
                .headers(headers -> headers.setBearerAuth(encodedJwt))
                .retrieve()
                .toBodilessEntity();
    }

    public void fallbackForAdd(Throwable t) {
        log.error("Inside circuit breaker fallbackForAdd, cause - {}", t.toString());
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Trainer workload API is unavailable");
    }

    public void fallbackForDelete(Throwable t) {
        log.error("Inside circuit breaker fallbackForDelete, cause - {}", t.toString());
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Trainer workload API is unavailable");
    }

    private ServiceInstance getServiceInstance() {
        List<ServiceInstance> instances = discoveryClient.getInstances(trainerWorkloadApiName);
        if (instances.isEmpty()) {
            throw new IllegalStateException("No instances available for " + trainerWorkloadApiName);
        }
        return instances.get(0);
    }

    private String getUri(ServiceInstance serviceInstance) {
        return UriComponentsBuilder.fromUri(serviceInstance.getUri())
                .path(trainerWorkloadApiUri)
                .toUriString();
    }
}
