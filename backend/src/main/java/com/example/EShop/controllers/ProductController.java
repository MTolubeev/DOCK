package com.example.EShop.controllers;

import com.example.EShop.dtos.CategoryDto;
import com.example.EShop.dtos.ProductChangeDto;
import com.example.EShop.dtos.ProductDto;
import com.example.EShop.models.*;
import com.example.EShop.repositories.ImageRepository;
import com.example.EShop.repositories.ProductRepository;
import com.example.EShop.repositories.UserRepository;
import com.example.EShop.services.BasketService;
import com.example.EShop.services.CommentService;
import com.example.EShop.services.ProductService;
import com.example.EShop.services.UserService;
import com.example.EShop.utils.JwtTokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/")
    public ResponseEntity<?> products(@RequestParam(name = "title", required = false) String title,
                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        return productService.forMainPage(userDetails);
    }

    @GetMapping("/product/getAll")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<ProductDto> products = productService.findAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/product/getAll/{id}")
    public ResponseEntity<ProductDto> getAllProducts(@PathVariable Long id) {
        ProductDto product = productService.findAllProducts(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping("/product/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createProduct(@RequestParam(required = false) MultipartFile file1,
                                           @ModelAttribute Product product,
                                           @RequestParam String category,
                                           @RequestParam(required = false) String subcategory,
                                           @RequestParam(required = false) String subsubcategory,
                                           @RequestHeader(value = "Authorization", required = false) String token) throws IOException {
        productService.saveProduct(product, category, subcategory, subsubcategory, file1);
        return ResponseEntity.status(HttpStatus.CREATED).body("Product created successfully");
    }

    @PostMapping("/product/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id,
                                           @RequestHeader(value = "Authorization", required = false) String token) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }

    @PutMapping("/product/categories/reorder")
    public ResponseEntity<Void> reorderCategories(
            @RequestBody HashMap<Long, List<CategoryDto>> productCategories) {

        for (Map.Entry<Long, List<CategoryDto>> entry : productCategories.entrySet()) {
            Long productId = entry.getKey();
            List<CategoryDto> newOrder = entry.getValue();

            productService.reorderCategories(productId, newOrder);
        }

        return ResponseEntity.ok().build();
    }
    @PutMapping(value = "/product/change", consumes = "multipart/form-data")
    public ResponseEntity<?> changeProduct(@RequestPart("productData") String productData,
                                           @RequestPart(value = "images", required = false) MultipartFile images) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ProductChangeDto productChangeDto = objectMapper.readValue(productData, ProductChangeDto.class);
        productService.changeProduct(productChangeDto, images);
        return ResponseEntity.ok("Product was changed successfully");
    }
}