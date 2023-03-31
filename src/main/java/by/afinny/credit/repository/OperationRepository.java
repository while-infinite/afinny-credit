package by.afinny.credit.repository;

import by.afinny.credit.entity.Account;
import by.afinny.credit.entity.Operation;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OperationRepository extends JpaRepository<Operation, UUID>, JpaSpecificationExecutor<Operation> {

    default Specification<Operation> findAllOperationCreditTransactions(Account account) {
        return (root, query, builder) -> builder.equal(root.get("account"), account);
    }
}
