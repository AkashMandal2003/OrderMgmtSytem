package com.jocata.oms.data.inventory;

import com.jocata.oms.datamodel.inventory.entity.Inventory;

import java.util.List;

public interface InventoryDao {

    Inventory saveOrUpdateInventory(Inventory inventory);

    Inventory findByProductId(Integer productId);

    void deleteInventory(Integer productId);

    List<Inventory> findAllInventory();
}
