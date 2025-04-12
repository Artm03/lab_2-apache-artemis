package org.example.artemis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageProducer {

    private final JmsTemplate jmsTemplate;

    @Value("${app.queues.input}")
    private String inputQueue;

    public MessageProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendToInputQueue(String message) {
        new Thread(() -> {
            try {
                log.info("Attempting to send message to {}: {}", inputQueue, message);
                jmsTemplate.convertAndSend(inputQueue, message);
                log.info("Message successfully sent to {} queue", inputQueue);
            } catch (JmsException e) {
                log.error("Failed to send message to {} queue", inputQueue, e);
            }
        }).start();

    }
}