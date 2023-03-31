package by.afinny.credit.unit.repository;

import by.afinny.credit.document.CreditOrderDocument;
import by.afinny.credit.exception.EntityNotFoundException;
import by.afinny.credit.integration.config.annotation.TestWithMongoContainer;
import by.afinny.credit.repository.DocumentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@TestWithMongoContainer
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@Sql(
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        scripts = {"/schema-h2.sql"}
)
class DocumentRepositoryTest {

    @Autowired
    DocumentRepository documentRepository;

    private final UUID ID = UUID.fromString("a861c25d-7968-4f0b-b922-971d90c756a9");
    private final byte[] bytes = {1, 2, 3};


    private CreditOrderDocument creditOrderDocument;

    @BeforeEach
    void setUp() {

        creditOrderDocument = CreditOrderDocument.builder()
                .id(ID)
                .clientId(ID)
                .documentName("name")
                .creationDate(LocalDate.now())
                .creditOrderId(ID)
                .file(bytes)
                .build();
    }

    @AfterEach
    void clean() {
        documentRepository.deleteAll();
    }

    @Test
    @DisplayName("If save creditOrderDocument then find creditOrderDocument and return")
    void findDocumentByClientId_thenReturnCreditOrderDocument() {
        //ARRANGE
        documentRepository.save(creditOrderDocument);

        //ACT
        List<CreditOrderDocument> found = documentRepository.findAllByClientId(ID);
        if (found == null) {
            throw new EntityNotFoundException("don't found");
        }
        //VERIFY
        verifyDocuments(found);
    }

    private void verifyDocuments(List<CreditOrderDocument> foundDocument) {
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(foundDocument.get(0).getDocumentName())
                    .withFailMessage("Document Name")
                    .isEqualTo(creditOrderDocument.getDocumentName());
            softAssertions.assertThat(foundDocument.get(0).getClientId())
                    .withFailMessage("Client Id")
                    .isEqualTo(creditOrderDocument.getClientId());
            softAssertions.assertThat(foundDocument.get(0).getCreditOrderId())
                    .withFailMessage("Credit Order Id")
                    .isEqualTo(creditOrderDocument.getCreditOrderId());
            softAssertions.assertThat(foundDocument.get(0).getFile())
                    .withFailMessage("File")
                    .isEqualTo(creditOrderDocument.getFile());
            softAssertions.assertThat(foundDocument.get(0).getFileFormat())
                    .withFailMessage("File Format")
                    .isEqualTo(creditOrderDocument.getFileFormat());
        });
    }
}