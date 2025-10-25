package com.mosbach.demo.data.api;


import java.util.List;

import com.mosbach.demo.model.teilnehmer.Teilnehmerliste;

import java.time.LocalDate;

public interface EventManager {

    List<Event> getAllEventsPerUserId(int user_id, LocalDate startDate, LocalDate endDate);
    int addEvent(Event event, int user_id);
    boolean updateEvent(int event_id, Event event);
    boolean deleteEvent(int event_id);
    List<Teilnehmerliste> getParticipants(int event_id);
    boolean addParticipants(int event_id, List<Integer> user_ids);
    boolean removeParticipants(int event_id, List<Integer> user_ids);

}