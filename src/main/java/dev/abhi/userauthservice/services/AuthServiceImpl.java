package dev.abhi.userauthservice.services;

import dev.abhi.userauthservice.exceptions.IncorrectPasswordException;
import dev.abhi.userauthservice.exceptions.UserAlreadyExistException;
import dev.abhi.userauthservice.exceptions.UserNotRegisteredException;
import dev.abhi.userauthservice.models.Role;
import dev.abhi.userauthservice.models.State;
import dev.abhi.userauthservice.models.User;
import dev.abhi.userauthservice.repositories.RoleRepository;
import dev.abhi.userauthservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public User registerUser(String username, String email, String password) {
        // TODO Auto-generated method stub
        Optional<User> optionalUser  = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            throw new UserAlreadyExistException("User with email " + email + " already exists");
        }
        User user = new User();
        user.setName(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setState(State.ACTIVE);

        Role role;
        Optional<Role> optionalRole = roleRepository.findByRoleName("DEFAULT");

        if (optionalRole.isEmpty()) {
            role = new Role();
            role.setRoleName("DEFAULT");
            role.setState(State.ACTIVE);
            roleRepository.save(role);

        }else{
            role = optionalRole.get();
        }

        List<Role> roles = new ArrayList<>();
            roles.add(role);

        user.setRole(roles);

        return userRepository.save(user);
    }

    @Override
    public User loginUser(String email, String password) {
        // TODO Auto-generated method stub

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new UserNotRegisteredException("User with email " + email + " does not exist");
        }

        User user = optionalUser.get();

        if (!user.getPassword().equals(password)) {
            throw new IncorrectPasswordException("Invalid password for email " + email);

        }else {
            return user;
        }
    }
}
