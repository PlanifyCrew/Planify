package com.mosbach.demo.data.impl;

import com.mosbach.demo.data.api.Event;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.cglib.core.Local;

public class EventImpl implements Event {

    int event_id;
    String name;
    LocalDate date;
    String description;
    LocalTime startTime;
    LocalTime endTime;

    public EventImpl(int event_id, String name, LocalDate date, String description, LocalTime startTime, LocalTime endTime) {
        this.event_id = event_id;
        this.name = name;
        this.date = date;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public void setEventId(int event_id) {
        this.event_id = event_id;
    }

    @Override
    public int getEventId() {
        return event_id;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public LocalTime getStartTime() {
        return startTime;
    }

    @Override
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalTime getEndTime() {
        return endTime;
    }
}
