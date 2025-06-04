package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.ProductDTO;
import com.example.inventoryservice.entity.Inventory;
import com.example.inventoryservice.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final WebClient.Builder webClientBuilder;
    @Value("${product-service.url}")
    private String URIProductService;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository, WebClient.Builder webClientBuilder) {
        this.inventoryRepository = inventoryRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public void createInventory(Inventory inventory) {
        ProductDTO productDTO = webClientBuilder.build().get()
                .uri(URIProductService + "/{id}", inventory.getProductId())
                .retrieve()
                .bodyToMono(ProductDTO.class)
                .block();
        if (productDTO != null) {
            inventory.setInStock(inventory.getQuantity() > 0);
            inventoryRepository.save(inventory);
        } else {
            System.err.println("Could not find product with id " + inventory.getProductId());
        }
    }

    public List<Inventory> getAllInventories() {
        return inventoryRepository.findAll();
    }

    @Cacheable(value = "inventory", key = "#id")
    public Inventory getInventoryById(Long id) {
        return inventoryRepository.findById(id).orElse(null);
    }

    public Inventory getInventoryByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId).orElse(null);
    }

    @CachePut(value = "inventory", key = "#inventory.id")
    public Inventory updateInventory(Inventory inventory) {
        inventory.setInStock(inventory.getQuantity() > 0);
        return inventoryRepository.save(inventory);
    }

    @CacheEvict(value = "inventory", key = "#id")
    public void deleteInventoryById(Long id) {
        inventoryRepository.deleteById(id);
    }

    public boolean isInStock(Long productId) {
        return inventoryRepository.findByProductId(productId).map(Inventory::getInStock).orElse(false);
    }
}
