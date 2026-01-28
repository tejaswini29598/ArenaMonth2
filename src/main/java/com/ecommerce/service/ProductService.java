package com.ecommerce.service;

import com.ecommerce.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    ProductDTO createProduct(ProductDTO productDTO);

    ProductDTO updateProduct(Long id, ProductDTO productDTO);

    void deleteProduct(Long id);

    ProductDTO getProductById(Long id);

    Page<ProductDTO> getAllProducts(Pageable pageable);

    Page<ProductDTO> searchProducts(String keyword, Pageable pageable);

    Page<ProductDTO> filterByPrice(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    List<ProductDTO> getProductsByCategory(Long categoryId);

    void decreaseStock(Long productId, Integer quantity);

    void increaseStock(Long productId, Integer quantity);
}
