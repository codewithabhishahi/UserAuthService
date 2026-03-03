package dev.abhi.userauthservice.services;

import dev.abhi.userauthservice.models.User;
import dev.abhi.userauthservice.pojo.UserToken;

import java.security.PublicKey;

public interface IAuthService {
       public User registerUser(String name, String email, String password);
       public UserToken loginUser(String email, String password);
       public Boolean validateToken(String token);
}
