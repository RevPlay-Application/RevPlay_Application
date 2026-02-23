package com.revature.revplay.service;

import com.revature.revplay.model.User;
import java.util.List;

public interface UserService {
    User registerUser(User user);

    User updateUser(User user);

    User getUserById(Long id);

    User getUserByUsername(String username);

    User getUserByEmail(String email);

    List<User> getAllUsers();

    void deleteUser(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
