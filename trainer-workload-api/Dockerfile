FROM openjdk:17
WORKDIR application
COPY target/trainer-workload-api-0.0.1-SNAPSHOT.jar /trainer-workload-api-0.0.1-SNAPSHOT.jar
RUN java -Djarmode=layertools -jar /trainer-workload-api-0.0.1-SNAPSHOT.jar extract

ENTRYPOINT ["java", "-jar", "/trainer-workload-api-0.0.1-SNAPSHOT.jar"]