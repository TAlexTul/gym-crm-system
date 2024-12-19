package com.epam.gymcrmsystemapi.service.workload.activemq;

public interface MessageProducerMQ {

    void sendMessage(String destination, Object message);

}
