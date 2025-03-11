package com.jocata.oms.data.inventory.impl;

import com.jocata.oms.data.config.HibernateConfig;
import com.jocata.oms.data.inventory.InventoryDao;
import com.jocata.oms.datamodel.inventory.entity.Inventory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InventoryDaoImpl implements InventoryDao {

    private final HibernateConfig hibernateConfig;

    public InventoryDaoImpl(HibernateConfig hibernateConfig) {
        this.hibernateConfig = hibernateConfig;
    }

    @Override
    public Inventory saveOrUpdateInventory(Inventory inventory) {
        return hibernateConfig.saveOrUpdateEntity(inventory);
    }

    @Override
    public Inventory findByProductId(Integer productId) {
        return hibernateConfig.findEntityByCriteria(Inventory.class, "productId", productId);
    }

    @Override
    public void deleteInventory(Integer productId) {
        Inventory byProductId = findByProductId(productId);
        if (byProductId != null) {
            hibernateConfig.deleteEntity(Inventory.class, byProductId.getInventoryId());
        }
    }

    @Override
    public List<Inventory> findAllInventory() {
        return hibernateConfig.loadEntitiesByCriteria(Inventory.class);
    }
}
