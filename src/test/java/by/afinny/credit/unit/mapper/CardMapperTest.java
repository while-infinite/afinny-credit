package by.afinny.credit.unit.mapper;

import by.afinny.credit.dto.CardInfoDto;
import by.afinny.credit.dto.CreditCardDto;
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
import by.afinny.credit.mapper.CardMapperImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@DisplayName("Verification of correct data generation. It will pass if the fields of the entity and dto are equal")
class CardMapperTest {

    @InjectMocks
    private CardMapperImpl cardMapper;

    private Card card;
    private Account account;
    private Product product;
    private Credit credit;
    private CreditOrder creditOrder;
    private Agreement agreement;
    private CardInfoDto cardInfoDto;
    private List<CreditCardDto> cardsDto;
    private RequestCardStatusDto cardStatusDto;
    private final String CARD_NUMBER = "1111222233334444";

    private List<Card> cards;

    @BeforeAll
    void setUp() {
        product = Product.builder()
                .name("TEST")
                .build();

        creditOrder = CreditOrder.builder()
                .product(product)
                .build();

        agreement = Agreement.builder()
                .terminationDate(LocalDate.now())
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
                .id(UUID.randomUUID())
                .cardNumber("1111222233334444")
                .balance(BigDecimal.valueOf(5000.00))
                .holderName("Ivan Petrov")
                .expirationDate(LocalDate.now())
                .paymentSystem(PaymentSystem.VISA)
                .status(CardStatus.ACTIVE)
                .transactionLimit(BigDecimal.valueOf(10000))
                .account(account)
                .build();

        cardStatusDto = RequestCardStatusDto.builder()
                .cardNumber(CARD_NUMBER)
                .cardStatus(CardStatus.ACTIVE)
                .build();

        cards = List.of(Card.builder()
                        .cardNumber("58551")
                        .balance(BigDecimal.TEN)
                        .id(UUID.randomUUID())
                        .build(),
                Card.builder()
                        .cardNumber("588788")
                        .balance(BigDecimal.ONE)
                        .id(UUID.randomUUID())
                        .build());
    }

    @Test
    @DisplayName("Verify credit card dto fields setting")
    void toCardsDto_shouldReturnCorrectMappingData() {
        //ACT
        cardsDto = cardMapper.cardsToCardsDto(List.of(card));
        //VERIFY
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(cardsDto.get(0).getId())
                    .withFailMessage("Id should be equals")
                    .isEqualTo(card.getId());
            softAssertions.assertThat(cardsDto.get(0).getCardNumber())
                    .withFailMessage("Card number should be equals")
                    .isEqualTo(card.getCardNumber());
            softAssertions.assertThat(cardsDto.get(0).getBalance())
                    .withFailMessage("Balance should be equals")
                    .isEqualTo(card.getBalance());
            softAssertions.assertThat(cardsDto.get(0).getCurrencyCode())
                    .withFailMessage("Currency code limit should be equals")
                    .isEqualTo(account.getCurrencyCode());
            softAssertions.assertThat(cardsDto.get(0).getName())
                    .withFailMessage("Product name should be equals")
                    .isEqualTo(product.getName());
            softAssertions.assertThat(cardsDto.get(0).getPaymentSystem())
                    .withFailMessage("Payment system should be equals")
                    .isEqualTo(card.getPaymentSystem());
            softAssertions.assertThat(cardsDto.get(0).getExpirationDate())
                    .withFailMessage("Expiration date should be equals")
                    .isEqualTo(card.getExpirationDate());
            softAssertions.assertThat(cardsDto.get(0).getAccountNumber())
                    .withFailMessage("Account number should be equals")
                    .isEqualTo(card.getAccount().getAccountNumber());
        });
    }

    @Test
    @DisplayName("Verify card event dto fields setting")
    void requestDtoToCardEvent() {
        //ACT
        CardEvent cardEvent = cardMapper.requestDtoToCardEvent(cardStatusDto);
        //VERIFY
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(cardEvent.getCardNumber()).isEqualTo(cardStatusDto.getCardNumber());
            softAssertions.assertThat(cardEvent.getCardStatus()).isEqualTo(cardStatusDto.getCardStatus());
        });
    }

    @Test
    @DisplayName("Verify card balance dto fields setting")
    void toCardInfoDto_shouldReturnCorrectMappingData(){
        // ACT
        cardInfoDto = cardMapper.toCardInfoDto(card, account, credit, product, agreement);
        //VERIFY
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(cardInfoDto.getAccountNumber())
                    .withFailMessage("Account number should be equals")
                    .isEqualTo(account.getAccountNumber());
            softAssertions.assertThat(cardInfoDto.getBalance())
                    .withFailMessage("Balance should be equals")
                    .isEqualTo(card.getBalance());
            softAssertions.assertThat(cardInfoDto.getHolderName())
                    .withFailMessage("Holder name should be equals")
                    .isEqualTo(card.getHolderName());
            softAssertions.assertThat(cardInfoDto.getExpirationDate())
                    .withFailMessage("Expiration date should be equals")
                    .isEqualTo(card.getExpirationDate().toString());
            softAssertions.assertThat(cardInfoDto.getAccountNumber())
                    .withFailMessage("Account number should be equals")
                    .isEqualTo(card.getAccount().getAccountNumber());
            softAssertions.assertThat(cardInfoDto.getPaymentSystem())
                    .withFailMessage("Payment system should be equals")
                    .isEqualTo(card.getPaymentSystem());
            softAssertions.assertThat(cardInfoDto.getStatus())
                    .withFailMessage("Status should be equals")
                    .isEqualTo(card.getStatus());
            softAssertions.assertThat(cardInfoDto.getTransactionLimit())
                    .withFailMessage("Transaction limit should be equals")
                    .isEqualTo(card.getTransactionLimit());
            softAssertions.assertThat(cardInfoDto.getName())
                    .withFailMessage("Name should be equals")
                    .isEqualTo(account.getCredit().getCreditOrder().getProduct().getName());
            softAssertions.assertThat(cardInfoDto.getPrincipalDebt())
                    .withFailMessage("Principal debt should be equals")
                    .isEqualTo(account.getPrincipalDebt());
            softAssertions.assertThat(cardInfoDto.getCreditLimit())
                    .withFailMessage("Credit limit should be equals")
                    .isEqualTo(account.getCredit().getCreditLimit());
            softAssertions.assertThat(cardInfoDto.getCreditCurrencyCode())
                    .withFailMessage("Currency code should be equals")
                    .isEqualTo(credit.getCurrencyCode());
            softAssertions.assertThat(cardInfoDto.getTerminationDate())
                    .withFailMessage("Termination date should be equals")
                    .isEqualTo(account.getCredit().getAgreement().getTerminationDate().toString());
        });
    }
}




