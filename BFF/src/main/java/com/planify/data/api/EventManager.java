package com.planify.data.api;


import java.util.List;

import com.planify.model.teilnehmer.Teilnehmerliste;

import java.time.LocalDate;

public interface EventManager {

    List<Event> getAllEventsPerUserId(int user_id, LocalDate startDate, LocalDate endDate);
    int addEvent(Event event, int user_id);
    boolean updateEvent(int event_id, Event event);
    Event getEvent(int event_id);
    boolean deleteEvent(int event_id);
    List<Teilnehmerliste> getParticipants(int event_id);
    boolean addParticipants(int event_id, List<Integer> user_ids);
    boolean removeParticipants(int event_id, List<Integer> user_ids);
    boolean sendEmail(int event_id, List<String> tnListe);
    boolean changeStatus(int event_id, int user_id);

}