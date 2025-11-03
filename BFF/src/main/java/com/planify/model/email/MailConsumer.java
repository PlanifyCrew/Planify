package com.planify.model.email;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import com.planify.data.impl.PostgresEventManagerImpl;
import org.springframework.beans.factory.annotation.Qualifier;


@Component
public class MailConsumer {

    private final PostgresEventManagerImpl pgEventManager;

    @Autowired
    public MailConsumer(@Qualifier("eventManagerBean") PostgresEventManagerImpl pgEventManager) {
        this.pgEventManager = pgEventManager;
    }

    @RabbitListener(queues = "eventMailQueue")
    public void receiveMail(EventMailPayload payload) {
        pgEventManager.sendEmail(payload.getEventId(), payload.getEmail());
    }
}
