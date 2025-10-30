package com.planify;

import com.planify.data.api.TaskManager;
import com.planify.data.api.UserManager;
import com.planify.data.impl.PropertyFileTaskManagerImpl;
import com.planify.data.impl.PropertyFileUserManagerImpl;
import com.planify.data.impl.TaskImpl;
import com.planify.data.impl.UserImpl;

public class LocalRun {

    public static void main(String[] args) {
        UserManager userManager = PropertyFileUserManagerImpl.getPropertyFileUserManagerImpl("src/main/resources/users.properties");
        userManager.createUser(new UserImpl(
                    0,
                "She She",
                "she@she.com",
                "123",
                "OFF"
        ));
        userManager.logUserOn("she@she.com", "123");
        TaskManager taskManager = PropertyFileTaskManagerImpl.getPropertyFileTaskManagerImpl("src/main/resources/tasks.properties");
        taskManager.addTask(new TaskImpl("Learn Mathe", 1, "you@you.com"));

    }


}
