package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.ProductDTO;
import com.example.inventoryservice.entity.Inventory;
import com.example.inventoryservice.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private WebClient.Builder webClientBuilder;
    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;
    @InjectMocks
    private InventoryService inventoryService;

    private ProductDTO productDTO;
    private Inventory inventory1;
    private Inventory inventory2;
    private List<Inventory> inventoryList;

    @BeforeEach
    void setUp() {
        inventory1 = new Inventory();
        inventory1.setId(1L);
        inventory1.setProductId(1L);
        inventory1.setQuantity(1);
        inventory1.setInStock(true);
        inventory1.setLocationCode("locationCode1");

        inventory2 = new Inventory();
        inventory2.setId(2L);
        inventory2.setProductId(2L);
        inventory2.setQuantity(2);
        inventory2.setInStock(true);
        inventory2.setLocationCode("locationCode2");

        inventoryList = Arrays.asList(inventory1, inventory2);

        productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Test Product");
        productDTO.setPrice(100.0);
    }

    @Test
    void createInventory_WhenProductExists_ShouldCreateInventory() {
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyLong())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProductDTO.class)).thenReturn(Mono.just(productDTO));

        doReturn(inventory1).when(inventoryRepository).save(any(Inventory.class));

        inventoryService.createInventory(inventory1);

        assertThat(inventory1.getInStock()).isTrue();
        verify(inventoryRepository, times(1)).save(inventory1);
    }

    @Test
    void createInventory_WhenProductDoesNotExist_ShouldDoNothing() {
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyLong())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProductDTO.class)).thenReturn(Mono.empty());

        inventoryService.createInventory(inventory1);

        verify(inventoryRepository, never()).save(any());
    }

    @Test
    void getAllInventories_ShouldReturnAllInventories() {
        doReturn(inventoryList).when(inventoryRepository).findAll();

        List<Inventory> result = inventoryService.getAllInventories();

        assertThat(result).isNotEmpty();
        assertThat(result).isEqualTo(inventoryList);
        verify(inventoryRepository, times(1)).findAll();
    }

    @Test
    void getInventoryById_WhenInventoryExists_ShouldReturnInventory() {
        when(inventoryRepository.findById(inventory1.getId())).thenReturn(Optional.of(inventory1));

        Inventory result = inventoryService.getInventoryById(inventory1.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(inventory1.getId());
    }

    @Test
    void getInventoryById_WhenInventoryDoesNotExist_ShouldReturnNull() {
        when(inventoryRepository.findById(inventory2.getId())).thenReturn(Optional.empty());

        Inventory result = inventoryService.getInventoryById(inventory2.getId());

        assertThat(result).isNull();
        verify(inventoryRepository, times(1)).findById(inventory2.getId());
    }

    @Test
    void getInventoryByProductId_WhenProductExists_ShouldReturnInventory() {
        when(inventoryRepository.findByProductId(productDTO.getId())).thenReturn(Optional.of(inventory1));

        Inventory result = inventoryService.getInventoryByProductId(productDTO.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(inventory1.getId());
        assertThat(result.getProductId()).isEqualTo(productDTO.getId());
    }

    @Test
    void getInventoryByProductId_WhenProductDoesNotExist_ShouldReturnNull() {
        when(inventoryRepository.findByProductId(productDTO.getId())).thenReturn(Optional.empty());

        Inventory result = inventoryService.getInventoryByProductId(productDTO.getId());

        assertThat(result).isNull();
        verify(inventoryRepository, times(1)).findByProductId(productDTO.getId());
    }

    @Test
    void updateInventory_SaveAndReturnInventory() {
        Inventory inventory = new Inventory();
        inventory.setId(1L);
        inventory.setProductId(1L);
        inventory.setQuantity(1000);

        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        Inventory result = inventoryService.updateInventory(inventory);

        assertThat(result).isNotNull();
        assertThat(result.getInStock()).isTrue();
        assertThat(result).isEqualTo(inventory);
        verify(inventoryRepository, times(1)).save(inventory);
    }

    @Test
    void deleteInventoryById_ShouldDeleteInventory() {
        inventoryService.deleteInventoryById(inventory1.getId());

        verify(inventoryRepository, times(1)).deleteById(inventory1.getId());
    }

    @Test
    void isInStock_WhenProductInStock_ShouldReturnTrue() {
        doReturn(Optional.of(inventory1)).when(inventoryRepository).findByProductId(productDTO.getId());

        boolean result = inventoryService.isInStock(productDTO.getId());

        assertThat(result).isTrue();
        verify(inventoryRepository, times(1)).findByProductId(productDTO.getId());
    }

    @Test
    void isInStock_WhenProductDoesNotInStock_ShouldReturnFalse() {
        inventory1.setInStock(false);
        inventory1.setQuantity(0);
        doReturn(Optional.of(inventory1)).when(inventoryRepository).findByProductId(productDTO.getId());

        boolean result = inventoryService.isInStock(productDTO.getId());

        assertThat(result).isFalse();
        verify(inventoryRepository, times(1)).findByProductId(productDTO.getId());
    }
}