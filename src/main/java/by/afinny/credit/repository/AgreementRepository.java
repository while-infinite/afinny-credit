package by.afinny.credit.repository;

import by.afinny.credit.entity.Agreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AgreementRepository extends JpaRepository<Agreement, UUID> {

    Optional<Agreement> findByCreditCreditOrderClientIdAndId(UUID clientId, UUID agreementId);

}
