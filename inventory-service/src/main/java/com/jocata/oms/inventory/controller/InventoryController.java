package com.jocata.oms.inventory.controller;

import com.jocata.oms.datamodel.inventory.entity.Inventory;
import com.jocata.oms.datamodel.inventory.form.InventoryForm;
import com.jocata.oms.inventory.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/add")
    public ResponseEntity<InventoryForm> addProductToInventory(@RequestParam Integer productId, @RequestParam Integer stockQuantity) {
        InventoryForm inventoryForm = inventoryService.addProductToInventory(productId, stockQuantity);
        return ResponseEntity.ok(inventoryForm);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryForm> getInventoryByProductId(@PathVariable Integer productId) {
        InventoryForm inventory = inventoryService.getInventoryByProductId(productId);
        return inventory != null ? ResponseEntity.ok(inventory) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<InventoryForm>> getAllInventory() {
        List<InventoryForm> inventoryList = inventoryService.getAllInventory();
        return ResponseEntity.ok(inventoryList);
    }

    @PutMapping("/reserve")
    public ResponseEntity<String> reserveStock(@RequestParam Integer productId, @RequestParam Integer quantity) {
        boolean reserved = inventoryService.reserveStock(productId, quantity);
        return reserved ? ResponseEntity.ok("Stock reserved") : ResponseEntity.badRequest().body("Insufficient stock");
    }

    @PutMapping("/release")
    public ResponseEntity<String> releaseStock(@RequestParam Integer productId, @RequestParam Integer quantity) {
        inventoryService.releaseStock(productId, quantity);
        return ResponseEntity.ok("Stock released");
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateStock(@RequestParam Integer productId, @RequestParam Integer newStock) {
        inventoryService.updateStock(productId, newStock);
        return ResponseEntity.ok("Stock updated");
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<String> deleteProductFromInventory(@PathVariable Integer productId) {
        inventoryService.deleteProductFromInventory(productId);
        return ResponseEntity.ok("Product removed from inventory");
    }
}
