package by.afinny.credit.unit.repository;

import by.afinny.credit.entity.CreditOrder;
import by.afinny.credit.repository.CreditOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(
        executionPhase = ExecutionPhase.BEFORE_TEST_METHOD,
        scripts = {"/schema-h2.sql","/data-h2.sql"}
)
@ActiveProfiles("test")
class CreditOrderRepositoryTest {

    @Autowired
    CreditOrderRepository creditOrderRepository;

    private List<CreditOrder> creditOrders;
    private UUID clientId;

    @BeforeEach
    void lookUp(){
        clientId = UUID.fromString("00000000-0000-0001-0000-000000000001");
        creditOrders = creditOrderRepository.findAllByClientId(clientId);
    }

    @Test
    @DisplayName("Check that found credit order")
    void whenGetActiveProducts_returnListActiveProducts(){
        assertThat(creditOrders).hasSize(3);
        assertThat(creditOrders.get(0).getClientId()).isEqualTo(clientId);
    }
}