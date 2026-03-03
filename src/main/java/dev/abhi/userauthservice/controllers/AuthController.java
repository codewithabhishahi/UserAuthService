package dev.abhi.userauthservice.controllers;


import dev.abhi.userauthservice.dtos.LoginRequestDTO;
import dev.abhi.userauthservice.dtos.SignupRequestDTO;
import dev.abhi.userauthservice.dtos.UserDTO;
import dev.abhi.userauthservice.dtos.UserDTO;
import dev.abhi.userauthservice.models.User;
import dev.abhi.userauthservice.pojo.UserToken;
import org.springframework.beans.factory.annotation.Autowired;
import dev.abhi.userauthservice.services.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @PostMapping("/signup")
    ResponseEntity<UserDTO> signup(@RequestBody SignupRequestDTO signupRequestDTO) {
        System.out.println("****************signup***************");
        try{
        User user  = authService.registerUser(signupRequestDTO.getName(),
                signupRequestDTO.getEmail(),
                signupRequestDTO.getPassword());

        UserDTO userDTO = user.convertToUserDTO(user);
        ResponseEntity responseEntity = new ResponseEntity(userDTO, HttpStatus.CREATED);

        return  responseEntity;
        }catch (Exception e){
            ResponseEntity responseEntity = new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
            return  responseEntity;
        }
    }

    @PostMapping("/login")
    ResponseEntity<UserDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        System.out.println("****************login***************");
        try{
        UserToken userToken = authService.loginUser(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());
        UserDTO userDTO = userToken.getUser().convertToUserDTO(userToken.getUser());

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.COOKIE, userToken.getToken());

        HttpHeaders httpHeaders = new HttpHeaders(headers);

        ResponseEntity responseEntity = new ResponseEntity(userDTO, httpHeaders, HttpStatus.OK);
        return  responseEntity;
        }catch (Exception e){
            ResponseEntity responseEntity = new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
            return  responseEntity;
        }
    }

}
