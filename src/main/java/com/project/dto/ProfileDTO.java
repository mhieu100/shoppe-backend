package com.project.dto;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.project.enums.Gender;
import com.project.enums.Role;

import lombok.Data;

@Data
public class ProfileDTO {
    Integer id;
    String fullname;
    String email;
    String phoneNumber;
    String address;
    Set<Role> roles;
    Gender gender;
    Date birthday;
}
