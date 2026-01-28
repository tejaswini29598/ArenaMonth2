package com.ecommerce.service.impl;

import com.ecommerce.dto.ProductDTO;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.Category;
import com.ecommerce.model.Product;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        log.info("Creating product: {}", productDTO.getName());

        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> ResourceNotFoundException.withId("Category", productDTO.getCategoryId()));

        Product product = modelMapper.map(productDTO, Product.class);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with id: {}", savedProduct.getId());

        return mapToDTO(savedProduct);
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        log.info("Updating product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.withId("Product", id));

        if (productDTO.getCategoryId() != null && !productDTO.getCategoryId().equals(product.getCategory().getId())) {
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> ResourceNotFoundException.withId("Category", productDTO.getCategoryId()));
            product.setCategory(category);
        }

        modelMapper.map(productDTO, product);
        Product updatedProduct = productRepository.save(product);

        log.info("Product updated successfully with id: {}", id);
        return mapToDTO(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.withId("Product", id));

        productRepository.delete(product);
        log.info("Product deleted successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        log.info("Fetching product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.withId("Product", id));

        return mapToDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        log.info("Fetching all active products, page: {}", pageable.getPageNumber());

        return productRepository.findByIsActiveTrue(pageable)
                .map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> searchProducts(String keyword, Pageable pageable) {
        log.info("Searching products with keyword: {}", keyword);

        return productRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(keyword, pageable)
                .map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> filterByPrice(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        log.info("Filtering products by price range: {} - {}", minPrice, maxPrice);

        if (minPrice.compareTo(maxPrice) > 0) {
            throw new BadRequestException("Minimum price cannot be greater than maximum price");
        }

        return productRepository.findByPriceBetweenAndIsActiveTrue(minPrice, maxPrice, pageable)
                .map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByCategory(Long categoryId) {
        log.info("Fetching products for category id: {}", categoryId);

        // Verify category exists
        if (!categoryRepository.existsById(categoryId)) {
            throw ResourceNotFoundException.withId("Category", categoryId);
        }

        return productRepository.findByCategoryIdAndIsActiveTrue(categoryId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void decreaseStock(Long productId, Integer quantity) {
        log.info("Decreasing stock for product id: {} by quantity: {}", productId, quantity);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> ResourceNotFoundException.withId("Product", productId));

        if (product.getStockQuantity() < quantity) {
            throw new BadRequestException("Insufficient stock for product: " + product.getName());
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);

        log.info("Stock decreased successfully. New quantity: {}", product.getStockQuantity());
    }

    @Override
    public void increaseStock(Long productId, Integer quantity) {
        log.info("Increasing stock for product id: {} by quantity: {}", productId, quantity);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> ResourceNotFoundException.withId("Product", productId));

        product.setStockQuantity(product.getStockQuantity() + quantity);
        productRepository.save(product);

        log.info("Stock increased successfully. New quantity: {}", product.getStockQuantity());
    }

    private ProductDTO mapToDTO(Product product) {
        ProductDTO dto = modelMapper.map(product, ProductDTO.class);
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }
        return dto;
    }
}
