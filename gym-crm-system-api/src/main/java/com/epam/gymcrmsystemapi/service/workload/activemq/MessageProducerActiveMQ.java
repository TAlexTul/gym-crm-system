package com.epam.gymcrmsystemapi.service.workload.activemq;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MessageProducerActiveMQ implements MessageProducerMQ {

    private static final Logger log = LoggerFactory.getLogger(MessageProducerActiveMQ.class);

    private final JmsTemplate jmsTemplate;

    public MessageProducerActiveMQ(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    @CircuitBreaker(name = "action", fallbackMethod = "fallbackForAction")
    public void sendMessage(String destination, Object message) {
        jmsTemplate.convertAndSend(destination, message);
    }

    public void fallbackForAction(String destination, Throwable t) {
        log.error("Fallback called. Unable to send message to destination: " + destination);
        log.error("Inside circuit breaker fallbackForAction, cause - {}", t.toString());
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Trainer workload API is unavailable");
    }
}
