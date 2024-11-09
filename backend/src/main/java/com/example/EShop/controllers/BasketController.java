package com.example.EShop.controllers;

import com.example.EShop.models.CustomUserDetails;
import com.example.EShop.services.BasketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/basket")
@RequiredArgsConstructor
public class BasketController {

    private final BasketService basketService;
    @GetMapping("/myBasket")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getBasket(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(basketService.getUserProductDtos(userDetails));
    }

    @PostMapping("/addToBasket")
    public ResponseEntity<?> addToBasket(@RequestParam Long productId,
                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        basketService.addProduct(userDetails, productId);
        return ResponseEntity.ok("Product added to basket successfully");
    }

    @PostMapping("/delete/{productId}")
    public ResponseEntity<?> deleteFromBasket(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long productId) {
        basketService.deleteProduct(userDetails, productId);
        return ResponseEntity.ok("Product removed from basket successfully");
    }

    @PostMapping("/deleteFull/{productId}")
    public ResponseEntity<?> deleteFullFromBasket(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long productId) {
        basketService.deleteFullProduct(userDetails, productId);
        return ResponseEntity.ok("Product removed from basket successfully");

    }
}



//import com.example.EShop.dtos.ProductOrderDto;
//import com.example.EShop.models.Product;
//import com.example.EShop.models.User;
//import com.example.EShop.repositories.ProductRepository;
//import com.example.EShop.repositories.UserRepository;
//import com.example.EShop.services.BasketService;
//import com.example.EShop.utils.JwtTokenUtils;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/basket")
//@RequiredArgsConstructor
//public class BasketController {
//
//    private final BasketService basketService;
//    private final UserRepository userRepository;
//    private final ProductRepository productRepository;
//    private final JwtTokenUtils jwtTokenUtils;
//
//    @GetMapping("/{userId}")
//    public ResponseEntity<?> getBasket(@PathVariable Long userId,
//                                       @RequestHeader(value = "Authorization") String token) {
//        try {
//            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
//            User userFromJwt = userRepository.findByUsername(jwtTokenUtils.getUsername(token));
//            List<ProductOrderDto> products = basketService.getUserProductDtos(user);
//            if (user.getId() == userFromJwt.getId()) {
//
//                return ResponseEntity.ok(products);
//            } else {
//                return ResponseEntity.badRequest().body(" NO access");
//            }
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Error fetching basket: " + e.getMessage());
//        }
//    }
//
//
//    @PostMapping("/addToBasket")
//    public ResponseEntity<?> addToBasket(@RequestParam Long productId,
//                                         @RequestHeader(value = "Authorization") String token) {
//        try {
//            User user = userRepository.findByUsername(jwtTokenUtils.getUsername(token));
//            Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
//            basketService.addProduct(user, product);
//            return ResponseEntity.ok("Product added to basket successfully");
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Error adding product to basket: " + e.getMessage());
//        }
//    }
//
//    @PostMapping("/delete/{productId}")
//    public ResponseEntity<?> deleteFromBasket(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable Long productId) {
//        try {
//            User user = userRepository.findByUsername(jwtTokenUtils.getUsername(token));
//            Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
//
//            basketService.deleteProduct(user, product);
//            return ResponseEntity.ok("Product removed from basket successfully");
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Error removing product from basket: " + e.getMessage());
//        }
//    }
//
//    @PostMapping("/deleteFull/{productId}")
//    public ResponseEntity<?> deleteFullFromBasket(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable Long productId) {
//        try {
//            User user = userRepository.findByUsername(jwtTokenUtils.getUsername(token));
//            Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
//
//            basketService.deleteFullProduct(user, product);
//            return ResponseEntity.ok("Product removed from basket successfully");
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Error removing product from basket: " + e.getMessage());
//        }
//    }
//}
