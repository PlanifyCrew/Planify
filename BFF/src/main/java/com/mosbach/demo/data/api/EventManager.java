package com.mosbach.demo.data.api;


import java.util.List;

public interface EventManager {

    List<Event> getAllEventsPerUserId(int user_id);
    int addEvent(Event event, int user_id);
    boolean addParticipants(int event_id, List<Integer> user_ids);

}