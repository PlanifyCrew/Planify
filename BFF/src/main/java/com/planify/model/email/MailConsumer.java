package com.planify.model.email;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import com.planify.data.impl.PostgresEventManagerImpl;

@Component
public class MailConsumer {

    private final PostgresEventManagerImpl pgEventManager;

    public MailConsumer() {
        this.pgEventManager = PostgresEventManagerImpl.getPostgresEventManagerImpl();;
    }

    @RabbitListener(queues = "eventMailQueue")
    public void receiveMail(EventMailPayload payload) {
        List<String> emailList = List.of(payload.getEmail());
        pgEventManager.sendEmail(payload.getEventId(), emailList);
    }
}
