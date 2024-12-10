////package com.epam.gymcrmsystemapi.service.workload;
////
////import com.epam.gymcrmsystemapi.controller.TrainingController;
////import com.epam.gymcrmsystemapi.service.training.TrainingOperations;
////import org.junit.jupiter.api.BeforeEach;
////import org.springframework.cloud.client.ServiceInstance;
////import org.springframework.cloud.client.discovery.DiscoveryClient;
////import org.springframework.test.web.servlet.setup.MockMvcBuilders;
////import org.springframework.web.client.RestClient;
////
////import static org.mockito.Mockito.mock;
////import static org.mockito.Mockito.when;
////
////public class TrainerWorkloadServiceTest {
////
////    private DiscoveryClient discoveryClient;
////
////    private ServiceInstance serviceInstance;
////
////    @BeforeEach
////    void setUp() {
////        discoveryClient = mock(DiscoveryClient.class);
////        serviceInstance = mock(ServiceInstance.class);
////
////        RestClient.Builder restClientBuilder = mock(RestClient.Builder.class);
////        RestClient restClient = mock(RestClient.class);
////        when(restClientBuilder.build()).thenReturn(restClient);
////
////        mvc = MockMvcBuilders
////                .standaloneSetup(new TrainingController(trainingOperations, trainerWorkloadOperations, discoveryClient, restClientBuilder))
////                .build();
////    }
////
////
//package com.epam.gymcrmsystemapi.service.workload;
//
//import com.epam.gymcrmsystemapi.exceptions.TrainerExceptions;
//import com.epam.gymcrmsystemapi.model.trainer.response.TrainerResponse;
//import com.epam.gymcrmsystemapi.model.training.request.ProvidedTrainingSaveRequest;
//import com.epam.gymcrmsystemapi.model.training.request.TrainingSaveRequest;
//import com.epam.gymcrmsystemapi.model.training.response.TrainingResponse;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.cloud.client.discovery.DiscoveryClient;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.client.RestClient;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.net.URI;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class TrainerWorkloadServiceTest {
//
//    @Mock
//    private DiscoveryClient discoveryClient;
//    @Mock
//    private RestClient restClient;
//    @Mock
//    private RestClient.RequestSpecification requestSpecification;
//    @Mock
//    private RestClient.RequestSpecificationWithBody requestSpecificationWithBody;
//    @Mock
//    private RestClient.ResponseSpecification responseSpecification;
//    @Mock
//    private ServiceInstance serviceInstance;
//
//    @InjectMocks
//    private TrainerWorkloadService trainerWorkloadService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        when(restClient.post()).thenReturn(requestSpecificationWithBody);
//        when(restClient.delete()).thenReturn(requestSpecification);
//        when(requestSpecificationWithBody.uri(any(String.class))).thenReturn(requestSpecificationWithBody);
//        when(requestSpecificationWithBody.headers(any())).thenReturn(requestSpecificationWithBody);
//        when(requestSpecificationWithBody.body(any())).thenReturn(requestSpecificationWithBody);
//        when(requestSpecificationWithBody.retrieve()).thenReturn(responseSpecification);
//        when(requestSpecification.uri(any(String.class), any())).thenReturn(requestSpecification);
//        when(requestSpecification.headers(any())).thenReturn(requestSpecification);
//        when(requestSpecification.retrieve()).thenReturn(responseSpecification);
//        when(responseSpecification.toBodilessEntity()).thenReturn(null);
//    }
//
//    @Test
//    void testInvoke_AddTraining_Success() {
//        // Arrange
//        TrainingSaveRequest saveRequest = new TrainingSaveRequest("trainer1");
//        TrainingResponse response = new TrainingResponse(
//                List.of(new TrainerResponse("trainer1", "John", "Doe", "ACTIVE")),
//                "2024-12-01", 60
//        );
//        String encodedJwt = "mockedJwt";
//
//        when(discoveryClient.getInstances(anyString())).thenReturn(List.of(serviceInstance));
//        when(serviceInstance.getUri()).thenReturn(URI.create("http://mocked-service"));
//        trainerWorkloadService.trainerWorkloadApiUri = "/add";
//
//        // Act
//        assertDoesNotThrow(() -> trainerWorkloadService.invoke(saveRequest, response, encodedJwt));
//
//        // Assert
//        verify(requestSpecificationWithBody).uri("http://mocked-service/add");
//        verify(requestSpecificationWithBody).headers(any());
//        verify(requestSpecificationWithBody).body(any(ProvidedTrainingSaveRequest.class));
//        verify(responseSpecification).toBodilessEntity();
//    }
//
//    @Test
//    void testInvoke_AddTraining_TrainerNotFound() {
//        // Arrange
//        TrainingSaveRequest saveRequest = new TrainingSaveRequest("nonexistentTrainer");
//        TrainingResponse response = new TrainingResponse(List.of(), "2024-12-01", 60);
//        String encodedJwt = "mockedJwt";
//
//        // Act & Assert
//        TrainerExceptions exception = assertThrows(
//                TrainerExceptions.class,
//                () -> trainerWorkloadService.invoke(saveRequest, response, encodedJwt)
//        );
//        assertTrue(exception.getMessage().contains("trainer not found"));
//    }
//
//    @Test
//    void testInvoke_DeleteTraining_Success() {
//        // Arrange
//        TrainingResponse response = new TrainingResponse(
//                List.of(new TrainerResponse("trainer1", "John", "Doe", "ACTIVE")),
//                "2024-12-01", 60
//        );
//        String encodedJwt = "mockedJwt";
//
//        when(discoveryClient.getInstances(anyString())).thenReturn(List.of(serviceInstance));
//        when(serviceInstance.getUri()).thenReturn(URI.create("http://mocked-service"));
//        trainerWorkloadService.trainerWorkloadApiUri = "/delete";
//
//        // Act
//        assertDoesNotThrow(() -> trainerWorkloadService.invoke(response, encodedJwt));
//
//        // Assert
//        verify(requestSpecification).uri(eq("http://mocked-service/delete"), any());
//        verify(requestSpecification).headers(any());
//        verify(responseSpecification).toBodilessEntity();
//    }
//
//    @Test
//    void testInvoke_NoInstancesFound() {
//        // Arrange
//        when(discoveryClient.getInstances(anyString())).thenReturn(List.of());
//        String encodedJwt = "mockedJwt";
//        TrainingResponse response = new TrainingResponse(
//                List.of(new TrainerResponse("trainer1", "John", "Doe", "ACTIVE")),
//                "2024-12-01", 60
//        );
//
//        // Act & Assert
//        IllegalStateException exception = assertThrows(
//                IllegalStateException.class,
//                () -> trainerWorkloadService.invoke(response, encodedJwt)
//        );
//        assertTrue(exception.getMessage().contains("No instances available"));
//    }
//}
//
