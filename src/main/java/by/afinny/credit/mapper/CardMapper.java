package by.afinny.credit.mapper;

import by.afinny.credit.dto.CardInfoDto;
import by.afinny.credit.dto.CreditCardBalanceDto;
import by.afinny.credit.dto.CreditCardDto;
import by.afinny.credit.dto.RequestCardStatusDto;
import by.afinny.credit.dto.kafka.CardEvent;
import by.afinny.credit.entity.Account;
import by.afinny.credit.entity.Agreement;
import by.afinny.credit.entity.Card;
import by.afinny.credit.entity.Credit;
import by.afinny.credit.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface CardMapper {

    @Mapping(target = "currencyCode", source = "cards.account.currencyCode")
    @Mapping(target = "name", source = "cards.account.credit.creditOrder.product.name")
    @Mapping(target = "accountNumber", source = "cards.account.accountNumber")
    List<CreditCardDto> cardsToCardsDto(List<Card> cards);

    @Mapping(target = "currencyCode", source = "card.account.currencyCode")
    @Mapping(target = "name", source = "card.account.credit.creditOrder.product.name")
    @Mapping(target = "accountNumber", source = "card.account.accountNumber")
    CreditCardDto cardToCardDto(Card card);

    CardEvent requestDtoToCardEvent(RequestCardStatusDto requestCardStatus);
    @Mapping(source = "credit.id", target = "creditId")
    @Mapping(source = "card.status", target = "status")
    @Mapping(source = "credit.currencyCode", target = "creditCurrencyCode")
    CardInfoDto toCardInfoDto(Card card, Account account, Credit credit, Product product, Agreement agreement);

    List<CreditCardBalanceDto> toCreditCardsDto(List<Card> cards);
    @Mapping(source = "card.id", target = "cardId")
    CreditCardBalanceDto toCreditCardDto(Card card);
}