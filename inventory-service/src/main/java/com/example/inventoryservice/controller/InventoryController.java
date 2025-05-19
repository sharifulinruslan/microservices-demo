package com.example.inventoryservice.controller;

import com.example.inventoryservice.entity.Inventory;
import com.example.inventoryservice.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Inventory> getInventoryByProductId(@PathVariable Long productId) {
        Inventory inventory = inventoryService.getInventoryByProductId(productId);
        return new ResponseEntity<>(inventory, HttpStatus.OK);
    }

    @GetMapping("/product/{productId}/in-stock")
    public ResponseEntity<Boolean> getInventoryByProductIdInStock(@PathVariable Long productId) {
        boolean inStock = inventoryService.isInStock(productId);
        return new ResponseEntity<>(inStock, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Inventory> createInventory(@RequestBody Inventory inventory) {
        inventoryService.createInventory(inventory);
        return new ResponseEntity<>(inventory, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Inventory> updateInventory(@RequestBody Inventory inventory) {
        inventoryService.updateInventory(inventory);
        return new ResponseEntity<>(inventory, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Inventory> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventoryById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
