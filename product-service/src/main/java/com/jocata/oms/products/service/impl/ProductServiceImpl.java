package com.jocata.oms.products.service.impl;

import com.jocata.oms.data.products.dao.ProductsDao;
import com.jocata.oms.datamodel.product.entity.ProductDetails;
import com.jocata.oms.datamodel.product.form.ProductForm;
import com.jocata.oms.products.service.ProductService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductsDao productsDao;

    public ProductServiceImpl(ProductsDao productsDao) {
        this.productsDao = productsDao;
    }

    @Override
    public ProductForm createProduct(ProductForm productForm) {

        ProductDetails newProduct = getProductDetails(productForm);
        ProductDetails savedProduct = productsDao.createProduct(newProduct);
        return getProductForm(savedProduct);

    }

    @Override
    public List<ProductForm> getAllProducts() {
        List<ProductDetails> allProducts = productsDao.getAllProducts();
        List<ProductForm> productForms = new ArrayList<>();
        for (ProductDetails productDetails : allProducts) {
            ProductForm productForm = getProductForm(productDetails);
            productForms.add(productForm);
        }
        return productForms;
    }

    @Override
    public ProductForm updateProduct(Integer productId,ProductForm productForm) {
        ProductDetails productDetails = productsDao.getProductDetails(productId);
        productDetails.setName(productForm.getName());
        productDetails.setDescription(productForm.getDescription());
        productDetails.setPrice(new BigDecimal(productForm.getPrice()));
        productDetails.setUpdatedAt(Timestamp.from(Instant.now()));

        ProductDetails savedProduct = productsDao.updateProduct(productDetails);
        return getProductForm(savedProduct);
    }

    @Override
    public String deleteProduct(Integer productId) {
        ProductDetails productDetails = productsDao.getProductDetails(productId);
        if(productDetails!=null) {
            productsDao.deleteProduct(productDetails);
            return "Product deleted";
        }
        return "Product not found";
    }

    private static ProductDetails getProductDetails(ProductForm productForm) {
        ProductDetails newProduct = new ProductDetails();
        newProduct.setName(productForm.getName());
        newProduct.setDescription(productForm.getDescription());
        newProduct.setPrice(new BigDecimal(productForm.getPrice()));
        return newProduct;
    }

    private static ProductForm getProductForm(ProductDetails savedProduct) {
        ProductForm response = new ProductForm();
        response.setProductId(String.valueOf(savedProduct.getProductId()));
        response.setName(savedProduct.getName());
        response.setDescription(savedProduct.getDescription());
        response.setPrice(String.valueOf(savedProduct.getPrice()));
        return response;
    }
}
