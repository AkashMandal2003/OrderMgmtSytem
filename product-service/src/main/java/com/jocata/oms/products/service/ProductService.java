package com.jocata.oms.products.service;

import com.jocata.oms.datamodel.product.form.ProductForm;

import java.util.List;

public interface ProductService {

    ProductForm createProduct(ProductForm productForm);
    List<ProductForm> getAllProducts();
    ProductForm updateProduct(Integer productId,ProductForm productForm);
    String deleteProduct(Integer productId);

}
