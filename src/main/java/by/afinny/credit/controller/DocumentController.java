package by.afinny.credit.controller;

import by.afinny.credit.document.CreditOrderDocument;
import by.afinny.credit.dto.RequestCreditOrderDocumentDto;
import by.afinny.credit.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth/credit-order-documents")
public class DocumentController {

    private final DocumentService documentService;

    public static final String URL_CREDIT_DOCUMENT = "/auth/credit-order-documents";
    public static final String URL_DOCUMENT_ID = "/{documentId}";


    @PostMapping(value = "new", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadingDocuments(@RequestParam UUID clientId, @RequestParam UUID creditOrderId,
                                                   @ModelAttribute("file") RequestCreditOrderDocumentDto
                                                           creditOrderDocument) throws IOException {
        documentService.uploadDocument(clientId, creditOrderId, creditOrderDocument.getFile());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> deleteDocument(@RequestParam UUID clientId, @PathVariable UUID documentId) {
        documentService.deleteDocument(clientId, documentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<List<CreditOrderDocument>> getDocuments(@PathVariable UUID clientId) {
        return ResponseEntity.ok(documentService.getDocuments(clientId));
    }
}
