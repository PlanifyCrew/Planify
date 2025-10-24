package com.mosbach.demo.data.api;


import java.util.List;
import java.time.LocalDate;

public interface EventManager {

    List<Event> getAllEventsPerUserId(int user_id, LocalDate startDate, LocalDate endDate);
    int addEvent(Event event, int user_id);
    boolean addParticipants(int event_id, List<Integer> user_ids);

}