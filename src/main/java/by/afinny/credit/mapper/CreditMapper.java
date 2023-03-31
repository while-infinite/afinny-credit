package by.afinny.credit.mapper;

import by.afinny.credit.dto.CreditBalanceDto;
import by.afinny.credit.dto.CreditDto;
import by.afinny.credit.dto.CreditScheduleDto;
import by.afinny.credit.entity.Account;
import by.afinny.credit.entity.Agreement;
import by.afinny.credit.entity.Card;
import by.afinny.credit.entity.Credit;
import by.afinny.credit.entity.CreditOrder;
import by.afinny.credit.entity.PaymentSchedule;
import by.afinny.credit.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = {ScheduleMapper.class, CardMapper.class}, componentModel = "spring")
public interface CreditMapper {

    List<CreditDto> creditsToCreditDto(List<Credit> credits);

    @Mapping(target = "creditId", source = "id")
    @Mapping(target = "name", source = "creditOrder.product.name")
    @Mapping(target = "principalDebt", source = "account.principalDebt")
    @Mapping(target = "terminationDate", source = "agreement.terminationDate")
    @Mapping(target = "creditCurrencyCode", source = "currencyCode")
    CreditDto creditToCreditDto(Credit credit);

    @Mapping(source = "credit.currencyCode", target = "creditCurrencyCode")
    @Mapping(source = "agreement.number", target = "agreementNumber")
    @Mapping(source = "agreement.id", target = "agreementId")
    @Mapping(source = "account.currencyCode", target = "accountCurrencyCode")
    @Mapping(source = "paymentSchedule.principal", target = "paymentPrincipal")
    @Mapping(source = "paymentSchedule.interest", target = "paymentInterest")
    @Mapping(source = "card", target = "card")
    CreditBalanceDto toCreditBalanceDto(Product product, Credit credit, Agreement agreement, Account account,
                                        Card card, PaymentSchedule paymentSchedule);

    @Mapping(source = "agreement.id", target = "agreementID")
    CreditScheduleDto toCreditScheduleDto(Agreement agreement, Account account, List<PaymentSchedule> paymentsSchedule);

    @Mapping(source = "creditOrder.product.typeCredit", target = "type")
    @Mapping(source = "creditOrder.product.currencyCode", target = "currencyCode")
    @Mapping(source = "creditOrder.product.rateFixPart", target = "interestRate")
    @Mapping(source = "creditOrder.product.increasedRate", target = "latePaymentRate")
    @Mapping(source = "creditOrder", target = "creditOrder")
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "status")
    Credit toCreditEntity(CreditOrder creditOrder);
}
