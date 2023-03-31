package by.afinny.credit.service;

import by.afinny.credit.document.CreditOrderDocument;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface DocumentService {

    void deleteDocument(UUID clientId, UUID documentId);

    void uploadDocument(UUID clientId, UUID creditOrderId, MultipartFile file) throws IOException;

   List<CreditOrderDocument> getDocuments (UUID clientId);
}
