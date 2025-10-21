package com.mosbach.demo.data.api;

public interface User {

    void setUserId(int userId);
    int getUserId();
    void setName(String name);
    String getName();
    void setEmail(String email);
    String getEmail();
    void setPassword(String password);
    String getPassword();
    void setToken(String token);
    String getToken();

}
