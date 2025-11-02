package com.planify.model.email;

import com.planify.model.email.EventMailPayload;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class MailPublisher {

    private final RabbitTemplate rabbitTemplate;

    public MailPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMailToQueue(EventMailPayload payload) {
        rabbitTemplate.convertAndSend("eventMailQueue", payload);
    }
}
