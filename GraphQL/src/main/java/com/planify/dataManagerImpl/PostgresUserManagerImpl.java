package com.planify.dataManagerImpl;

import com.planify.dataManager.UserManager;
import com.planify.dataManager.EventManager;
import com.planify.model.user.User;
import com.planify.model.event.Event;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

public class PostgresUserManagerImpl implements UserManager {

    String fileName;

    static PostgresUserManagerImpl PostgresUserManager = null;

    private PostgresUserManagerImpl(String fileName) {
        this.fileName = fileName;
    }

    static public PostgresUserManagerImpl getPostgresUserManagerImpl(String fileName) {
        if (PostgresUserManager == null)
            PostgresUserManager = new PostgresUserManagerImpl(fileName);
        return PostgresUserManager;
    }


    @Override
    public User getUserById(String userID) {

        Properties properties = new Properties();
        int i = 1;
        User user = null;

        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            try(InputStream resourceStream = loader.getResourceAsStream(fileName)) {
                properties.load(resourceStream);
            }

            while (properties.containsKey("User." + i + ".firstname")) {

                    if (properties.getProperty("User." + i + ".ID").equals(userID)) {
                       user = new User(
                               userID,
                               properties.getProperty("Event." + i + ".firstname"),
                               properties.getProperty("Event." + i + ".lastname"));
                    }
                    i++;
                }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }


    @Override
    public Collection<User> getAllUsers() {
        Properties properties = new Properties();
        int i = 1;
        List<User> users = new ArrayList<>();
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            try(InputStream resourceStream = loader.getResourceAsStream(fileName)) {
                properties.load(resourceStream);
            }

            while (properties.containsKey("User." + i + ".firstname")) {
                users.add(
                        new User(
                            properties.getProperty("User." + i + ".id"),
                            properties.getProperty("User." + i + ".firstname"),
                            properties.getProperty("User." + i + ".lastname"))
                    );
                i++;
                }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return users;

    }

}
