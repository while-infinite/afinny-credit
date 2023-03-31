package by.afinny.credit.service.impl;

import by.afinny.credit.dto.CardInfoDto;
import by.afinny.credit.dto.CreditCardDto;
import by.afinny.credit.dto.CreditCardLimitDto;
import by.afinny.credit.dto.CreditCardPinCodeDto;
import by.afinny.credit.dto.RequestCardStatusDto;
import by.afinny.credit.dto.kafka.CardEvent;
import by.afinny.credit.entity.Account;
import by.afinny.credit.entity.Agreement;
import by.afinny.credit.entity.Card;
import by.afinny.credit.entity.Credit;
import by.afinny.credit.entity.Product;
import by.afinny.credit.entity.constant.CardStatus;
import by.afinny.credit.exception.CardBalanceIsNotEqualsCreditLimitException;
import by.afinny.credit.exception.CardStatusesAreEqualsException;
import by.afinny.credit.exception.EntityNotFoundException;
import by.afinny.credit.mapper.CardMapper;
import by.afinny.credit.repository.CardRepository;
import by.afinny.credit.service.CreditCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditCardServiceImpl implements CreditCardService {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public List<CreditCardDto> getCreditCards(UUID clientId) {
        log.info("getCreditCards() method invoked");
        List<Card> cards = cardRepository.findByAccountCreditCreditOrderClientIdAndStatusNot(clientId, CardStatus.CLOSED);
        return cardMapper.cardsToCardsDto(cards);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void changeCardStatus(UUID clientId, RequestCardStatusDto requestCardStatus) {
        log.info("changeCardStatus() method invoke");
        Card foundCard = getCardByCardNumber(clientId, requestCardStatus.getCardNumber());
        checkStatusesEqual(foundCard.getStatus(), requestCardStatus.getCardStatus());
        foundCard.setStatus(requestCardStatus.getCardStatus());
        sendToKafka(requestCardStatus);
        cardRepository.save(foundCard);
    }

    @Override
    public void modifyCardStatus(UUID clientId, String cardNumber, CardStatus newCardStatus) {
        log.info("modifyCardStatus() method invoked");
        Card foundCard = getCardByCardNumber(clientId, cardNumber);
        CardStatus foundCardStatus = foundCard.getStatus();
        checkStatusesEqual(foundCardStatus, newCardStatus);
        log.info("Updating card status from " + foundCardStatus + " to " + newCardStatus);
        foundCard.setStatus(newCardStatus);
        cardRepository.save(foundCard);
    }

    @Override
    public CardInfoDto getCardInformation(UUID clientId, UUID cardId) {
        log.info("getBalanceCard() method invoke");

        Card card = cardRepository.findByAccountCreditCreditOrderClientIdAndId(clientId, cardId).orElseThrow(
                () -> new EntityNotFoundException("credit card with card id " + cardId + " wasn't found"));
        Account account = card.getAccount();
        Credit credit = account.getCredit();
        Product product = credit.getCreditOrder().getProduct();
        Agreement agreement = credit.getAgreement();
        return cardMapper.toCardInfoDto(card, account, credit, product, agreement);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void setCreditCardLimit(UUID clientId, CreditCardLimitDto creditCardLimitDto) {
        log.info("set credit card limit() method invoke");
        Card card = getCardByCardNumber(clientId, creditCardLimitDto.getCardNumber());
        BigDecimal newTransactionLimit = creditCardLimitDto.getTransactionLimit();
        card.setTransactionLimit(newTransactionLimit);
        cardRepository.save(card);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteCreditCard(UUID clientId, UUID cardId) {
        log.info("delete credit card() method invoke");

        Card card = cardRepository.findByAccountCreditCreditOrderClientIdAndId(clientId, cardId).orElseThrow(
                () -> new EntityNotFoundException("credit card with card id " + cardId + " wasn't found"));
        BigDecimal cardBalance = card.getBalance();
        BigDecimal creditLimit = card.getAccount().getCredit().getCreditLimit();

        if (!cardBalance.equals(creditLimit)) {
                    throw new CardBalanceIsNotEqualsCreditLimitException(
                            Integer.toString(HttpStatus.BAD_REQUEST.value()),
                            "Credit card balance is not equals credit limit");
        }
        cardRepository.deleteById(cardId);
    }

    @Override
    public String getCardNumber(UUID clientId, UUID cardId) {
        return cardRepository.findByAccountCreditCreditOrderClientIdAndId(clientId, cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card with card id " + cardId + " wasn't found"))
                .getCardNumber();
    }

    @Override
    public void changeCardPinCode(UUID clientId, CreditCardPinCodeDto creditCardPinCodeDto) {
        log.info("changeCardPinCode() method invoke");

        cardRepository.findByAccountCreditCreditOrderClientIdAndCardNumber(clientId, creditCardPinCodeDto.getCardNumber()).orElseThrow(
                () -> new EntityNotFoundException("credit card with card number " + creditCardPinCodeDto.getCardNumber() + " wasn't found"));
        sendToKafka(creditCardPinCodeDto);
    }

    private Card getCardByCardNumber(UUID clientId, String cardNumber) {
        return cardRepository.findByAccountCreditCreditOrderClientIdAndCardNumber(clientId, cardNumber)
                .orElseThrow(() -> new EntityNotFoundException("Card with card number " + cardNumber + " wasn't found"));
    }

    private void sendToKafka(RequestCardStatusDto requestCardStatus) {
        CardEvent event = cardMapper.requestDtoToCardEvent(requestCardStatus);
        log.info("Publishing event...");
        eventPublisher.publishEvent(event);
    }

    private void sendToKafka(CreditCardPinCodeDto creditCardPinCodeDto) {
        log.info("Publishing event...");
        eventPublisher.publishEvent(creditCardPinCodeDto);
    }

    private void checkStatusesEqual(CardStatus oldStatus, CardStatus newStatus) {
        if (oldStatus.equals(newStatus)) {
            log.info("Card statuses are the same");
            throw new CardStatusesAreEqualsException(
                    Integer.toString(HttpStatus.BAD_REQUEST.value()),
                    "The same card status already exists!");
        }
    }
}