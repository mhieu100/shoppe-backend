package com.project.dto;

import java.util.Date;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.enums.Gender;
import com.project.enums.Role;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO {
    Integer id;
    String fullname;
    String email;
    String phoneNumber;
    String address;
    Set<Role> roles;
    Gender gender;
    Date birthday;

}
