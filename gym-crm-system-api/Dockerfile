FROM openjdk:17
WORKDIR application
COPY target/gym-crm-system-api-0.0.1-SNAPSHOT.jar /gym-crm-system-api-0.0.1-SNAPSHOT.jar
RUN java -Djarmode=layertools -jar /gym-crm-system-api-0.0.1-SNAPSHOT.jar extract

ENTRYPOINT ["java", "-jar", "/gym-crm-system-api-0.0.1-SNAPSHOT.jar"]