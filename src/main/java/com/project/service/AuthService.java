package com.project.service;

import com.project.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtils jwtUtil;
    private final UserDetailsService userDetailsService;

    public String generateToken(String username){
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if(userDetails == null){
            throw new UsernameNotFoundException("User not found");
        }
        return jwtUtil.generateToken(userDetails);
    }

    public String generateRefreshToken(String username){
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if(userDetails == null){
            throw new UsernameNotFoundException("User not found");
        }
        return jwtUtil.generateRefreshToken(userDetails);
    }
}
