package com.example.productservice.service;

import com.example.productservice.entity.Product;
import com.example.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("testProduct");
        testProduct.setPrice(1.0);
        testProduct.setCategory("testCategory");
        testProduct.setDescription("testProductDescription");
        testProduct.setStockQuantity(1);
    }

    @Test
    void saveProduct_ShouldCallRepositorySave() {
        productService.saveProduct(testProduct);

        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    void updateProduct_ShouldUpdateProduct() {
        doReturn(testProduct).when(productRepository).save(testProduct);

        Product updatedProduct = productService.updateProduct(testProduct);

        assertThat(updatedProduct).isNotNull();
        assertThat(updatedProduct).isEqualTo(testProduct);
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    void getProductById_WhenProductsExists_ShouldReturnProduct() {
        doReturn(Optional.of(testProduct)).when(productRepository).findById(1L);

        Product result = productService.getProductById(1L);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(testProduct);
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_WhenProductDoesNotExist_ShouldReturnNull() {
        doReturn(Optional.empty()).when(productRepository).findById(1L);

        Product result = productService.getProductById(1L);

        assertThat(result).isNull();
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() {
        List<Product> products = List.of(testProduct);
        doReturn(products).when(productRepository).findAll();

        List<Product> result = productService.getAllProducts();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result).isEqualTo(products);
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProductsByCategory_ShouldReturnProductsByCategory() {
        Product testProduct1 = new Product();
        testProduct1.setId(2L);
        testProduct1.setName("testProduct2");
        testProduct1.setPrice(2.0);
        testProduct1.setCategory("testCategory");
        testProduct1.setDescription("testProductDescription2");
        testProduct1.setStockQuantity(2);
        List<Product> products = List.of(testProduct, testProduct1);
        doReturn(products).when(productRepository).findByCategory("testCategory");

        List<Product> result = productService.getProductsByCategory("testCategory");

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).isEqualTo(products);
        verify(productRepository, times(1)).findByCategory("testCategory");
    }

    @Test
    void getProductsByCategory_ShouldReturnNothing() {
        doReturn(null).when(productRepository).findByCategory("testCategory");

        List<Product> result = productService.getProductsByCategory("testCategory");

        assertThat(result).isNull();
        verify(productRepository, times(1)).findByCategory("testCategory");
    }

    @Test
    void deleteProductById_ShouldCallRepositoryDelete() {
        productService.deleteProductById(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }
}