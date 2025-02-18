package com.project.controller;


import com.project.dto.AuthDTO;
import com.project.model.User;
import com.project.repository.UserRepository;
import com.project.service.AuthService;
import com.project.service.UserService;
import com.project.util.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final UserService userService;
    private final JwtUtils jwtUtil;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

        User existUser = userRepository.findByEmail(user.getEmail()).orElseThrow(()->new UsernameNotFoundException("User Not Found"));
        String token = authService.generateToken(user.getEmail());
        String refreshToken = authService.generateRefreshToken(user.getEmail());

        existUser.setRefreshToken(refreshToken);
        userRepository.save(existUser);

        AuthDTO authDTO = new AuthDTO(existUser);
        return ResponseEntity.ok(Map.of(
                "user", authDTO,
                "token",token,
                "refreshToken",refreshToken
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody String refreshToken) {
        if(refreshToken == null || !jwtUtil.validateToken(refreshToken)){
            return ResponseEntity.status(403).body(Map.of("error","Invalid refresh token"));
        }

        String email = jwtUtil.extractUsername(refreshToken);
        User user = userRepository.findByEmail(email).orElseThrow();

        if(!refreshToken.equals(user.getRefreshToken())){
            return ResponseEntity.status(403).body(Map.of("error","Invalid refresh token"));
        }

        String newAccessToken = jwtUtil.generateToken((UserDetails) user);

        return ResponseEntity.ok(Map.of("accessToken",newAccessToken));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user){
        User registerUser =userService.registerUser(user);
        AuthDTO authDTO = new AuthDTO(registerUser);
        return ResponseEntity.ok(Map.of("user", authDTO));
    }
}
