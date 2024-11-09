package com.example.EShop.services;

import com.example.EShop.dtos.ProductOrderDto;
import com.example.EShop.exceptions.CustomRuntimeException;
import com.example.EShop.models.*;
import com.example.EShop.repositories.BasketRepository;
import com.example.EShop.repositories.ImageRepository;
import com.example.EShop.repositories.ProductRepository;
import com.example.EShop.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class BasketService {

    private final BasketRepository basketRepository;
    private final ImageRepository imageRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public List<ProductOrderDto> getUserProductDtos(CustomUserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername());
        Basket basket = basketRepository.findByUser(user);
        if (basket != null && basket.getProducts() != null) {
            return convertToProductOrderDtos(basket.getProducts());
        }
        return new ArrayList<>();
    }


    private List<ProductOrderDto> convertToProductOrderDtos(List<Product> products) {
        Map<Long, ProductOrderDto> productOrderDtoMap = new HashMap<>();
        for (Product product : products) {
            Image image = imageRepository.findById(product.getPreviewImageId()).orElse(null);
            String base64Image = image != null ? Base64.getEncoder().encodeToString(image.getBytes()) : null;
            ProductOrderDto dto = productOrderDtoMap.getOrDefault(product.getId(),
                    new ProductOrderDto(product.getId(),
                            product.getTitle(),
                            product.getDiscountPrice(),
                            0,
                            base64Image));
            dto.setCount(dto.getCount() + 1);
            productOrderDtoMap.put(product.getId(), dto);
        }
        return new ArrayList<>(productOrderDtoMap.values());
    }

    @Transactional
    public void addProduct(CustomUserDetails userDetails, Long productId) {
        User user = userRepository.findByUsername(userDetails.getUsername());
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        Basket basket = basketRepository.findByUser(user);
        if (product.getCount() > 0) {

            if (basket == null) {
                basket = new Basket();
                basket.setUser(user);
            }

            List<Product> products = basket.getProducts();
            if (products == null) {
                products = new ArrayList<>();
                basket.setProducts(products);
            }

            products.add(product);
            product.setCount(product.getCount() - 1);
            basketRepository.save(basket);
        }
    }

    @Transactional
    public void deleteProduct(CustomUserDetails userDetails, Long productId) {
        User user = userRepository.findByUsername(userDetails.getUsername());
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        Basket basket = basketRepository.findByUser(user);
        List<Product> products = basket.getProducts();
        products.remove(product);
        product.setCount(product.getCount() + 1);
        basketRepository.save(basket);

    }

    @Transactional
    public void deleteFullProduct(CustomUserDetails userDetails, Long productId) {
        User user = userRepository.findByUsername(userDetails.getUsername());
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        Basket basket = basketRepository.findByUser(user);
        List<Product> products = basket.getProducts();
        while (products.contains(product)) {
            products.remove(product);

            product.setCount(product.getCount() + 1);
        }
        basketRepository.save(basket);

    }




    @Transactional
    public int returnBasketSize(User user) {
        Basket basket = basketRepository.findByUser(user);
        if (basket == null) {
            basket = new Basket();
        }
        List<Product> productsInBasket = basket.getProducts();
        return productsInBasket.size();
    }

    public void cleanBasketAfterEmail(User user, Long id) {
        Basket basket = basketRepository.findByUser(user);
        List<Product> products = basket.getProducts();
        Product product = productRepository.findById(id).orElseThrow(() -> new CustomRuntimeException("Product not found"));
        while (products.contains(product)) {
            products.remove(product);
        }
        basketRepository.save(basket);
    }

}
