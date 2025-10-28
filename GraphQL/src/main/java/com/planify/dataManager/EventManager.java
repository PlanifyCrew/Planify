package com.planify.dataManager;

import com.planify.model.user.User;
import com.planify.model.event.Event;

import java.util.Collection;

public interface EventManager {

    Collection<Event> getAllEventsByUser(User user);
    Collection<Event> getAllEvents();
    void addEvent(Event event, User user);

    // TODO
    // removeTask, getTasksInOrder, getTaskByTaskID, ...

    // TODO
    // Make the TaskManager handling students.

}
