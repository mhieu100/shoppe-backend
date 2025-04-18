package com.project.controller;

import com.project.dto.AuthDTO;
import com.project.dto.ProfileDTO;
import com.project.model.User;
import com.project.repository.UserRepository;
import com.project.service.AuthService;
import com.project.service.UserService;
import com.project.util.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpHeaders;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final AuthService authService;
    private final UserService userService;
    private final JwtUtils jwtUtil;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                user.getEmail(), user.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User existUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        String access_token = authService.generateToken(user.getEmail());
        String refresh_token = authService.generateRefreshToken(user.getEmail());

        existUser.setRefreshToken(refresh_token);
        userRepository.save(existUser);

        AuthDTO authDTO = new AuthDTO(existUser);

        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(8640000)
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, resCookies.toString()).body(Map.of(
                "user", authDTO,
                "access_token", access_token));
    }

    @GetMapping("/account")
    public ResponseEntity<?> getAll() {
        String email = JwtUtils.getCurrentUserLogin().isPresent() ? JwtUtils.getCurrentUserLogin().get() : "";
        User user = userRepository.findByEmail(email).get();
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setId(user.getId());
        profileDTO.setEmail(user.getEmail());
        profileDTO.setFullname(user.getFullname());
        profileDTO.setPhoneNumber(user.getPhoneNumber());
        profileDTO.setAddress(user.getAddress());
        profileDTO.setRoles(user.getRoles());
        profileDTO.setGender(user.getGender());
        profileDTO.setBirthday(user.getBirthday());
        return ResponseEntity.ok().body(profileDTO);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(name = "refresh_token", defaultValue = "empty") String refresh_token) {
        if (refresh_token.equals("empty") || !jwtUtil.validateToken(refresh_token)) {
            return ResponseEntity.status(403).body(Map.of("error", "Invalid refresh token"));
        }
        String email = jwtUtil.extractUsername(refresh_token);
        User user = userRepository.findByEmail(email).orElseThrow();

        if (!refresh_token.equals(user.getRefreshToken())) {
            return ResponseEntity.status(403).body(Map.of("error", "Invalid refresh token"));
        }

        String new_access_token = jwtUtil.generateToken((UserDetails) user);
        String new_refresh_token = authService.generateRefreshToken(user.getEmail());

        user.setRefreshToken(new_refresh_token);
        userRepository.save(user);

        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", new_refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(8640000)
                .build();


        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, resCookies.toString()).body(Map.of("access_token", new_access_token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        User registerUser = userService.registerUser(user);
        AuthDTO authDTO = new AuthDTO(registerUser);
        return ResponseEntity.ok(Map.of("user", authDTO));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        String email = JwtUtils.getCurrentUserLogin().isPresent() ? JwtUtils.getCurrentUserLogin().get() : "";
        User user = userRepository.findByEmail(email).get();
        user.setRefreshToken("");
        userRepository.save(user);
        @SuppressWarnings("null")
        ResponseCookie deleteSpringCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString()).body(Map.of("message", "Logout success"));
    }
}
