package com.project.dto;

import com.project.enums.Role;
import com.project.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class AuthDTO {
    private String username;
    private String email;
    private Set<Role> roles;

    public AuthDTO(User user){
        this.username = user.getFullname();
        this.email = user.getEmail();
        this.roles = user.getRoles();
    }
}