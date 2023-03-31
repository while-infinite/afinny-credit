package by.afinny.credit.repository;

import by.afinny.credit.entity.CreditOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CreditOrderRepository extends JpaRepository<CreditOrder, UUID> {

    List<CreditOrder> findAllByClientId(UUID clientId);

    Optional<CreditOrder> findByClientIdAndId(UUID clientId, UUID creditOrderId);
}
