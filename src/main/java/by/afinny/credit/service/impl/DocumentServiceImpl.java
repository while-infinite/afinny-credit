package by.afinny.credit.service.impl;

import by.afinny.credit.document.CreditOrderDocument;
import by.afinny.credit.repository.DocumentRepository;
import by.afinny.credit.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;

    @Override
    public void deleteDocument(UUID clientId, UUID documentId) {
        log.info("deleteDocument() method invoke");
        CreditOrderDocument creditOrderDocument = getCreditOrderDocumentByClientIdId(clientId, documentId);
        documentRepository.delete(creditOrderDocument);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void uploadDocument(UUID clientId, UUID creditOrderId, MultipartFile file)
            throws IOException {
        log.info("uploadingDocuments() is invoked");
        documentRepository.saveAll(List.of(createCreditOrderDocument(clientId, creditOrderId, file)));
    }

    @Override
    public List<CreditOrderDocument> getDocuments(UUID clientId) {
        log.info("getDocuments() method invoked");

        List<CreditOrderDocument> creditOrderDocuments = documentRepository.findAllByClientId(clientId);
        if (creditOrderDocuments == null) {
            throw new EntityNotFoundException("Client with id " + clientId + " don't have any documents");
        } else {
            return creditOrderDocuments;
        }
    }


    private CreditOrderDocument getCreditOrderDocumentByClientIdId(UUID clientId, UUID documentId) {
        return documentRepository.findByClientIdAndId(clientId, documentId).orElseThrow(
                () -> new EntityNotFoundException("document with id" + documentId + "wasn't found"));
    }


    private CreditOrderDocument createCreditOrderDocument (UUID clientId, UUID creditOrderId, MultipartFile file)
            throws IOException {
        return CreditOrderDocument.builder()
                .id(UUID.randomUUID())
                .clientId(clientId)
                .creditOrderId(creditOrderId)
                .creationDate(LocalDate.now())
                .fileFormat(file.getContentType())
                .documentName(file.getName())
                .file(file.getBytes())
                .build();
    }
}

