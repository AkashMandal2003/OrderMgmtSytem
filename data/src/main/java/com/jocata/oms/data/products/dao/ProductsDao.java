package com.jocata.oms.data.products.dao;

import com.jocata.oms.datamodel.product.entity.ProductDetails;

import java.util.List;

public interface ProductsDao {

    ProductDetails createProduct(ProductDetails productDetails);
    ProductDetails getProductDetails(Integer productId);
    List<ProductDetails> getAllProducts();
    ProductDetails updateProduct(ProductDetails productDetails);
    String deleteProduct(ProductDetails productDetails);

}
