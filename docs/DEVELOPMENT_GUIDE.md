# Development Guide

## 📝 Code Style Guidelines

### Java Conventions
- Follow Google Java Style Guide
- Use meaningful variable names
- Maximum line length: 120 characters
- Use 4 spaces for indentation

### Naming Conventions
- Classes: PascalCase (e.g., `ProductController`)
- Variables: camelCase (e.g., `productName`)
- Constants: UPPER_SNAKE_CASE (e.g., `DEFAULT_PAGE_SIZE`)
- Packages: lowercase with dots (e.g., `com.ecommerce.service`)

### Annotations
```java
// Use on classes
@Service
@Repository
@RestController
@Configuration

// Use on methods
@Override
@Transactional
@Transactional(readOnly = true)
@PostMapping
@GetMapping
@PutMapping
@DeleteMapping

// Use on fields
@Autowired
@Inject
@Value
@NotNull
@NotBlank
@Email
```

## 🏗️ Project Structure Best Practices

### Package Organization
```
com.ecommerce
├── config/          # Spring configuration
├── controller/      # REST controllers
├── service/         # Business logic (interface first)
├── service/impl/    # Service implementations
├── repository/      # Data access
├── model/           # JPA entities
├── dto/             # Data transfer objects
├── security/        # Security-related classes
├── exception/       # Custom exceptions & handlers
└── util/            # Utility classes
```

### Layered Architecture
```
Request
  ↓
Controller (REST endpoint)
  ↓
Service Interface (Business logic contract)
  ↓
Service Implementation (Business logic)
  ↓
Repository (Data access)
  ↓
Database
```

## 🔒 Security Best Practices

### Password Handling
```java
@Service
public class UserService {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User createUser(UserDTO dto) {
        // Never store plain text passwords
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }
}
```

### Input Validation
```java
@PostMapping
public ResponseEntity<ProductDTO> createProduct(
        @Valid @RequestBody ProductDTO productDTO) {
    // Validation happens automatically
    ProductDTO created = productService.createProduct(productDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
}
```

### Entity Protection
```java
@RestController
public class ProductController {
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
        // Only return DTOs, never expose entities
        ProductDTO dto = productService.getProductById(id);
        return ResponseEntity.ok(dto);
    }
}
```

## 🧪 Testing Guidelines

### Unit Testing
```java
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @InjectMocks
    private ProductServiceImpl productService;
    
    @Test
    void testCreateProduct() {
        // Arrange
        ProductDTO inputDTO = ProductDTO.builder().name("Test Product").build();
        
        // Act
        ProductDTO result = productService.createProduct(inputDTO);
        
        // Assert
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
    }
}
```

### Integration Testing
```java
@SpringBootTest
@Transactional
public class ProductControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testGetAllProducts() throws Exception {
        mockMvc.perform(get("/products"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content").isArray());
    }
}
```

## 📝 Logging Best Practices

### Use SLF4J with Lombok
```java
@Service
@Slf4j
public class ProductService {
    
    public void createProduct(ProductDTO dto) {
        log.info("Creating product: {}", dto.getName());
        
        try {
            // Business logic
            log.info("Product created successfully");
        } catch (Exception e) {
            log.error("Error creating product", e);
        }
    }
}
```

### Logging Levels
- `TRACE`: Very detailed debugging
- `DEBUG`: Detailed information for debugging
- `INFO`: General informational messages
- `WARN`: Warning messages for potential issues
- `ERROR`: Error messages
- `FATAL`: Critical errors

## 🎯 Error Handling

### Custom Exceptions
```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public static ResourceNotFoundException withId(String resource, Long id) {
        return new ResourceNotFoundException(
            resource + " not found with id: " + id
        );
    }
}
```

### Global Exception Handler
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(404, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
```

## 🔄 Transaction Management

### Service Layer Transactions
```java
@Service
@Transactional
public class OrderService {
    
    // Read-only transaction
    @Transactional(readOnly = true)
    public OrderDTO getOrder(Long id) {
        return orderRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow();
    }
    
    // Write transaction
    @Transactional
    public OrderDTO createOrder(OrderDTO dto) {
        // Database modifications
        return orderRepository.save(order);
    }
}
```

## 🗄️ Database Best Practices

### Entity Design
```java
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false)
    private String name;
    
    // Use lazy loading for relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```

### Query Methods
```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Derived query methods
    List<Product> findByNameContainingIgnoreCase(String name);
    Page<Product> findByIsActiveTrue(Pageable pageable);
    
    // JPQL queries
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max")
    Page<Product> findByPriceRange(
        @Param("min") BigDecimal min,
        @Param("max") BigDecimal max,
        Pageable pageable
    );
}
```

## 📦 Dependency Injection

### Constructor Injection (Recommended)
```java
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    
    // No need for constructor or field injection
}
```

### Field Injection (Acceptable for Configuration)
```java
@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
}
```

## 🚀 Performance Optimization

### Pagination
```java
@GetMapping
public Page<ProductDTO> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    return productService.findAll(pageable);
}
```

### Lazy Loading
```java
@ManyToOne(fetch = FetchType.LAZY)
private Category category;
```

### Query Optimization
```java
@Query("SELECT p FROM Product p " +
       "LEFT JOIN FETCH p.category " +
       "WHERE p.isActive = true")
List<Product> findAllActive();
```

## 📚 Documentation

### JavaDoc Comments
```java
/**
 * Creates a new product in the system.
 *
 * @param productDTO the product data transfer object
 * @return the created product DTO with assigned ID
 * @throws BadRequestException if product data is invalid
 * @throws ResourceNotFoundException if category does not exist
 */
public ProductDTO createProduct(ProductDTO productDTO) {
    // Implementation
}
```

### API Documentation with Swagger
```java
@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "Product management API")
public class ProductController {
    
    @GetMapping
    @Operation(summary = "Get all products")
    @ApiResponse(responseCode = "200", description = "List of products")
    public ResponseEntity<Page<ProductDTO>> getAll(...) {
        // Implementation
    }
}
```

## 🔄 Git Workflow

### Commit Message Format
```
feat: Add user registration endpoint
fix: Fix product search filtering
docs: Update API documentation
test: Add unit tests for ProductService
refactor: Reorganize controller structure
```

### Branch Naming
- `feature/feature-name`
- `bugfix/bug-name`
- `hotfix/urgent-fix`
- `docs/documentation-update`

---

**Version:** 1.0.0  
**Last Updated:** January 2026
