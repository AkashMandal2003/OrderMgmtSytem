package com.jocata.oms.inventory.service.impl;

import com.jocata.oms.common.response.GenericResponsePayload;
import com.jocata.oms.data.inventory.InventoryDao;
import com.jocata.oms.datamodel.inventory.entity.Inventory;
import com.jocata.oms.datamodel.inventory.form.InventoryForm;
import com.jocata.oms.datamodel.product.form.ProductForm;
import com.jocata.oms.inventory.service.InventoryService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryDao inventoryDao;
    private final RestTemplate restTemplate;

    public InventoryServiceImpl(InventoryDao inventoryDao, RestTemplate restTemplate) {
        this.inventoryDao = inventoryDao;
        this.restTemplate = restTemplate;
    }

    private static final String PRODUCT_URI="http://localhost:8085/products/product/";

    @Override
    public InventoryForm addProductToInventory(Integer productId, Integer stockQuantity) {
        String productUrl = PRODUCT_URI + productId;

        ResponseEntity<GenericResponsePayload<ProductForm>> response = restTemplate.exchange(
                productUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<GenericResponsePayload<ProductForm>>() {
                }
        );

        ProductForm productForm = response.getBody().getData();
        if (productForm != null) {
            Inventory inventory = new Inventory();
            inventory.setProductId(Integer.valueOf(productForm.getProductId()));
            inventory.setStockQuantity(stockQuantity);
            Inventory savedInventory = inventoryDao.saveOrUpdateInventory(inventory);

            InventoryForm inventoryForm = new InventoryForm();
            inventoryForm.setProduct(productForm);
            inventoryForm.setStockQuantity(savedInventory.getStockQuantity());

            return inventoryForm;
        }
        return null;
    }


    @Override
    public boolean reserveStock(Integer productId, Integer quantity) {
        Inventory inventory = inventoryDao.findByProductId(productId);
        if (inventory != null && inventory.getStockQuantity() >= quantity) {
            inventory.setStockQuantity(inventory.getStockQuantity() - quantity);
            inventory.setReservedStock(inventory.getReservedStock() + quantity);
            inventoryDao.saveOrUpdateInventory(inventory);
            return true;
        }
        return false;
    }

    @Override
    public void releaseStock(Integer productId, Integer quantity) {
        Inventory inventory = inventoryDao.findByProductId(productId);
        if (inventory != null && inventory.getReservedStock() >= quantity) {
            inventory.setReservedStock(inventory.getReservedStock() - quantity);
            inventory.setStockQuantity(inventory.getStockQuantity() + quantity);
            inventoryDao.saveOrUpdateInventory(inventory);
        }
    }

    @Override
    public InventoryForm updateStock(Integer productId, Integer newStock) {
        String productUrl = PRODUCT_URI + productId;

        ResponseEntity<GenericResponsePayload<ProductForm>> response = restTemplate.exchange(
                productUrl,
                HttpMethod.GET.GET,
                null,
                new ParameterizedTypeReference<GenericResponsePayload<ProductForm>>() {
                }
        );

        ProductForm productForm = response.getBody().getData();

        Inventory inventory = inventoryDao.findByProductId(productId);
        if (inventory == null) {
            inventory = new Inventory();
            inventory.setProductId(Integer.valueOf(productForm.getProductId()));
            inventory.setStockQuantity(newStock);
        } else {
            inventory.setStockQuantity(newStock);
        }

        Inventory saved = inventoryDao.saveOrUpdateInventory(inventory);

        InventoryForm inventoryForm = new InventoryForm();
        inventoryForm.setProduct(productForm);
        inventoryForm.setStockQuantity(saved.getStockQuantity());
        return inventoryForm;
    }

    @Override
    public InventoryForm getInventoryByProductId(Integer productId) {
        Inventory inventory = inventoryDao.findByProductId(productId);

        if (inventory == null) {
            throw new RuntimeException("Inventory not found for Product ID: " + productId);
        }

        InventoryForm inventoryForm = new InventoryForm();
        inventoryForm.setStockQuantity(inventory.getStockQuantity());

        ResponseEntity<GenericResponsePayload<ProductForm>> response = restTemplate.exchange(
                PRODUCT_URI + productId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<GenericResponsePayload<ProductForm>>() {}
        );

        ProductForm productForm = response.getBody().getData();
        inventoryForm.setProduct(productForm);

        return inventoryForm;
    }


    @Override
    public List<InventoryForm> getAllInventory() {
        List<Inventory> inventories = inventoryDao.findAllInventory();

        return inventories.stream().map(inventory -> {
            InventoryForm inventoryForm = new InventoryForm();
            inventoryForm.setStockQuantity(inventory.getStockQuantity());

            ResponseEntity<GenericResponsePayload<ProductForm>> response = restTemplate.exchange(
                    PRODUCT_URI + inventory.getProductId(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<GenericResponsePayload<ProductForm>>() {}
            );

            ProductForm productForm = response.getBody().getData();
            inventoryForm.setProduct(productForm);

            return inventoryForm;
        }).toList();
    }


    @Override
    public void deleteProductFromInventory(Integer productId) {
        inventoryDao.deleteInventory(productId);
    }


}
