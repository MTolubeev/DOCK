package com.example.EShop.controllers;

import com.example.EShop.dtos.AuthDto;
import com.example.EShop.dtos.SafeLoginDto;
import com.example.EShop.dtos.UserDto;

import com.example.EShop.repositories.UserRepository;
import com.example.EShop.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import com.example.EShop.models.User;
import com.example.EShop.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @Lazy
    @Autowired
    private UserService userService;

    @Lazy
    @Autowired
    private AuthService authService;

//    @PostMapping("/login")
//    public ResponseEntity<?> auth(@RequestBody SafeLoginDto safeLoginDto) {
//
//        try {
//            String token = authService.createAuthToken(safeLoginDto.getEmail(), safeLoginDto.getPassword());
//            User user = userRepository.findByEmail(safeLoginDto.getEmail());
//            return ResponseEntity.ok(new AuthDto(user, token));
//        } catch (BadCredentialsException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неправильный логин или пароль");
//        }
//    }
    @PostMapping("/login")
    public ResponseEntity<?> auth(@RequestParam(name = "email") String email, @RequestParam(name = "password") String password) {

        try {
            String token = authService.createAuthToken(email, password);
            User user = userRepository.findByEmail(email);
            return ResponseEntity.ok(new AuthDto(user, token));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неправильный логин или пароль");
        }
    }

    @PostMapping("/registration")
    public ResponseEntity<?> createNewUser(@RequestParam(name = "username") String username,
                                           @RequestParam(name = "surname") String surname,
                                           @RequestParam(name = "email") String email,
                                           @RequestParam(name = "password") String password) {
        User user = userService.createNewUser(username, surname, email, password);
        try {
            String token = authService.createAuthToken(user.getEmail(), user.getPassword());
            User user2 = userRepository.findByEmail(email);
            return ResponseEntity.ok(new AuthDto(user2, token));
            // return ResponseEntity.ok(new UserDto(user.getId(), user.getUsername(), user.getEmail()));
        } catch (NullPointerException e) {
            throw new NullPointerException();
        }

    }
}