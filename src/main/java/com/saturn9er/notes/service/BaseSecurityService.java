package com.saturn9er.notes.service;

public interface BaseSecurityService {
    String findLoggedInUsername();

    void autoLogin(String username, String password);
}
