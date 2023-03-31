package by.afinny.credit.unit.service;

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
import by.afinny.credit.entity.CreditOrder;
import by.afinny.credit.entity.Product;
import by.afinny.credit.entity.constant.CardStatus;
import by.afinny.credit.entity.constant.PaymentSystem;
import by.afinny.credit.exception.CardBalanceIsNotEqualsCreditLimitException;
import by.afinny.credit.exception.CardStatusesAreEqualsException;
import by.afinny.credit.exception.EntityNotFoundException;
import by.afinny.credit.mapper.CardMapper;
import by.afinny.credit.repository.CardRepository;

import by.afinny.credit.service.impl.CreditCardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles("test")
class CreditCardServiceTest {

    @InjectMocks
    private CreditCardServiceImpl creditCardService;

    @Mock
    private CardRepository cardRepository;
    @Mock
    private CardMapper cardMapper;
    @Mock
    private ApplicationEventPublisher publisher;

    private final UUID CARD_ID = UUID.fromString("6841e664-090b-4d30-8e45-2424aee6f266");

    private List<Card> cardList;
    private List<CreditCardDto> cardDtoList;
    private Card card;
    private CardEvent cardEvent;
    private Account account;
    private Product product;
    private Credit credit;
    private CreditOrder creditOrder;
    private Agreement agreement;
    private RequestCardStatusDto requestCardStatus;

    private final UUID CLIENT_ID = UUID.randomUUID();
    private final String CARD_NUMBER = "1111222233334444";
    private final CardStatus CARD_STATUS = CardStatus.ACTIVE;
    private CardInfoDto cardInfoDto;
    private CreditCardLimitDto creditCardLimitDto;
    private CreditCardPinCodeDto creditCardPinCodeDto;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .name("TEST")
                .build();

        creditOrder = CreditOrder.builder()
                .product(product)
                .build();

        agreement = Agreement.builder()
                .terminationDate(LocalDate.of(2024, 1, 2))
                .build();

        credit = Credit.builder()
                .creditLimit(BigDecimal.valueOf(150000))
                .currencyCode("182")
                .creditOrder(creditOrder)
                .agreement(agreement)
                .build();

        account = Account.builder()
                .accountNumber("123")
                .currencyCode("181")
                .principalDebt(BigDecimal.valueOf(1))
                .credit(credit)
                .build();

        card = Card.builder()
                .id(CLIENT_ID)
                .cardNumber(CARD_NUMBER)
                .holderName("holder_name")
                .expirationDate(LocalDate.of(2024, 1, 2))
                .paymentSystem(PaymentSystem.AMERICAN_EXPRESS)
                .balance(BigDecimal.valueOf(5000.00))
                .status(CardStatus.BLOCKED)
                .account(account)
                .transactionLimit(new BigDecimal(700))
                .deliveryPoint("delivery_point")
                .isDigitalWallet(false)
                .isVirtual(false)
                .coBrand("co_brand")
                .build();

        cardEvent = new CardEvent();

        cardList = List.of(card);
        cardDtoList = List.of();

        requestCardStatus = RequestCardStatusDto.builder()
                .cardNumber(CARD_NUMBER)
                .cardStatus(CARD_STATUS)
                .build();

        cardInfoDto = CardInfoDto.builder()
                .accountNumber("123")
                .balance(BigDecimal.valueOf(5000.00))
                .holderName("holder_name")
                .expirationDate(LocalDate.of(2024, 1, 2).toString())
                .paymentSystem(PaymentSystem.AMERICAN_EXPRESS)
                .status(CardStatus.BLOCKED)
                .transactionLimit(new BigDecimal(700))
                .name("TEST")
                .principalDebt(BigDecimal.valueOf(1))
                .creditLimit(BigDecimal.valueOf(150000))
                .creditCurrencyCode("182")
                .terminationDate(LocalDate.of(2024, 1, 2).toString())
                .build();

        creditCardLimitDto = CreditCardLimitDto.builder()
                .cardNumber(CARD_NUMBER)
                .transactionLimit(new BigDecimal(10000))
                .build();

        creditCardPinCodeDto = CreditCardPinCodeDto.builder()
                .cardNumber(CARD_NUMBER)
                .build();
    }

    @Test
    @DisplayName("Return active credit cards for specified client when client id was found.")
    void getCreditCards_shouldReturnListCardsDto() {
        //ARRANGE
        when(cardRepository.findByAccountCreditCreditOrderClientIdAndStatusNot(CLIENT_ID, CardStatus.CLOSED))
                .thenReturn(cardList);
        when(cardMapper.cardsToCardsDto(cardList))
                .thenReturn(cardDtoList);
        //ACT
        List<CreditCardDto> creditCardsDto = creditCardService.getCreditCards(CLIENT_ID);
        //VERIFY
        assertThat(creditCardsDto).isEqualTo(cardDtoList);
    }

    @Test
    @DisplayName("If card was found then save")
    void changeCardStatus_shouldSave() {
        //ARRANGE
        when(cardRepository.findByAccountCreditCreditOrderClientIdAndCardNumber(CLIENT_ID, CARD_NUMBER)).thenReturn(Optional.of(card));
        when(cardMapper.requestDtoToCardEvent(requestCardStatus)).thenReturn(cardEvent);
        //ACT
        creditCardService.changeCardStatus(CLIENT_ID, requestCardStatus);
        //VERIFY
        verify(publisher).publishEvent(cardEvent);
        verify(cardRepository).save(card);
    }

    @Test
    @DisplayName("If cards statuses are equals")
    void changeCardStatus_ifStatusesEquals_thenThrow() {
        //ARRANGE
        card.setStatus(CardStatus.ACTIVE);
        when(cardRepository.findByAccountCreditCreditOrderClientIdAndCardNumber(CLIENT_ID, CARD_NUMBER)).thenReturn(Optional.of(card));
        //ACT
        ThrowingCallable createOrderMethodInvocation = () -> creditCardService.changeCardStatus(CLIENT_ID, requestCardStatus);
        //VERIFY
        assertThatThrownBy(createOrderMethodInvocation).isInstanceOf(CardStatusesAreEqualsException.class);
        verify(publisher, never()).publishEvent(any());
        verify(cardRepository, never()).save(card);
    }

    @Test
    @DisplayName("If card with incoming card number wasn't found")
    void changeCardStatus_ifCardNotFound_thenThrow() {
        //ARRANGE
        when(cardRepository.findByAccountCreditCreditOrderClientIdAndCardNumber(CLIENT_ID, CARD_NUMBER)).thenReturn(Optional.empty());
        //ACT
        ThrowingCallable createOrderMethodInvocation = () -> creditCardService.changeCardStatus(CLIENT_ID, requestCardStatus);
        //VERIFY
        assertThatThrownBy(createOrderMethodInvocation).isInstanceOf(EntityNotFoundException.class);
        verify(publisher, never()).publishEvent(any());
        verify(cardRepository, never()).save(card);
    }

    @Test
    @DisplayName("If card was found then save")
    void modifyCardStatus_shouldSave() {
        //ARRANGE
        when(cardRepository.findByAccountCreditCreditOrderClientIdAndCardNumber(CLIENT_ID, CARD_NUMBER)).thenReturn(Optional.of(card));
        //ACT
        creditCardService.modifyCardStatus(CLIENT_ID, CARD_NUMBER, CARD_STATUS);
        //VERIFY
        verify(cardRepository).save(card);
    }

    @Test
    @DisplayName("If cards statuses are equals")
    void modifyCardStatus_ifStatusesEquals_thenThrow() {
        //ARRANGE
        when(cardRepository.findByAccountCreditCreditOrderClientIdAndCardNumber(CLIENT_ID, CARD_NUMBER)).thenReturn(Optional.of(card));
        //ACT
        ThrowingCallable createOrderMethodInvocation = () -> creditCardService.modifyCardStatus(CLIENT_ID, CARD_NUMBER, CardStatus.BLOCKED);
        //VERIFY
        assertThatThrownBy(createOrderMethodInvocation).isInstanceOf(CardStatusesAreEqualsException.class);
        verify(cardRepository, never()).save(card);
    }

    @Test
    @DisplayName("If card with incoming card number wasn't found")
    void modifyCardStatus_ifCardNotFound_thenThrow() {
        //ARRANGE
        when(cardRepository.findByAccountCreditCreditOrderClientIdAndCardNumber(CLIENT_ID, CARD_NUMBER)).thenReturn(Optional.empty());
        //ACT
        ThrowingCallable createOrderMethodInvocation = () -> creditCardService.modifyCardStatus(CLIENT_ID, CARD_NUMBER, CARD_STATUS);
        //VERIFY
        assertThatThrownBy(createOrderMethodInvocation).isInstanceOf(EntityNotFoundException.class);
        verify(cardRepository, never()).save(card);
    }

    @Test
    @DisplayName("If not success then throw Runtime Exception")
    void getCreditCards_ifNotSuccess_thenThrow() {
        //ARRANGE
        when(cardRepository.findByAccountCreditCreditOrderClientIdAndStatusNot(CLIENT_ID, CardStatus.CLOSED))
                .thenThrow(RuntimeException.class);
        //ACT
        ThrowingCallable throwingCallable = () -> creditCardService.getCreditCards(CLIENT_ID);
        //VERIFY
        assertThatThrownBy(throwingCallable).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("if card with incoming card number was found then return balance and credit limit")
    void getCardInformation_shouldReturnCardBalanceDto() {
        //ARRANGE
        when(cardRepository.findByAccountCreditCreditOrderClientIdAndId(CLIENT_ID, CARD_ID)).thenReturn(Optional.of(card));
        when(cardMapper.toCardInfoDto(card, account, credit, product, agreement)).thenReturn(cardInfoDto);

        //ACT
        CardInfoDto result = creditCardService.getCardInformation(CLIENT_ID, CARD_ID);

        //VERIFY
        assertThat(result).isNotNull();
        assertThat(result.toString()).isEqualTo(cardInfoDto.toString());
    }

    @Test
    @DisplayName("if card with incoming card number wasn't found then throws EntityNotFoundException")
    void getCardInformation_ifNotSuccess_thenThrow() {
        //ARRANGE
        when(cardRepository.findByAccountCreditCreditOrderClientIdAndId(CLIENT_ID, CARD_ID)).thenReturn(Optional.empty());

        //ACT
        ThrowingCallable getCardBalanceMethodInvocation = () -> creditCardService.getCardInformation(CLIENT_ID, CARD_ID);

        //VERIFY
        assertThatThrownBy(getCardBalanceMethodInvocation).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("If credit card number found then don't return content")
    void setCreditCardLimit_shouldNotReturnContent() {
        //ARRANGE
        when(cardRepository.findByAccountCreditCreditOrderClientIdAndCardNumber(CLIENT_ID, CARD_NUMBER)).thenReturn(Optional.of(card));

        //ACT
        creditCardService.setCreditCardLimit(CLIENT_ID, creditCardLimitDto);

        //VERIFY
        verifyCreditCardLimit(card);
        verify(cardRepository).save(card);
    }

    @Test
    @DisplayName("If credit card number wasn't found then throw exception")
    void setCreditCardLimit_ifClientNotFound_thenThrow() {
        //ARRANGE
        when(cardRepository.findByAccountCreditCreditOrderClientIdAndCardNumber(CLIENT_ID, CARD_NUMBER)).thenReturn(Optional.empty());

        //ACT
        ThrowingCallable changeLimitMethod = () -> creditCardService.setCreditCardLimit(CLIENT_ID, creditCardLimitDto);

        //VERIFY
        assertThatThrownBy(changeLimitMethod).isInstanceOf(EntityNotFoundException.class);
        verify(cardRepository, never()).save(card);
    }

    @Test
    @DisplayName("deleting credit card if balance equals credit limit and card exist")
    void deleteCreditCard_shouldDelete(){
        //ARRANGE
        card.setBalance(BigDecimal.ONE);
        credit.setCreditLimit(BigDecimal.ONE);
        Optional<Card> optionalCard = Optional.of(card);
        when(cardRepository.findByAccountCreditCreditOrderClientIdAndId(CLIENT_ID, CARD_ID)).thenReturn(optionalCard);

        //ACT
        creditCardService.deleteCreditCard(CLIENT_ID, CARD_ID);

        //VERIFY
        verify(cardRepository).deleteById(CARD_ID);
    }

    @Test
    @DisplayName("If credit card for deleting was not found then throw exception")
    void deleteCreditCard_ifCardNotFound_thenThrow(){
        //ARRANGE
        when(cardRepository.findByAccountCreditCreditOrderClientIdAndId(CLIENT_ID, CARD_ID)).thenReturn(Optional.empty());

        //ACT
        ThrowingCallable changeLimitMethod = () -> creditCardService.deleteCreditCard(CLIENT_ID, CARD_ID);

        //VERIFY
        assertThatThrownBy(changeLimitMethod).isInstanceOf(EntityNotFoundException.class);
        verify(cardRepository, never()).deleteById(CARD_ID);
    }

    @Test
    @DisplayName("If credit card for deleting balance not equals credit limit then throw exception")
    void deleteCreditCard_ifCardBalanceIsNotEqualsCreditLimit_thenThrow(){
        //ARRANGE
        card.setBalance(BigDecimal.ONE);
        Optional<Card> optionalCard = Optional.of(card);
        when(cardRepository.findByAccountCreditCreditOrderClientIdAndId(CLIENT_ID,CARD_ID)).thenReturn(optionalCard);

        //ACT
        ThrowingCallable changeLimitMethod = () -> creditCardService.deleteCreditCard(CLIENT_ID, CARD_ID);

        //VERIFY
        assertThatThrownBy(changeLimitMethod).isInstanceOf(CardBalanceIsNotEqualsCreditLimitException.class);
        verify(cardRepository, never()).deleteById(CARD_ID);
    }


    @Test
    @DisplayName("If card was found then save")
    void getCardNumber_shouldReturnCardNumber() {
        //ARRANGE
        when(cardRepository.findByAccountCreditCreditOrderClientIdAndId(CLIENT_ID, CARD_ID)).thenReturn(Optional.of(card));

        //ACT
        String foundCardNumber = creditCardService.getCardNumber(CLIENT_ID, CARD_ID);

        //VERIFY
        assertThat(foundCardNumber).isEqualTo(card.getCardNumber());
    }

    @Test
    @DisplayName("If card was not found then throw")
    void getCardNumber_ifNotSuccess_thenThrow() {
        //ARRANGE
        when(cardRepository.findByAccountCreditCreditOrderClientIdAndId(CLIENT_ID, CARD_ID)).thenReturn(Optional.empty());

        //ACT
        ThrowingCallable getCardNumber = () -> creditCardService.getCardNumber(CLIENT_ID, CARD_ID);

        //VERIFY
        assertThatThrownBy(getCardNumber).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("If card was not found then throw")
    void changeCardPinCode_ifNotSuccess_thenThrow(){
        //ARRANGE
        when(cardRepository.findByAccountCreditCreditOrderClientIdAndCardNumber(CLIENT_ID, CARD_NUMBER)).thenReturn(Optional.empty());

        //ACT
        ThrowingCallable createChangeCardPinCodeInvocation = () ->
                creditCardService.changeCardPinCode(CLIENT_ID, creditCardPinCodeDto);

        //VERIFY
        assertThatThrownBy(createChangeCardPinCodeInvocation).isInstanceOf(EntityNotFoundException.class);
    }

    private void verifyCreditCardLimit(Card card) {
        assertSoftly(softAssertions -> {
            BigDecimal limit = card.getTransactionLimit();
            BigDecimal newLimit = creditCardLimitDto.getTransactionLimit();
            softAssertions.assertThat(limit)
                    .withFailMessage("Transaction limit should be " + newLimit
                            + " instead of " + limit)
                    .isEqualTo(newLimit);
        });
    }
}