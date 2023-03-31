package by.afinny.credit.integration.controller;

import by.afinny.credit.controller.DocumentController;
import by.afinny.credit.document.CreditOrderDocument;
import by.afinny.credit.dto.RequestCreditOrderDocumentDto;
import by.afinny.credit.entity.CreditOrder;
import by.afinny.credit.entity.Product;
import by.afinny.credit.integration.config.annotation.TestWithMongoContainer;
import by.afinny.credit.repository.CreditOrderRepository;
import by.afinny.credit.repository.DocumentRepository;
import by.afinny.credit.repository.ProductRepository;
import by.afinny.credit.utils.CompareClass;
import by.afinny.credit.utils.InitClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestWithMongoContainer
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Integration test for document controller")
public class DocumentControllerIT {
    public Product product;
    public CreditOrder creditOrder;
    private final UUID CLIENT_ID = UUID.fromString("9af9fe9a-23b7-4d55-b3dc-a929b86c87b4");
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CreditOrderRepository creditOrderRepository;

    @Autowired
    private DocumentRepository documentRepository;

    private RequestCreditOrderDocumentDto creditOrderDocumentDto;

    private CreditOrderDocument creditOrderDocument;

    @Autowired
    private InitClass initClass;
    @Autowired
    private CompareClass compareClass;


    @BeforeEach
    public void setUp() throws IOException {

        product = initClass.setUpProduct();

        creditOrder = initClass.setUpCreditOrder();


        creditOrder.setProduct(product);
        creditOrderRepository.save(creditOrder);

        productRepository.save(product);


        MockMultipartFile file = new MockMultipartFile("file",
                "document.jpg",
                "image/jpeg",
                "Hello, World!".getBytes());

        creditOrderDocument = CreditOrderDocument.builder()
                .id(UUID.randomUUID())
                .clientId(CLIENT_ID)
                .creditOrderId(creditOrder.getId())
                .creationDate(LocalDate.now())
                .documentName("page3")
                .fileFormat(".jpg")
                .file(file.getBytes())
                .build();


        creditOrderDocumentDto = RequestCreditOrderDocumentDto.builder()
                .file(file)
                .build();

        documentRepository.deleteAll();
        documentRepository.save(creditOrderDocument);


    }


    @Test
    @DisplayName("If successfully saved documents then don't return content")
    void uploadingDocuments_shouldNotReturnContent() throws Exception {
        //ACT&VERIFY
        mockMvc.perform(post("/auth/credit-order-documents/new")
                        .param("clientId", CLIENT_ID.toString())
                        .param("creditOrderId", creditOrderRepository.findAll().get(0).getId().toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .flashAttr("file", creditOrderDocumentDto))
                .andExpect(status().isOk());

    }


    @Test
    @DisplayName("Get document dto")
    void findDocument_thenReturnDocument() throws Exception {


        //ACT
        MvcResult result = mockMvc.perform(
                        get("/auth/credit-order-documents/" + CLIENT_ID))
                .andExpect(status().isOk())
                .andReturn();

        String actual = result.getResponse().getContentAsString();
        //VERIFY
        String expected = compareClass.asJsonString(List.of(creditOrderDocument));
        compareClass.verifyBody(actual, expected);
    }

    @Test
    @DisplayName("If document was successfully delete then return status No_Content")
    void deleteDocument_ifSuccessfullyDeleted_then204_NO_CONTENT() throws Exception {

        //ACT
        ResultActions perform = mockMvc.perform(
                delete(DocumentController.URL_CREDIT_DOCUMENT
                        + DocumentController.URL_DOCUMENT_ID, creditOrderDocument.getId())
                        .param("clientId", CLIENT_ID.toString()));

        //VERIFY
        perform.andExpect(status().isNoContent());

    }

}
