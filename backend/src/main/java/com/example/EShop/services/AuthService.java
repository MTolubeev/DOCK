package com.example.EShop.services;


import com.example.EShop.exceptions.BadCredentialsException;
import com.example.EShop.exceptions.CustomNullPointerException;
import com.example.EShop.models.CustomUserDetails;
import com.example.EShop.repositories.UserRepository;
import com.example.EShop.utils.JwtTokenUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;


@Service
public class AuthService {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public AuthService(@Lazy UserService userService,
                       JwtTokenUtils jwtTokenUtils,
                       AuthenticationManager authenticationManager,
                       UserRepository userRepository) {
        this.userService = userService;
        this.jwtTokenUtils = jwtTokenUtils;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    public String createAuthToken(String email, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userRepository.findByEmail(email).getUsername(), password));
        } catch (com.example.EShop.exceptions.BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect login or password");
        }
        try {
            CustomUserDetails customUserDetails = userService.loadUserByUsername(userRepository.findByEmail(email).getUsername());
            return jwtTokenUtils.generateToken(customUserDetails);
        } catch (CustomNullPointerException e){
            throw new CustomNullPointerException("User not found");
        }

    }
}