package com.planify.model.email;

import java.io.Serializable;

public class EventMailPayload implements Serializable {
    private int eventId;
    private String email;

    // Konstruktoren
    public EventMailPayload() {}
    public EventMailPayload(int eventId, String email) {
        this.eventId = eventId;
        this.email = email;
    }

    // Getter & Setter
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
