package com.mosbach.demo.data.api;
import java.util.List;

public interface UserManager {

    int createUser(User user);
    String logUserOn(String email, String password);
    boolean logUserOff(String email);
    int getUserIdFromToken(String token);
    List<Integer> getUserIdsFromEmails(List<String> emails);

}
