package by.afinny.credit.controller;

import by.afinny.credit.dto.CreditBalanceDto;
import by.afinny.credit.dto.CreditDto;
import by.afinny.credit.dto.CreditScheduleDto;
import by.afinny.credit.dto.DetailsDto;
import by.afinny.credit.dto.ResponseOperationDto;
import by.afinny.credit.service.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth/credits")
public class CreditController {

    public static final String URL_CREDITS = "/auth/credits";
    public static final String URL_CREDIT_ID = "/{creditId}";
    public static final String URL_CREDIT_SCHEDULE = "/schedule";
    public static final String URL_AGREEMENT_ID = "/{agreementId}";
    public static final String URL_CREDIT_DETAILS = "/details";
    public static final String URL_CREDIT_HISTORY = "/history";
    public static final String PARAM_CLIENT_ID = "clientId";

    private final CreditService creditService;

    @GetMapping
    public ResponseEntity<List<CreditDto>> getClientCurrentCredits(@RequestParam UUID clientId) {
        List<CreditDto> clientCurrentCredits = creditService.getClientCreditsWithActiveStatus(clientId);

        return ResponseEntity.ok(clientCurrentCredits);
    }

    @GetMapping("/{creditId}")
    public ResponseEntity<CreditBalanceDto> getCreditBalance(@RequestParam UUID clientId, @PathVariable UUID creditId) {
        CreditBalanceDto creditBalanceDto = creditService.getCreditBalance(clientId, creditId);
        return ResponseEntity.ok(creditBalanceDto);
    }

    @GetMapping("/{creditId}/schedule")
    public ResponseEntity<CreditScheduleDto> getPaymentSchedule(@RequestParam UUID clientId, @PathVariable UUID creditId) {
        CreditScheduleDto creditBalanceDto = creditService.getPaymentSchedule(clientId, creditId);
        return ResponseEntity.ok(creditBalanceDto);
    }

    @GetMapping("/{agreementId}/details")
    public ResponseEntity<DetailsDto> getDetailsForPayment(@RequestParam UUID clientId, @PathVariable UUID agreementId) {
        DetailsDto clientCurrentCredits = creditService.getDetailsForPayment(clientId, agreementId);
        return ResponseEntity.ok(clientCurrentCredits);
    }

    @GetMapping("/{creditId}/history")
    public ResponseEntity<List<ResponseOperationDto>> getDetailsOfLastPayments(@RequestParam UUID clientId,
                                                                               @PathVariable UUID creditId,
                                                                               @RequestParam Integer pageNumber,
                                                                               @RequestParam(name = "pageSize", defaultValue = "4") Integer pageSize) {
        List<ResponseOperationDto> responseOperations = creditService.getDetailsOfLastPayments(clientId, creditId, pageNumber, pageSize);
        return ResponseEntity.ok(responseOperations);
    }
}