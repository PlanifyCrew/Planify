package com.planify.model.event;

public class Event {

	private String id = "";
	private String name = "";
	private String description = "";
	private int priority = 0;
	private String userId = "";


	public String getUserId() {
		return userId;
	}


	public Event() {
		
	}

	public String getUserId(String id) {
		return userId;
	}

	public Event(String id, String name, String description, int priority, String userId) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.priority = priority;
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getId() {
		return this.id;
	}
}
