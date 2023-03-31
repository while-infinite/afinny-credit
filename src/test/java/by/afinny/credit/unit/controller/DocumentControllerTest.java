package by.afinny.credit.unit.controller;

import by.afinny.credit.controller.DocumentController;
import by.afinny.credit.document.CreditOrderDocument;
import by.afinny.credit.dto.RequestCreditOrderDocumentDto;
import by.afinny.credit.exception.EntityNotFoundException;
import by.afinny.credit.exception.handler.ExceptionHandlerController;
import by.afinny.credit.service.DocumentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@WebMvcTest(DocumentController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class DocumentControllerTest {

    @MockBean
    private DocumentService documentsService;

    @Autowired
    private MockMvc mockMvc;

    private final UUID CLIENT_ID = UUID.fromString("9b81ee52-2c0d-4bda-90b4-0b12e9d6f467");
    private final UUID CREDIT_ORDER_ID = UUID.fromString("9b81ab52-4bda-4bda-90b4-0b12e9d6f467");
    private MockMultipartFile file;
    private RequestCreditOrderDocumentDto creditOrderDocumentDto;
    private CreditOrderDocument creditOrderDocument;


    private final UUID DOCUMENT_ID = UUID.randomUUID();

    @BeforeEach
    public void setUp() {
        mockMvc = standaloneSetup(new DocumentController(documentsService))
                .setControllerAdvice(ExceptionHandlerController.class).build();
        file = new MockMultipartFile("file",
                "document.jpg",
                "image/jpeg",
                "Hello, World!".getBytes());

        creditOrderDocumentDto = RequestCreditOrderDocumentDto.builder()
                .file(file)
                .build();

        creditOrderDocument = CreditOrderDocument.builder()
                .id(UUID.randomUUID())
                .clientId(UUID.randomUUID())
                .documentName("name")
                .creationDate(LocalDate.now())
                .creditOrderId(UUID.randomUUID())
                .file(new byte[]{1, 2, 3})
                .build();


    }

    @Test
    @DisplayName("If successfully saved documents then don't return content")
    void uploadingDocuments_shouldNotReturnContent() throws Exception {
        //ACT&VERIFY
        mockMvc.perform(post("/auth/credit-order-documents/new")
                        .param("clientId", CLIENT_ID.toString())
                        .param("creditOrderId", CREDIT_ORDER_ID.toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .sessionAttr("file", creditOrderDocumentDto))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("If documents saving has been failed then return INTERNAL SERVER ERROR")
    void uploadingDocuments_ifDocumentsNotSaved_thenReturnInternalServerError() throws Exception {
        //ARRANGE
        doThrow(RuntimeException.class).when(documentsService).uploadDocument(any(UUID.class), any(UUID.class), any());
        //ACT&VERIFY
        mockMvc.perform(post("/auth/credit-order-documents/new")
                        .param("clientId", CLIENT_ID.toString())
                        .param("creditOrderId", CREDIT_ORDER_ID.toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .sessionAttr("files", creditOrderDocumentDto))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("If document was successfully delete then return status No_Content")
    void deleteDocument_ifSuccessfullyDeleted_then204_NO_CONTENT() throws Exception {
        //ARRANGE
        ArgumentCaptor<UUID> operationIdCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<UUID> clientIdCaptor = ArgumentCaptor.forClass(UUID.class);

        //ACT
        ResultActions perform = mockMvc.perform(
                delete(DocumentController.URL_CREDIT_DOCUMENT
                        + DocumentController.URL_DOCUMENT_ID, DOCUMENT_ID)
                        .param("clientId", CLIENT_ID.toString()));

        //VERIFY
        perform.andExpect(status().isNoContent());
        verify(documentsService, times(1)).deleteDocument(clientIdCaptor.capture(), operationIdCaptor.capture());
        assertThat(DOCUMENT_ID).isEqualTo(operationIdCaptor.getValue());
        assertThat(CLIENT_ID).isEqualTo(clientIdCaptor.getValue());
    }

    @Test
    @DisplayName("If document wasn't successfully delete then return status BAD_REQUEST")
    void deleteDocument_ifNotDeleted_then400_BAD_REQUEST() throws Exception {
        //ARRANGE
        doThrow(EntityNotFoundException.class).when(documentsService).deleteDocument(any(UUID.class), any(UUID.class));

        //ACT
        ResultActions perform = mockMvc.perform(
                delete(DocumentController.URL_CREDIT_DOCUMENT
                        + DocumentController.URL_DOCUMENT_ID, DOCUMENT_ID)
                        .param("clientId", CLIENT_ID.toString()));

        //VERIFY
        perform.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Get document dto")
    void findDocument_thenReturnDocument() throws Exception {
        //ARRANGE
        when(documentsService.getDocuments(CLIENT_ID)).thenReturn(List.of(creditOrderDocument));

        //ACT
        MvcResult result = mockMvc.perform(
                        get("/auth/credit-order-documents/" + CLIENT_ID))
                .andExpect(status().isOk())
                .andReturn();

        //VERIFY
        verifyBody(asJsonString(List.of(creditOrderDocument)), result.getResponse().getContentAsString());
    }

    private String asJsonString(Object obj) throws JsonProcessingException {

        return new ObjectMapper().findAndRegisterModules().enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writeValueAsString(obj);
    }

    private void verifyBody(String expectedBody, String actualBody) {

        AssertionsForClassTypes.assertThat(actualBody).isEqualTo(expectedBody);
    }
}
