package by.afinny.credit.unit.repository;

import by.afinny.credit.entity.Credit;
import by.afinny.credit.entity.constant.CreditStatus;
import by.afinny.credit.repository.CreditRepository;
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
        scripts = {"/schema-h2.sql", "/data-h2.sql"}
)
@ActiveProfiles("test")
class CreditRepositoryTest {

    @Autowired
    private CreditRepository creditRepository;

    private List<Credit> clientActiveCredits;
    private final UUID clientId = UUID.fromString("00000000-0000-0001-0000-000000000001");

    @BeforeEach
    void lookup() {
        clientActiveCredits = creditRepository.findByCreditOrderClientIdAndStatus(clientId, CreditStatus.ACTIVE);
    }

    @Test
    @DisplayName("Check that found client's ACTIVE credits")
    void whenGetActiveProducts_returnListActiveProducts(){
        //VERIFY
        assertThat(clientActiveCredits).hasSize(2);
        clientActiveCredits
                .forEach(credit -> assertThat(credit.getStatus()).isEqualTo(CreditStatus.ACTIVE));
    }
}