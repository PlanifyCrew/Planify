package com.mosbach.demo.data.api;

import org.springframework.cglib.core.Local;
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
