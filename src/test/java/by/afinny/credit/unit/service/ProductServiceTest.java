package by.afinny.credit.unit.service;

import by.afinny.credit.dto.ProductDto;
import by.afinny.credit.entity.Product;
import by.afinny.credit.mapper.ProductMapper;
import by.afinny.credit.repository.ProductRepository;
import by.afinny.credit.service.impl.ProductServiceImpl;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;

    private static List<Product> products;
    private static List<ProductDto> productsDto;

    @BeforeAll
    public static void setUp() {
        Product testProductA = new Product();
        Product testProductB = new Product();
        testProductA.setIsActive(Boolean.TRUE);
        testProductB.setIsActive(Boolean.FALSE);

        productsDto = new ArrayList<>();
        products = new ArrayList<>();
        products.add(testProductA);
        products.add(testProductB);
    }

    @Test
    @DisplayName("If successfully save then verify that returned value isn't null")
    void getActiveProducts_ifSuccess_thenReturnListProducts() {
        //ARRANGE
        when(productRepository.findAll()).thenReturn(products);
        when(productMapper.productsToProductsDto(products)).thenReturn(productsDto);

        //ACT
        List<ProductDto> productDto = productService.getProducts();

        //VERIFY
        verify(productRepository).findAll();
        assertThat(productDto).isEqualTo(productsDto);
    }

    @Test
    @DisplayName("If save failed then throw Runtime Exception")
    void get_activeProducts_ifNotSuccess_thenThrow() {
        //ARRANGE
        when(productRepository.findAll())
                .thenThrow(RuntimeException.class);

        //ACT
        ThrowingCallable getProductsMethodInvocation = () -> productService.getProducts();

        //VERIFY
        assertThatThrownBy(getProductsMethodInvocation).isInstanceOf(RuntimeException.class);
    }
}

