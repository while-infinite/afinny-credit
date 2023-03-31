package by.afinny.credit.service.impl;

import by.afinny.credit.dto.LoanCalculationDto;
import by.afinny.credit.entity.Account;
import by.afinny.credit.entity.Agreement;
import by.afinny.credit.entity.Credit;
import by.afinny.credit.entity.CreditOrder;
import by.afinny.credit.entity.PaymentSchedule;
import by.afinny.credit.entity.constant.CreditStatus;
import by.afinny.credit.mapper.AccountMapper;
import by.afinny.credit.mapper.CreditMapper;
import by.afinny.credit.mapper.LoanCalculationMapper;
import by.afinny.credit.repository.AccountRepository;
import by.afinny.credit.repository.AgreementRepository;
import by.afinny.credit.repository.CreditRepository;
import by.afinny.credit.repository.PaymentScheduleRepository;
import by.afinny.credit.service.CalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalculationServiceImpl implements CalculationService {

    private final CreditMapper creditMapper;
    private final AccountMapper accountMapper;
    private final LoanCalculationMapper loanCalculationMapper;

    private final AccountRepository accountRepository;
    private final CreditRepository creditRepository;
    private final PaymentScheduleRepository paymentScheduleRepository;
    private final AgreementRepository agreementRepository;

    @Override
    @Transactional
    public LoanCalculationDto loanCalculation(CreditOrder creditOrder) {
        log.info("loanCalculation() method invoke");

        BigDecimal principal = creditOrder.getAmount().divide(BigDecimal.valueOf(creditOrder.getPeriodMonths()), 2, RoundingMode.HALF_UP);
        BigDecimal interest = (creditOrder.getAmount().multiply(creditOrder.getProduct().getRateFixPart().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP))).divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
        BigDecimal principalDebt = creditOrder.getAmount();
        BigDecimal interestDebt = interest.multiply(BigDecimal.valueOf(creditOrder.getPeriodMonths()));
        LocalDate creationDate = creditOrder.getCreationDate();
        LocalDate paymentDate = creationDate.plusMonths(1);
        BigDecimal creditLimit = principalDebt.add(interestDebt);
        log.info("all parameters calculated");

        Credit credit = createCredit(creditOrder, creditLimit);
        creditRepository.save(credit);
        log.info("credit was saved");

        Account account = createAccount(creditOrder, principalDebt, interestDebt, credit);
        accountRepository.save(account);
        log.info("account was saved");

        PaymentSchedule paymentSchedule = createPaymentSchedule(account, paymentDate, principal, interest);
        paymentScheduleRepository.save(paymentSchedule);
        log.info("paymentSchedule was saved");

        Agreement agreement = createAgreement(credit, creditOrder);
        agreementRepository.save(agreement);
        log.info("agreement was saved");

        return loanCalculationMapper.toLoanCalculationDto(
                principal,
                interest,
                principalDebt,
                interestDebt,
                paymentDate);
    }

    private Agreement createAgreement(Credit credit, CreditOrder creditOrder) {
        log.info("createAgreement() method invoke");
        Agreement agreement = new Agreement();
        agreement.setCredit(credit);
        agreement.setNumber(createAccountNumber());
        agreement.setAgreementDate(creditOrder.getCreationDate());
        agreement.setTerminationDate(creditOrder.getCreationDate().plusMonths(creditOrder.getPeriodMonths()));
        agreement.setResponsibleSpecialistId("auto_processing");
        agreement.setIsActive(true);
        return agreement;
    }

    private Credit createCredit(CreditOrder creditOrder, BigDecimal creditLimit) {
        log.info("createCredit() method invoke");
        Credit credit = creditMapper.toCreditEntity(creditOrder);
        credit.setStatus(CreditStatus.ACTIVE);
        credit.setPersonalGuarantees(false);
        credit.setCreditLimit(creditLimit);
        return credit;
    }

    private Account createAccount(CreditOrder creditOrder, BigDecimal principalDebt, BigDecimal interestDebt, Credit credit) {
        log.info("createAccount() method invoke");
        Account account = accountMapper.toAccountEntity(creditOrder);
        account.setPrincipalDebt(principalDebt);
        account.setInterestDebt(interestDebt);
        account.setClosingDate(creditOrder.getCreationDate().plusMonths(creditOrder.getPeriodMonths()));
        account.setAccountNumber(createAccountNumber());
        account.setIsActive(true);
        account.setCredit(credit);
        return account;
    }

    private PaymentSchedule createPaymentSchedule(Account account, LocalDate paymentDate, BigDecimal principal, BigDecimal interest) {
        log.info("createPaymentSchedule() method invoke");
        PaymentSchedule paymentSchedule = new PaymentSchedule();
        paymentSchedule.setAccount(account);
        paymentSchedule.setPaymentDate(paymentDate);
        paymentSchedule.setPrincipal(principal);
        paymentSchedule.setInterest(interest);
        return paymentSchedule;
    }

    private String createAccountNumber() {
        log.info("createAccountNumber() method invoke");
        Integer[] i = new Integer[20];
        for (int j = 0; j <= 19; j++) {
            int random = (int) (Math.random() * (10));
            i[j] = random;
        }
        StringBuilder number = new StringBuilder();
        for (Integer integer : i) {
            number.append(integer);
        }
        return String.valueOf(number);
    }
}
