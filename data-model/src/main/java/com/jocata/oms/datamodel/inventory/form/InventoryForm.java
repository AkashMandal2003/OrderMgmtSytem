package com.jocata.oms.datamodel.inventory.form;

import com.jocata.oms.datamodel.product.form.ProductForm;

public class InventoryForm {

    private ProductForm product;
    private Integer stockQuantity;

    public ProductForm getProduct() {
        return product;
    }

    public void setProduct(ProductForm product) {
        this.product = product;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}
