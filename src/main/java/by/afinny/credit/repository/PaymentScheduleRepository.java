package by.afinny.credit.repository;

import by.afinny.credit.entity.PaymentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentScheduleRepository extends JpaRepository<PaymentSchedule, UUID> {
}
