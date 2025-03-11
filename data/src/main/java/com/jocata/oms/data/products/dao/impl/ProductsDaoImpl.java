package com.jocata.oms.data.products.dao.impl;

import com.jocata.oms.data.config.HibernateConfig;
import com.jocata.oms.data.products.dao.ProductsDao;
import com.jocata.oms.datamodel.product.entity.ProductDetails;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductsDaoImpl implements ProductsDao {

    private final HibernateConfig hibernateConfig;

    public ProductsDaoImpl(HibernateConfig hibernateConfig) {
        this.hibernateConfig = hibernateConfig;
    }

    @Override
    public ProductDetails createProduct(ProductDetails productDetails) {
        return hibernateConfig.saveEntity(productDetails);
    }

    @Override
    public ProductDetails getProductDetails(Integer productId) {
        return hibernateConfig.findEntityById(ProductDetails.class ,productId);
    }

    @Override
    public List<ProductDetails> getAllProducts() {
        return hibernateConfig.loadEntitiesByCriteria(ProductDetails.class);
    }

    @Override
    public ProductDetails updateProduct(ProductDetails productDetails) {
        return hibernateConfig.updateEntity(productDetails);
    }

    @Override
    public String deleteProduct(ProductDetails productDetails) {
        hibernateConfig.deleteEntity(productDetails, productDetails.getProductId());
        return "";
    }
}
