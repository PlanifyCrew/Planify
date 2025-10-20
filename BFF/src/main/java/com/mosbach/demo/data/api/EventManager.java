package com.mosbach.demo.data.api;


import java.util.List;

public interface EventManager {

    List<Event> getAllEventsPerEmail(String email);
    boolean addEvent(Event event);

}
