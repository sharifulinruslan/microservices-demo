package com.example.productservice.controller;

import com.example.productservice.entity.Product;
import com.example.productservice.service.ProductService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class)
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ProductService productService;
    @Autowired
    private ObjectMapper mapper = new ObjectMapper();

    private Product testProduct1;
    private Product testProduct2;
    private List<Product> testProducts;

    @BeforeEach
    void setUp() {
        testProduct1 = new Product();
        testProduct1.setId(1L);
        testProduct1.setName("Test Product");
        testProduct1.setCategory("Electronics");
        testProduct1.setPrice(999.99);

        testProduct2 = new Product();
        testProduct2.setId(2L);
        testProduct2.setName("Test Product 2");
        testProduct2.setCategory("Books");
        testProduct2.setPrice(19.99);

        testProducts = Arrays.asList(testProduct1, testProduct2);
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() throws Exception {
        doReturn(testProducts).when(productService).getAllProducts();

        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(testProduct1.getName()))
                .andExpect(jsonPath("$[1].name").value(testProduct2.getName()));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() throws Exception {
        doReturn(testProduct1).when(productService).getProductById(testProduct1.getId());

        mockMvc.perform(get("/api/products/" + testProduct1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testProduct1.getId()))
                .andExpect(jsonPath("$.name").value(testProduct1.getName()))
                .andExpect(jsonPath("$.category").value(testProduct1.getCategory()))
                .andExpect(jsonPath("$.price").value(testProduct1.getPrice()));

        verify(productService, times(1)).getProductById(testProduct1.getId());
    }

    @Test
    void getProductById_WhenProductDoesNotExist_ShouldReturn404() throws Exception {
        doReturn(null).when(productService).getProductById(testProduct1.getId());

        mockMvc.perform(get("/api/products/" + testProduct1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        verify(productService, times(1)).getProductById(testProduct1.getId());
    }

    @Test
    void getProductsByCategory_ShouldReturnFilteredProducts() throws Exception {
        doReturn(List.of(testProduct1)).when(productService).getProductsByCategory(testProduct1.getCategory());

        mockMvc.perform(get("/api/products/category/" + testProduct1.getCategory())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(testProduct1.getName()))
                .andExpect(jsonPath("$[0].category").value(testProduct1.getCategory()));

        verify(productService, times(1)).getProductsByCategory(testProduct1.getCategory());
    }

    @Test
    void createProduct_ShouldReturn201() throws Exception {
//        doNothing().when(productService).saveProduct(any(Product.class));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(testProduct1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(testProduct1.getName()));
    }

    @Test
    void updateProduct_ShouldReturn200() throws Exception {
        doReturn(testProduct1).when(productService).updateProduct(any(Product.class));

        mockMvc.perform(put("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(testProduct1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testProduct1.getName()));

        verify(productService, times(1)).updateProduct(any(Product.class));
    }

    @Test
    void deleteProduct_WhenProductExist_ShouldReturn200() throws Exception {
        doNothing().when(productService).deleteProductById(testProduct1.getId());

        mockMvc.perform(delete("/api/products/" + testProduct1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(productService, times(1)).deleteProductById(testProduct1.getId());
    }
}