package com.example.EShop.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductChangeDto {
    private Long productId;
    private String newTitle ;
    private String newDescription;
    private Integer newCount ;
    private Long newPrice;
    private Long newDiscountPrice;
    private String newCategory;
    private String newSubCategory;
    private String newSubSubCategory;
}
