package by.afinny.credit.service;

import by.afinny.credit.dto.LoanCalculationDto;
import by.afinny.credit.entity.CreditOrder;

public interface CalculationService {
    LoanCalculationDto loanCalculation (CreditOrder creditOrder);
}
