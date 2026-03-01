package dev.abhi.userauthservice.models;

import dev.abhi.userauthservice.dtos.UserDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Entity
@Getter
@Setter
public class User extends BaseModel {


    private String name;
    private String password;
    private String email;

    @ManyToMany
    private List<Role> role;


    public UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setRoles(user.getRole());
        return userDTO;

    }
}
