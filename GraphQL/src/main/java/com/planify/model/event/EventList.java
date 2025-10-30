package com.planify.model.event;

import com.planify.dataManager.EventManager;
import com.planify.dataManagerImpl.PostgresEventManagerImpl;
import com.planify.model.user.User;

import java.util.Collection;

public class EventList {
	
	private User user;
	private Collection<Event> events;
	EventManager eventManager = PostgresEventManagerImpl.getPostgresEventManagerImpl("src/main/resources/Events.properties");

	public EventList() { }

	public EventList(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Collection<Event> getEvents() {
		return events;
	}

	public void setEvents() {
		events = eventManager.getAllEventsByUser(user);
	}

	public void addEvent(Event event) {
		eventManager.addEvent(event, user);
	}

}
