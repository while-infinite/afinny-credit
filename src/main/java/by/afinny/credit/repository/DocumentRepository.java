package by.afinny.credit.repository;

import by.afinny.credit.document.CreditOrderDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository extends MongoRepository<CreditOrderDocument, UUID> {

    List<CreditOrderDocument> findAllByClientId (UUID clientId);
    Optional<CreditOrderDocument> findByClientIdAndId(UUID clientId, UUID documentId);

}
