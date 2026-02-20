package com.project.store.controller.product;

import com.project.store.domain.product.request.ProductRequest;
import com.project.store.domain.product.response.ProductResponse;
import com.project.store.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductController {


    @Autowired
    private ProductService productService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create/secure")
    public ResponseEntity<ProductResponse> createProduct(Authentication authentication,
                                                         @RequestBody ProductRequest productRequest){

        if (authentication != null || authentication.isAuthenticated()) {

            String email= authentication.getName();

           ProductResponse productResponse= productService.createProductService(productRequest,email);

            return new ResponseEntity <>(productResponse, HttpStatus.OK);
        }

        return new ResponseEntity <>(HttpStatus.UNAUTHORIZED);
    }
}
