package com.example.EShop.services;

import com.example.EShop.models.CustomUserDetails;
import com.example.EShop.models.User;
import com.example.EShop.models.enums.Role;
import com.example.EShop.repositories.UserRepository;

import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createNewUser(String username, String surname, String email, String password) {
        if(userRepository.findByEmail(email) == null && userRepository.findByUsername(username) == null) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setSurname(surname);
            user.setPassword(passwordEncoder.encode(password));
            user.getRoles().add(Role.ROLE_USER);
            user.setActive(true);
            return userRepository.save(user);
        }
        return null;
    }

    public List<User> list() {
        return userRepository.findAll();
    }

    public char returnFirstLetter(User user) {
        return user.getUsername().charAt(0);
    }

    @Override
    @Transactional
    public CustomUserDetails loadUserByUsername(String username) throws com.example.EShop.exceptions.UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return new CustomUserDetails(
                user.getUsername(),
                user.getPassword(),
                user.getSurname(),
                user.getEmail(),
                user.getId(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getAuthority())).collect(Collectors.toList())
        );
    }
}