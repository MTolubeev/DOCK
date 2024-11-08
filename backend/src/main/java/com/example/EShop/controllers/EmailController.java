package com.example.EShop.controllers;

import com.example.EShop.models.CustomUserDetails;
import com.example.EShop.services.DefaultEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailController {

    @Autowired
    private final DefaultEmailService defaultEmailService;

    @PostMapping("/email")
    public ResponseEntity<String> sendEmail(@AuthenticationPrincipal CustomUserDetails userDetails) {
        defaultEmailService.sendEmail(userDetails);
        return ResponseEntity.ok("Email sent successful");
    }
}