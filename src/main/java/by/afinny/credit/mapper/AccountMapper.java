package by.afinny.credit.mapper;

import by.afinny.credit.entity.Account;
import by.afinny.credit.entity.CreditOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AccountMapper {

    @Mapping(source = "creditOrder.creationDate",target = "openingDate")
    @Mapping(source = "creditOrder.product.currencyCode",target = "currencyCode")
    @Mapping(ignore = true, target = "id")
    Account toAccountEntity (CreditOrder creditOrder);
}
