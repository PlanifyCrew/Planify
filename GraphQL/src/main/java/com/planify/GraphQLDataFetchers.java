package com.planify;

import com.google.common.collect.ImmutableMap;
import com.planify.dataManager.UserManager;
import com.planify.dataManager.EventManager;
import com.planify.dataManagerImpl.PostgresUserManagerImpl;
import com.planify.dataManagerImpl.PostgresEventManagerImpl;
import com.planify.model.user.User;
import com.planify.model.event.Event;
import graphql.schema.DataFetcher;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class GraphQLDataFetchers {

    EventManager eventManager = PostgresEventManagerImpl.getPostgresEventManagerImpl("Events.properties");
    UserManager userManager = PostgresUserManagerImpl.getPostgresUserManagerImpl("Users.properties");

    /*
        TODO Either fetch the data from the REST server OR from the database.
        Make sure you fetch always before returning answers OR fetch it every 20 seconds or ...
    */
    private Collection<Event> events() {
        return eventManager.getAllEvents();
    };
    private Collection<User> users() {
        return userManager.getAllUsers();
    };

    public DataFetcher getEventByIdDataFetcher() {
        return dataFetchingEnvironment -> {
            String eventId = dataFetchingEnvironment.getArgument("id");

            return events()
                    .stream()
                    .filter(event -> event.getId().equals(eventId))
                    .findFirst()
                    .orElse(null);
        };
    }

    public DataFetcher getEventsByUserIdDataFetcher() {
        return dataFetchingEnvironment -> {
            String userId = dataFetchingEnvironment.getArgument("id");
            return events()
                    .stream()
                    .filter(event -> event.getUserId().equals(userId))
                    .collect(Collectors.toList());
        };
    }

    public DataFetcher getUserByIdDataFetcher() {
        return dataFetchingEnvironment -> {
            String userId = dataFetchingEnvironment.getArgument("id");
            return users()
                    .stream()
                    .filter(user -> user.getId().equals(userId))
                    .findFirst()
                    .orElse(null);
        };
    }
    public DataFetcher getUserDataFetcher() {
        return dataFetchingEnvironment -> {
            Event event = dataFetchingEnvironment.getSource();
            String userId = event.getUserId();
            return users()
                    .stream()
                    .filter(user -> user.getId().equals(userId))
                    .findFirst()
                    .orElse(null);
        };
    }

    public DataFetcher getEventsDataFetcher() {
        return dataFetchingEnvironment -> {
            User user = dataFetchingEnvironment.getSource();
            String userId = user.getId();
            return events()
                    .stream()
                    .filter(event -> event.getUserId().equals(userId))
                    .collect(Collectors.toList());
        };
    }
}
