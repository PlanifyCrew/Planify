package com.planify.model.email;

import com.planify.data.impl.PostgresEventManagerImpl;
import com.planify.data.api.EventManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventManagerConfig {

    @Bean
    public PostgresEventManagerImpl eventManagerBean() {
        return PostgresEventManagerImpl.getPostgresEventManagerImpl();
    }
}
