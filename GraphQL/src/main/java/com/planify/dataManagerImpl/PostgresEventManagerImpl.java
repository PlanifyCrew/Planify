package com.planify.dataManagerImpl;

import com.planify.dataManager.EventManager;
import com.planify.model.user.User;
import com.planify.model.event.Event;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

public class PostgresEventManagerImpl implements EventManager {

    String fileName;

    static PostgresEventManagerImpl propertyFileEventManager = null;

    private PostgresEventManagerImpl(String fileName) {
        this.fileName = fileName;
    }

    static public PostgresEventManagerImpl getPostgresEventManagerImpl(String fileName) {
        if (propertyFileEventManager == null)
            propertyFileEventManager = new PostgresEventManagerImpl(fileName);
        return propertyFileEventManager;
    }


    @Override
    public Collection<Event> getAllEventsByUser(User user) {

        List<Event> events = new ArrayList<>();
        Properties properties = new Properties();
        int i = 1;
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            try(InputStream resourceStream = loader.getResourceAsStream(fileName)) {
                properties.load(resourceStream);
            }

            while (properties.containsKey("Event." + i + ".name")) {
                    String userIdOfEvent = properties.getProperty("Event." + i + ".userid");
                    if (userIdOfEvent == user.getId())
                            events.add(
                                new Event(
                                    properties.getProperty("Event." + i + ".id"),
                                    properties.getProperty("Event." + i + ".name"),
                                    properties.getProperty("Event." + i + ".description"),
                                    Integer.parseInt(
                                            properties.getProperty("Event." + i + ".priority")
                                    ),
                                    properties.getProperty("Event." + i + ".userid")
                            )
                    );
                    i++;
                }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return events;
    }


    @Override
    public Collection<Event> getAllEvents() {

        List<Event> events = new ArrayList<>();
        Properties properties = new Properties();
        int i = 1;
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            try(InputStream resourceStream = loader.getResourceAsStream(fileName)) {
                properties.load(resourceStream);
            }
            while (properties.containsKey("Event." + i + ".name")) {
                    events.add(
                            new Event(
                                    properties.getProperty("Event." + i + ".id"),
                                    properties.getProperty("Event." + i + ".name"),
                                    properties.getProperty("Event." + i + ".description"),
                                    Integer.parseInt(
                                            properties.getProperty("Event." + i + ".priority")
                                    ),
                                    properties.getProperty("Event." + i + ".userid")
                            )
                    );
                i++;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return events;
    }

    @Override
    public void addEvent(Event event, User user) {
        Collection<Event> events = getAllEventsByUser(user);
        events.add(event);
        storeAllEvents(events, user);
    }


    public void storeAllEvents(Collection<Event> Events, User user) {

        // Still ignores the user!!! Destroys the file

        Properties properties = new Properties();
        final AtomicLong counter = new AtomicLong();
        counter.set(0);

        Events.forEach( t -> {
                        properties.setProperty("Event." + counter.incrementAndGet() + ".name", t.getName());
                        properties.setProperty("Event." + counter.get() + ".description", t.getDescription());
                        properties.setProperty("Event." + counter.get() + ".priority", "" + t.getPriority());
        });
        try {
            properties.store(new FileOutputStream(fileName), null);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


}
