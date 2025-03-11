package com.jocata.oms.products.controller;


import com.jocata.oms.common.request.GenericRequestPayload;
import com.jocata.oms.common.response.GenericResponsePayload;
import com.jocata.oms.datamodel.product.form.ProductForm;
import com.jocata.oms.products.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/product/create")
    public ResponseEntity<GenericResponsePayload<ProductForm>> createProduct(@RequestBody GenericRequestPayload<ProductForm> request) {
        ProductForm product = productService.createProduct(request.getData());
        return new ResponseEntity<GenericResponsePayload<ProductForm>>(
                new GenericResponsePayload<ProductForm>(request.getRequestId(),
                        String.valueOf(Timestamp.from(Instant.now())),
                        HttpStatus.CREATED.toString(),
                        HttpStatus.Series.SUCCESSFUL.toString()
                        , product),
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<GenericResponsePayload<List<ProductForm>>> getAllProducts() {
        List<ProductForm> allProducts = productService.getAllProducts();
        return new ResponseEntity<GenericResponsePayload<List<ProductForm>>>(
                new GenericResponsePayload<List<ProductForm>>(UUID.randomUUID().toString(),
                        String.valueOf(Timestamp.from(Instant.now())),
                        HttpStatus.FOUND.toString(),
                        HttpStatus.Series.SUCCESSFUL.toString()
                        , allProducts),
                HttpStatus.OK);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<GenericResponsePayload<ProductForm>> updateProduct(@PathVariable Integer productId,
                                                                             @RequestBody GenericRequestPayload<ProductForm> request) {
        ProductForm product = productService.updateProduct(productId, request.getData());
        return new ResponseEntity<GenericResponsePayload<ProductForm>>(
                new GenericResponsePayload<ProductForm>(request.getRequestId(),
                        String.valueOf(Timestamp.from(Instant.now())),
                        HttpStatus.OK.toString(),
                        HttpStatus.Series.SUCCESSFUL.toString()
                        , product),
                HttpStatus.OK);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<GenericResponsePayload<String>> deleteProduct(@PathVariable Integer productId) {
        productService.deleteProduct(productId);
        return new ResponseEntity<GenericResponsePayload<String>>(
                new GenericResponsePayload<String>(UUID.randomUUID().toString(),
                        String.valueOf(Timestamp.from(Instant.now())),
                        HttpStatus.OK.toString(),
                        HttpStatus.Series.SUCCESSFUL.toString()
                        , "Product deleted successfully."),
                HttpStatus.CREATED);
    }

}
