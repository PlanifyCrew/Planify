package com.planify.data.impl;

import com.planify.data.api.User;

public class UserImpl implements User {

    int userId;
    String name;
    String email;
    String password;
    String token;

    public UserImpl(int userId, String name, String email, String password, String token) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.token = token;
    }

    @Override
    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public int getUserId() {
        return userId;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String getToken() {
        return token;
    }
}
