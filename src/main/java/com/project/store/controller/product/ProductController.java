package com.project.store.controller.product;

import com.project.store.domain.product.Category;
import com.project.store.domain.product.request.ProductRequest;
import com.project.store.domain.product.request.ProductUpdateRequest;
import com.project.store.domain.product.response.ProductResponse;
import com.project.store.domain.product.response.ProductResponseConsumer;
import com.project.store.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * @author rahul
 * Product Controller
 */
@RestController
@RequestMapping("/api/v1/product")
public class ProductController {


    @Autowired
    private ProductService productService;

    /**
     * API to create Product [ADMIN]
     *
     * @param authentication {@link Authentication}
     * @param productRequest {@link ProductRequest}
     * @return {@link ProductResponse}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/secure")
    public ResponseEntity <ProductResponse> createProduct(Authentication authentication,
                                                          @RequestBody ProductRequest productRequest) {

        if (authentication != null || authentication.isAuthenticated()) {

            String email = authentication.getName();

            ProductResponse productResponse = productService.createProductService(productRequest, email);

            return new ResponseEntity <>(productResponse, HttpStatus.OK);
        }

        return new ResponseEntity <>(HttpStatus.UNAUTHORIZED);
    }

    /**
     * API to update Product details [ADMIN]
     *
     * @param id             {@link Integer}
     * @param authentication {@link Authentication}
     * @param updateRequest  {@link ProductUpdateRequest}
     * @return {@link ProductResponse}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/secure")
    public ResponseEntity <ProductResponse> updateProduct(
            @PathVariable Integer id,
            Authentication authentication,
            @RequestBody ProductUpdateRequest updateRequest) {

        String email = authentication.getName();

        ProductResponse response =
                productService.updateProductService(id, updateRequest, email);

        return ResponseEntity.ok(response);
    }

    /**
     * Get Product By Id [ADMIN]
     *
     * @param id {@link Integer}
     * @return {@link ProductResponse}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/secure")
    public ResponseEntity <ProductResponse> getProductById(@PathVariable Integer id) {

        ProductResponse response = productService.getProductByIdService(id);

        return ResponseEntity.ok(response);
    }


    /**
     * Fetch all Products by pagination and optional filter by category  [ADMIN]
     *
     * @param category {@link Category}
     * @param pageable {@link Pageable}
     * @return {@link Page<ProductResponse>}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/page/secure")
    public ResponseEntity <Page <ProductResponse>> getProducts(
            @RequestParam(required = false) Category category,
            Pageable pageable) {

        Page <ProductResponse> response =
                productService.getAllProductsService(category, pageable);

        return ResponseEntity.ok(response);
    }


    /**
     * Soft Delete Product (Make unavailable) [ADMIN]
     *
     * @param id {@link Integer}
     * @param authentication {@link Authentication}
     * @return {@link ProductResponse}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}/secure")
    public ResponseEntity<String> deleteProduct(
            @PathVariable Integer id,
            Authentication authentication) {

        String email = authentication.getName();

        String response =
                productService.deleteProductService(id, email);

        return ResponseEntity.ok(response);
    }

    /**
     * Fetch all Products by pagination and optional filter by category  [USER]
     *
     * @param category {@link Category}
     * @param pageable {@link Pageable}
     * @return {@link Pageable<ProductResponseConsumer>}
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/consumer/secure")
    public ResponseEntity <Page <ProductResponseConsumer>> getAvailableProducts(
            @RequestParam(required = false) Category category,
            Pageable pageable) {

        Page <ProductResponseConsumer> response =
                productService.getAvailableProductsService(category, pageable);

        return ResponseEntity.ok(response);
    }

}
