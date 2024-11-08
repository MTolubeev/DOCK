package com.example.EShop.controllers;

import com.example.EShop.dtos.ImageDto;
import com.example.EShop.models.Image;
import com.example.EShop.repositories.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
@RequiredArgsConstructor
public class ImageController {
    private final ImageRepository imageRepository;

    @GetMapping("/images/{id}")
    private ResponseEntity<ImageDto> getImageById(@PathVariable Long id) {
        Image image = imageRepository.findById(id).orElse(null);
        if (image == null) {
            return ResponseEntity.notFound().build();
        }

        String base64Image = Base64.getEncoder().encodeToString(image.getBytes());

        ImageDto imageDto = new ImageDto(
                image.getId(),
                image.getOriginalFileName(),
                image.getContentType(),
                image.getSize(),
                base64Image
        );
        return ResponseEntity.ok(imageDto);
    }
}