package dev.abhi.userauthservice.services;

import dev.abhi.userauthservice.exceptions.IncorrectPasswordException;
import dev.abhi.userauthservice.exceptions.UserAlreadyExistException;
import dev.abhi.userauthservice.exceptions.UserNotRegisteredException;
import dev.abhi.userauthservice.models.Role;
import dev.abhi.userauthservice.models.Session;
import dev.abhi.userauthservice.models.State;
import dev.abhi.userauthservice.models.User;
import dev.abhi.userauthservice.pojo.UserToken;
import dev.abhi.userauthservice.repositories.RoleRepository;
import dev.abhi.userauthservice.repositories.SessionRepository;
import dev.abhi.userauthservice.repositories.UserRepository;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public User registerUser(String name, String email, String password) {
        // TODO Auto-generated method stub
        Optional<User> optionalUser  = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            throw new UserAlreadyExistException("User with email " + email + " already exists");
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setState(State.ACTIVE);

        Role role;
        Optional<Role> optionalRole = roleRepository.findByRoleName("DEFAULT");

        if (optionalRole.isEmpty()) {
            role = new Role();
            role.setRoleName("DEFAULT");
            role.setCreatedAt((new Date()));
            role.setUpdatedAt((new Date()));
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
    public UserToken loginUser(String email, String password) {
        // TODO Auto-generated method stub

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new UserNotRegisteredException("User with email " + email + " does not exist");
        }

        User user = optionalUser.get();

        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new IncorrectPasswordException("Invalid password for email " + email);

        }else {
            //JWT token generation logic here
            //payload for JWT token
            Map<String , Object> payload = new HashMap<>();
            Long currentTimeMills = System.currentTimeMillis();
            payload.put("issued_at", currentTimeMills);
            payload.put("expiry", currentTimeMills + 3600000); // Token valid for 1 hour
            payload.put("userId", user.getId());
            payload.put("issuer", "Google");
            payload.put("scope", user.getRole());

            // Header for JWT token
            MacAlgorithm algorithm = Jwts.SIG.HS256;

            // Secret key for signing the JWT token (In production, use a secure key and store it safely)
            SecretKey secretKey = algorithm.key().build();

            String token = Jwts.builder()
                    .setHeaderParam("typ", "JWT")
                    .setClaims(payload)
                    .signWith(secretKey)
                    .compact();

            /*/ Store the session in the database
             * In a real application, you might want to store additional information such as token expiry time, user agent, IP address, etc.
             * This will help in managing sessions and implementing features like logout and session invalidation.
             */
            Session session = new Session();
            session.setToken(token);
            session.setExpiresAt(currentTimeMills + 3600000); // Set the expiry time for the session (1 hour)
            session.setUser(user);
            session.setState(State.ACTIVE);
            session.setCreatedAt(new Date());
            session.setUpdatedAt(new Date());
            sessionRepository.save(session);

            return new UserToken(user, token);
        }
    }
}
