package com.example.EShop.services;

import com.example.EShop.dtos.CommentDto;
import com.example.EShop.exceptions.CustomRuntimeException;
import com.example.EShop.models.*;
import com.example.EShop.repositories.CommentRepository;
import com.example.EShop.repositories.ProductRepository;
import com.example.EShop.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Value("${image.upload.dir.for.com}")
    private  String uploadDirForCom;

    public List<Comment> findByProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        return commentRepository.findByProduct(product);
    }

    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    public void delete(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));
        commentRepository.delete(comment);
    }

    public ResponseEntity<String> delete(Long commentId, CustomUserDetails userDetails) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));
        User user = userRepository.findByUsername(userDetails.getUsername());
        if (comment.getUser().getId() == user.getId()) {
            commentRepository.delete(comment);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("That's not your comment.");
        }
        return null;
    }

    public void deleteImage(Long commentId, Long id) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CustomRuntimeException("Comment not found"));
        List<CommentImage> images = comment.getImages();

        if (images != null && !images.isEmpty()) {
            images.removeIf(image -> image.getId().equals(id));
            comment.setImages(images);
            commentRepository.save(comment);
        } else {
            throw new CustomRuntimeException("The comment has no photo to delete.");
        }
    }

    public Comment addComment(String text, int score, MultipartFile[] images, CustomUserDetails userDetails, Long productId) throws IOException {
        User user = userRepository.findByUsername(userDetails.getUsername());
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setProduct(product);
        comment.setText(text);
        comment.setScore(score);

        if (comment.getImages().isEmpty()) {
            comment.setImages(new ArrayList<>());
        }

        if (images != null) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String imageUrl = saveImage(image);
                    CommentImage commentImage = new CommentImage();
                    commentImage.setImageUrl(imageUrl);
                    commentImage.setComment(comment);
                    commentImage.setBytes(image.getBytes());
                    comment.getImages().add(commentImage);
                }
            }
        }
        return commentRepository.save(comment);
    }

    private String saveImage(MultipartFile image) throws IOException {
        Path imageDirectory = Paths.get(uploadDirForCom);

        if (!Files.exists(imageDirectory)) {
            Files.createDirectories(imageDirectory); // Создаем директорию, если ее нет
        }

        String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
        Path imagePath = imageDirectory.resolve(fileName);

        Files.copy(image.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);

        return "/" + uploadDirForCom + fileName;
    }

    public  Collection<CommentDto> getAllComments(){
        List<Comment> comments = commentRepository.findAll();
        return comments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public  CommentDto getOneComment(Long id){
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new RuntimeException("No comment with this id"));
        return convertToDto(comment);
    }

    public CommentDto convertToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setScore(comment.getScore());
        dto.setUserId(comment.getUser().getId());
        dto.setUsername(comment.getUser().getUsername());
        dto.setProductId(comment.getProduct().getId());
        dto.setProductTitle(comment.getProduct().getTitle());

        // Если изображения не инициализированы, возвращаем пустой список
        if (comment.getImages() == null) {
            dto.setImages(new ArrayList<>());
        } else {
            dto.setImages(comment.getImages());
        }

        return dto;
    }
}
