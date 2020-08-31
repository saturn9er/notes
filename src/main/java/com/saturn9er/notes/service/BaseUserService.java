package com.saturn9er.notes.service;

import com.saturn9er.notes.model.User;

public interface BaseUserService {
    User findByUsername(String username);

    void save(User user);
}
