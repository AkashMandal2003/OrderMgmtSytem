package com.jocata.oms.datamodel.orders.form;

import com.jocata.oms.datamodel.product.form.ProductForm;

public class OrderItemForm {

    private ProductForm product;
    private Integer quantity;

    public ProductForm getProduct() {
        return product;
    }

    public void setProduct(ProductForm product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}
