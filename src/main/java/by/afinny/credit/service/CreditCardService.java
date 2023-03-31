package by.afinny.credit.service;

import by.afinny.credit.dto.CardInfoDto;
import by.afinny.credit.dto.CreditCardDto;
import by.afinny.credit.dto.CreditCardLimitDto;
import by.afinny.credit.dto.CreditCardPinCodeDto;
import by.afinny.credit.dto.RequestCardStatusDto;
import by.afinny.credit.entity.constant.CardStatus;

import java.util.List;
import java.util.UUID;

public interface CreditCardService {

    List<CreditCardDto> getCreditCards(UUID clientId);

    void changeCardStatus(UUID clientId, RequestCardStatusDto requestCardStatusDto);

    void modifyCardStatus(UUID clientId, String cardNumber, CardStatus newCardStatus);

    CardInfoDto getCardInformation(UUID clientId, UUID cardId);

    void setCreditCardLimit(UUID clientId, CreditCardLimitDto creditCardLimitDto);

    void deleteCreditCard(UUID clientId, UUID cardId);

    String getCardNumber(UUID clientId, UUID cardId);

    void changeCardPinCode(UUID clientId, CreditCardPinCodeDto creditCardPinCodeDto);
}