package com.example.EShop.repositories;

import com.example.EShop.models.Basket;
import com.example.EShop.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface BasketRepository extends JpaRepository<Basket, Long> {
    Basket findByUser(User user);
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM basket_products WHERE product_id =:productId", nativeQuery = true)
    void deleteProductFromBasketProducts(@Param("productId") Long productId);
}