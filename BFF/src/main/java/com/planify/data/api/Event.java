package com.planify.data.api;

import java.time.LocalDate;
import java.time.LocalTime;

public interface Event {

    void setEventId(int eventId);
    int getEventId();
    void setName(String name);
    String getName();
    void setDate(LocalDate date);
    LocalDate getDate();
    void setDescription(String description);
    String getDescription();
    void setStartTime(LocalTime startTime);
    LocalTime getStartTime();
    void setEndTime(LocalTime endTime);
    LocalTime getEndTime();
}
