package com.example.EShop.controllers;

import com.example.EShop.dtos.CommentDto;
import com.example.EShop.models.Comment;
import com.example.EShop.models.CustomUserDetails;
import com.example.EShop.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Comment> addComment(@RequestParam("productId") Long productId,
                                              @RequestParam("text") String text,
                                              @RequestParam("score") int score,
                                              @RequestParam(value = "images", required = false) MultipartFile[] images,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {
        return ResponseEntity.ok(commentService.addComment(text, score, images, userDetails, productId));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long productId) {
        return ResponseEntity.ok(commentService.findByProduct(productId));
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.delete(commentId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/usersComment/{commentId}")
    public ResponseEntity<?> deleteCommentByUser(@PathVariable Long commentId,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.delete(commentId, userDetails);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteImage/{commentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCommentImage(@PathVariable Long commentId,
                                                   @RequestParam Long commentImageId) {
        commentService.deleteImage(commentId, commentImageId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getAllComments")
    public ResponseEntity<List<CommentDto>> getAllComments() {
        return ResponseEntity.ok((List<CommentDto>) commentService.getAllComments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getOneComment(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getOneComment(id));
    }
}