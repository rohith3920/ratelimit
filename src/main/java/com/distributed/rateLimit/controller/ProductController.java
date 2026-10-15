
package com.distributed.rateLimit.controller;

import com.distributed.rateLimit.entity.Product;
import com.distributed.rateLimit.event.RateLimitEvent;
import com.distributed.rateLimit.repository.ProductRepository;
import com.distributed.rateLimit.service.RateLimitEventProducer;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final RateLimitEventProducer eventProducer;

    public ProductController(
            ProductRepository productRepository,
            RateLimitEventProducer eventProducer) {
        this.productRepository = productRepository;
        this.eventProducer = eventProducer;
    }

    // Publish a Kafka event for each product API request
    private void publishEvent(String path, boolean allowed) {
        RateLimitEvent event = new RateLimitEvent(
                "test-user-123",
                path,
                allowed,
                Instant.now()
        );

        eventProducer.publishEvent(event);
    }

    // Get all products
    @GetMapping
    public ResponseEntity<List<Product>> getProducts() {
        publishEvent("/api/products", true);

        List<Product> products = productRepository.findAll();
        return ResponseEntity.ok(products);
    }

    // Get product by ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(
            @PathVariable Long id) {

        publishEvent("/api/products/" + id, true);

        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Add a new product
    @PostMapping
    public ResponseEntity<Product> addProduct(
            @Valid @RequestBody Product product) {

        publishEvent("/api/products", true);

        Product savedProduct = productRepository.save(product);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedProduct);
    }

    // Update an existing product
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody Product updatedProduct) {

        publishEvent("/api/products/" + id, true);

        return productRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setName(updatedProduct.getName());
                    existingProduct.setPrice(updatedProduct.getPrice());

                    Product savedProduct =
                            productRepository.save(existingProduct);

                    return ResponseEntity.ok(savedProduct);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete a product
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id) {

        publishEvent("/api/products/" + id, true);

        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
