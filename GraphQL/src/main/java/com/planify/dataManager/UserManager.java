package com.planify.dataManager;

import com.planify.model.user.User;

import java.util.Collection;

public interface UserManager {

    // getAllStudents, getSpecificStudent, logStudentOn, logStudentOff, ...

    User getUserById(String userID);
    Collection<User> getAllUsers();
}
