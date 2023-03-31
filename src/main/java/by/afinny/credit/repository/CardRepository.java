package by.afinny.credit.repository;

import by.afinny.credit.entity.Card;
import by.afinny.credit.entity.constant.CardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {

    List<Card> findByAccountCreditCreditOrderClientIdAndStatusNot(UUID clientId, CardStatus excludingStatus);

    Optional<Card> findByAccountCreditCreditOrderClientIdAndCardNumber(UUID clientId, String cardNumber);

    Optional<Card> findByAccountCreditCreditOrderClientIdAndId(UUID clientId, UUID cardId);

    void deleteById(UUID cardId);
}