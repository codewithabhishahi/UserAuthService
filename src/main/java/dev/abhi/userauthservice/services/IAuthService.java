package dev.abhi.userauthservice.services;

import dev.abhi.userauthservice.models.User;

import java.security.PublicKey;

public interface IAuthService {
       public User registerUser(String username, String email, String password);
       public User loginUser(String email, String password);
}
