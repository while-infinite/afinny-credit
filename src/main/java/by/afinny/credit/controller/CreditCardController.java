package by.afinny.credit.controller;

import by.afinny.credit.dto.CardInfoDto;
import by.afinny.credit.dto.CreditCardDto;
import by.afinny.credit.dto.CreditCardLimitDto;
import by.afinny.credit.dto.CreditCardPinCodeDto;
import by.afinny.credit.dto.RequestCardStatusDto;
import by.afinny.credit.service.CreditCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth/credit-cards")
@Slf4j
public class CreditCardController {

    public static final String URL_CARDS = "/auth/credit-cards/";
    public static final String URL_CARDS_ACTIVE = "active-cards";
    public static final String URL_INFORMATION = "{cardId}/information";
    public static final String PARAM_CLIENT_ID = "clientId";
    public static final String SET_CREDIT_CARD_LIMIT_URL = "/auth/credit-cards/limit";
    public static final String URL_CARD_PIN_CODE = "/code";

    public static final String URL_CARDS_CLIENT_ID = "/auth/credit-cards/info";
    public static final String PARAM_CARD_ID = "cardId";
    public static final String URL_LIMIT = "limit";

    private final CreditCardService creditCardService;

    @GetMapping
    public ResponseEntity<List<CreditCardDto>> getCreditCards(@RequestParam UUID clientId) {
        List<CreditCardDto> creditCards = creditCardService.getCreditCards(clientId);
        return ResponseEntity.ok(creditCards);
    }

    @PatchMapping("active-cards")
    public ResponseEntity<Void> changeCardStatus(@RequestParam UUID clientId, @RequestBody RequestCardStatusDto requestCardStatusDto) {
        creditCardService.changeCardStatus(clientId, requestCardStatusDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("info")
    public ResponseEntity<CardInfoDto> getCardInformation(@RequestParam UUID clientId, @RequestParam UUID cardId) {
        CardInfoDto cardInfoDto = creditCardService.getCardInformation(clientId, cardId);
        return ResponseEntity.ok(cardInfoDto);
    }

    @PatchMapping("limit")
    public ResponseEntity<Void> setCreditCardLimit(@RequestParam UUID clientId, @RequestBody CreditCardLimitDto creditCardLimitDto) {
        creditCardService.setCreditCardLimit(clientId, creditCardLimitDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCreditCard(@RequestParam UUID clientId, @RequestParam UUID cardId) {
        creditCardService.deleteCreditCard(clientId, cardId);
        return ResponseEntity.status(204).build();
    }

    @GetMapping("{cardId}/information")
    public ResponseEntity<String> getCardNumber(@RequestParam UUID clientId, @PathVariable UUID cardId) {
        String cardNumber = creditCardService.getCardNumber(clientId, cardId);
        return ResponseEntity.ok(cardNumber);
    }

    @PostMapping("code")
    public ResponseEntity<Void> changeCardPinCode(@RequestParam UUID clientId, @RequestBody CreditCardPinCodeDto creditCardPinCodeDto) {
        creditCardService.changeCardPinCode(clientId, creditCardPinCodeDto);
        return ResponseEntity.ok().build();
    }
}