package by.afinny.credit.repository;

import by.afinny.credit.entity.Credit;
import by.afinny.credit.entity.constant.CreditStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditRepository extends JpaRepository<Credit, UUID> {

    List<Credit> findByCreditOrderClientIdAndStatus(UUID clientId, CreditStatus creditStatus);

    Optional<Credit> findByCreditOrderClientIdAndId(UUID clientId, UUID creditId);

}
