package com.mosbach.demo.data.impl;

import com.mosbach.demo.data.api.Event;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.cglib.core.Local;

public class EventImpl implements Event {

    String name;
    LocalDate date;
    String description;
    LocalTime startTime;
    LocalTime endTime;
    String email;

    public EventImpl(String name, LocalDate date, String description, LocalTime startTime, LocalTime endTime, String email) {
        this.name = name;
        this.date = date;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.email = email;
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

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getEmail() {
        return email;
    }
}
