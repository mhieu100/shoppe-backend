package com.project.config;

import com.project.enums.Role;
import com.project.model.User;
import com.project.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner initAdmin(UserRepository userReponsitory, PasswordEncoder passwordEncoder){
        return args -> {
            String adminEmail = "admin@gmail.com";

            if(userReponsitory.findByEmail(adminEmail).isEmpty()){
                User adminUser = new User();
                adminUser.setFullname("Admin");
                adminUser.setEmail(adminEmail);
                adminUser.setPassword(passwordEncoder.encode("admin123"));
                adminUser.setRoles(Collections.singleton(Role.ADMIN));

                userReponsitory.save(adminUser);

                System.out.print("✅ admin created successfully");
            }
            else {
                System.out.println("admin already exists, skipping creation");
            }
        };
    }
}