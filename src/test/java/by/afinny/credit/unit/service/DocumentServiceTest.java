package by.afinny.credit.unit.service;

import by.afinny.credit.document.CreditOrderDocument;
import by.afinny.credit.repository.DocumentRepository;
import by.afinny.credit.service.impl.DocumentServiceImpl;
import org.assertj.core.api.AssertionsForClassTypes;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles("test")
public class DocumentServiceTest {

    @InjectMocks
    private DocumentServiceImpl documentService;

    @Mock
    private DocumentRepository documentRepository;

    private CreditOrderDocument creditOrderDocument;

    private final UUID DOCUMENT_ID = UUID.randomUUID();

    private final UUID CLIENT_ID = UUID.randomUUID();
    private final UUID CREDIT_ORDER_ID = UUID.randomUUID();

    private MockMultipartFile file;

    private List<CreditOrderDocument> documents;

    @Captor
    private ArgumentCaptor<List<CreditOrderDocument>> documentsCaptor;

    @BeforeEach
    void setUp() throws IOException {

        creditOrderDocument = CreditOrderDocument.builder()
                .id(DOCUMENT_ID)
                .build();

        file = new MockMultipartFile("ашду",
                "document.jpg",
                "image/jpeg",
                "Hello, World!".getBytes());
        documents = List.of(createCreditOrderDocument(CLIENT_ID, CREDIT_ORDER_ID, file));

    }

    @Test
    @DisplayName("Verifying that the submitted documents for verification have been saved")
    void uploadingDocuments_shouldSaveDocuments() throws Exception {
        //ACT
        documentService.uploadDocument(CLIENT_ID, CREDIT_ORDER_ID, file);
        //VERIFY
        verify(documentRepository).saveAll(documentsCaptor.capture());
        documents.get(0).setId(documentsCaptor.getValue().get(0).getId());
        assertThat(documentsCaptor.getValue().toString()).isEqualTo(documents.toString());
    }

    @Test
    @DisplayName("Verifying that the submitted documents for verification have not been saved")
    void uploadingDocuments_ifDocumentsNotSaved_thenThrow() {
        //ARRANGE
        when(documentRepository.saveAll(any(List.class))).thenThrow(RuntimeException.class);
        //ACT
        ThrowingCallable uploadingDocumentsMethod = () -> documentService.uploadDocument(CLIENT_ID, CREDIT_ORDER_ID, file);
        //VERIFY
        assertThatThrownBy(uploadingDocumentsMethod).isNotNull();
    }

    @Test
    @DisplayName("If document successfully delete then return No Content")
    void deleteDocument_shouldReturnNoContent() {
        //ARRANGE
        when(documentRepository.findByClientIdAndId(CLIENT_ID, DOCUMENT_ID)).thenReturn(Optional.of(creditOrderDocument));

        //ACT
        documentService.deleteDocument(CLIENT_ID, DOCUMENT_ID);

        //VERIFY
        verify(documentRepository, times(1)).delete(creditOrderDocument);
    }

    @Test
    @DisplayName("If document not success delete then throw Runtime Exception")
    void deleteDocument_ifNotSuccess_thenThrow() {
        //ARRANGE
        when(documentRepository.findByClientIdAndId(CLIENT_ID, DOCUMENT_ID)).thenThrow(javax.persistence.EntityNotFoundException.class);

        //ACT
        ThrowingCallable deleteCreditOrderDocumentMethodInvocation = () ->
                documentService.deleteDocument(CLIENT_ID, DOCUMENT_ID);

        //VERIFY
        AssertionsForClassTypes.assertThatThrownBy(deleteCreditOrderDocumentMethodInvocation)
                .isInstanceOf(javax.persistence.EntityNotFoundException.class);
        verify(documentRepository, never()).delete(creditOrderDocument);
    }

    @Test
    @DisplayName("If document find by Client Id then return document")
    void findDocument_thenReturnDocument() {
        //ARRANGE
        when(documentRepository.findAllByClientId(CLIENT_ID)).thenReturn(List.of(creditOrderDocument));

        //ACT
        List<CreditOrderDocument> found = documentRepository.findAllByClientId(CLIENT_ID);

        //VERIFY
        verifyFieldsCreditOrderDocument(found);
    }

    @Test
    @DisplayName("If client don't have document then return throw")
    void findDocument_returnThrow() {
        //ARRANGE
        when(documentRepository.findAllByClientId(CLIENT_ID)).thenThrow(EntityNotFoundException.class);

        //ACT
        ThrowingCallable found = () -> documentRepository.findAllByClientId(CLIENT_ID);

        //VERIFY
        assertThatThrownBy(found).isNotNull();
    }


    private CreditOrderDocument createCreditOrderDocument(UUID clientId, UUID creditOrderId, MockMultipartFile uploadDocument)
            throws IOException {
        return CreditOrderDocument.builder()
                .clientId(clientId)
                .creditOrderId(creditOrderId)
                .creationDate(LocalDate.now())
                .fileFormat(uploadDocument.getContentType())
                .documentName(uploadDocument.getName())
                .file(uploadDocument.getBytes())
                .build();
    }

    private void verifyFieldsCreditOrderDocument(List<CreditOrderDocument> foundDocument) {
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
