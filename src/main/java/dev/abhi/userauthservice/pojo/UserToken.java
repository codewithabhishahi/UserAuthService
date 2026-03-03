package dev.abhi.userauthservice.pojo;

import dev.abhi.userauthservice.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserToken {

    private User user;
    private String token;

}
