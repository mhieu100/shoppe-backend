package com.project.model;

import com.project.enums.Gender;
import com.project.enums.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "user")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     int id;

     String fullname;
     String email;
     String password;

    @Column(name = "phone_number")
     String phoneNumber;
     String address;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
     Set<Role> roles;

    @Enumerated(EnumType.STRING)
     Gender gender;

    @Temporal(TemporalType.TIMESTAMP)
     Date birthday;

     String refreshToken;
}
