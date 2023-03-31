package by.afinny.credit.unit.repository;

import by.afinny.credit.entity.Product;
import by.afinny.credit.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(
        executionPhase = ExecutionPhase.BEFORE_TEST_METHOD,
        scripts = {"/schema-h2.sql", "/data-h2.sql"}
)
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private List<Product> activeProducts;

    @BeforeEach
    void lookup() {
        activeProducts = productRepository.findByIsActiveTrue();
    }

    @Test
    @DisplayName("Check that found only ACTIVE products")
    void whenGetActiveProducts_returnListActiveProducts() {
        assertThat(activeProducts).hasSize(1);
        assertThat(activeProducts.get(0).getIsActive()).isTrue();
    }
}

