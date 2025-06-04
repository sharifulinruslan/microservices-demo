package com.example.inventoryservice.controller;

import com.example.inventoryservice.dto.ProductDTO;
import com.example.inventoryservice.entity.Inventory;
import com.example.inventoryservice.service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = InventoryController.class)
class InventoryControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private InventoryService inventoryService;
    @Autowired
    private ObjectMapper objectMapper;

    private Inventory inventory1;
    private Inventory inventory2;
    private List<Inventory> listOfInventory;
    private ProductDTO productDTO;

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

        listOfInventory = Arrays.asList(inventory1, inventory2);

        productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Test Product");
        productDTO.setPrice(100.0);
    }

    @Test
    void getAllInventory_ShouldReturnAllInventory() throws Exception {
        when(inventoryService.getAllInventories()).thenReturn(listOfInventory);

        mockMvc.perform(get("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].locationCode").value(inventory1.getLocationCode()))
                .andExpect(jsonPath("$[1].locationCode").value(inventory2.getLocationCode()));

        verify(inventoryService, times(1)).getAllInventories();
    }

    @Test
    void getInventoryById_WhenInventoryExists_ShouldReturnInventoryById() throws Exception {
        when(inventoryService.getInventoryById(inventory1.getId())).thenReturn(inventory1);

        mockMvc.perform(get("/api/inventory/" + inventory1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locationCode").value(inventory1.getLocationCode()))
                .andExpect(jsonPath("$.productId").value(inventory1.getProductId()))
                .andExpect(jsonPath("$.quantity").value(inventory1.getQuantity()))
                .andExpect(jsonPath("$.inStock").value(inventory1.getInStock()))
                .andExpect(jsonPath("$.id").value(inventory1.getId()));

        verify(inventoryService, times(1)).getInventoryById(inventory1.getId());
    }

    @Test
    void getInventoryById_WhenInventoryDoesNotExist_ShouldReturnEmpty() throws Exception {
        when(inventoryService.getInventoryById(anyLong())).thenReturn(null);

        mockMvc.perform(get("/api/inventory/" + inventory1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        verify(inventoryService, times(1)).getInventoryById(anyLong());
    }

    @Test
    void getInventoryByProductId_WhenInventoryExists_ShouldReturnInventoryByProductId() throws Exception {
        when(inventoryService.getInventoryByProductId(productDTO.getId())).thenReturn(inventory1);

        mockMvc.perform(get("/api/inventory/product/" + productDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(productDTO.getId()))
                .andExpect(jsonPath("$.locationCode").value(inventory1.getLocationCode()));

        verify(inventoryService, times(1)).getInventoryByProductId(productDTO.getId());
    }

    @Test
    void getInventoryByProductIdInStock_ShouldReturnTrueIfProductIdIsInStock() throws Exception {
        when(inventoryService.isInStock(productDTO.getId())).thenReturn(true);

        mockMvc.perform(get("/api/inventory/product/" + productDTO.getId() + "/in-stock"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(inventoryService, times(1)).isInStock(productDTO.getId());
    }

    @Test
    void getInventoryByProductIdInStock_ShouldReturnFalseIfProductIdNotIsInStock() throws Exception {
        when(inventoryService.isInStock(productDTO.getId())).thenReturn(false);

        mockMvc.perform(get("/api/inventory/product/" + productDTO.getId() + "/in-stock"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(inventoryService, times(1)).isInStock(productDTO.getId());
    }

    @Test
    void createInventory_ShouldReturn201Status() throws Exception {
        mockMvc.perform(post("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventory1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.locationCode").value(inventory1.getLocationCode()));

        verify(inventoryService, times(1)).createInventory(inventory1);
    }

    @Test
    void updateInventory_ShouldReturn200Status() throws Exception {
        when(inventoryService.updateInventory(inventory1)).thenReturn(inventory1);

        mockMvc.perform(put("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventory1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locationCode").value(inventory1.getLocationCode()))
                .andExpect(jsonPath("$.productId").value(inventory1.getProductId()));

        verify(inventoryService, times(1)).updateInventory(inventory1);
    }

    @Test
    void deleteInventory_ShouldDeleteInventory() throws Exception {
        mockMvc.perform(delete("/api/inventory/" + inventory1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(inventoryService, times(1)).deleteInventoryById(inventory1.getId());
    }
}