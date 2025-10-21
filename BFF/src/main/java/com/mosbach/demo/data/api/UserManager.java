package com.mosbach.demo.data.api;

public interface UserManager {

    int createUser(User user);
    String logUserOn(String email, String password);
    boolean logUserOff(String email);
    int getUserIdFromToken(String token);

}
