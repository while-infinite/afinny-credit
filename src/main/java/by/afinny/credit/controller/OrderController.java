package by.afinny.credit.controller;

import by.afinny.credit.dto.RequestCreditOrderDto;
import by.afinny.credit.dto.ResponseCreditOrderDto;
import by.afinny.credit.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("auth/credit-orders")
public class OrderController {

    public static final String URL_CREDIT_ORDERS = "/auth/credit-orders/";
    public static final String URL_NEW = "new";
    public static final String PARAM_CLIENT_ID = "clientId";
    public static final String PARAM_CREDIT_ORDER_ID = "/{creditOrderId}/pending";

    private final OrderService orderService;

    @PostMapping("new")
    public ResponseEntity<ResponseCreditOrderDto> createOrder(
            @RequestParam UUID clientId, @RequestBody RequestCreditOrderDto dto) {

        ResponseCreditOrderDto result = orderService.createOrder(clientId, dto);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<ResponseCreditOrderDto>> getCreditOrders(@RequestParam UUID clientId) {
        List<ResponseCreditOrderDto> result = orderService.getCreditOrders(clientId);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("{creditOrderId}/pending")
    public ResponseEntity<Void> deleteCreditOrder(@RequestParam UUID clientId, @PathVariable UUID creditOrderId) {
        orderService.deleteCreditOrder(clientId, creditOrderId);
        return ResponseEntity.noContent().build();
    }
}