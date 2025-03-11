package com.jocata.oms.inventory.service;

import com.jocata.oms.datamodel.inventory.form.InventoryForm;

import java.util.List;

public interface InventoryService {

    InventoryForm addProductToInventory(Integer productId, Integer stockQuantity);

    boolean reserveStock(Integer productId, Integer quantity);

    void releaseStock(Integer productId, Integer quantity);

    InventoryForm updateStock(Integer productId, Integer newStock);

    InventoryForm getInventoryByProductId(Integer productId);

    List<InventoryForm> getAllInventory();

    void deleteProductFromInventory(Integer productId);
}
