package by.afinny.credit.mapper;

import by.afinny.credit.dto.ResponseOperationDto;
import by.afinny.credit.entity.Operation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface OperationMapper {


    @Mapping(source = "operations.account.id", target = "accountId")
    @Mapping(source = "operations.account.credit.type", target = "type")
    @Mapping(source = "operations.type.type", target = "operationType")
    List<ResponseOperationDto> detailsOperationToResponseOperations(List<Operation> operations);

    @Mapping(source = "operation.account.id", target = "accountId")
    @Mapping(source = "operation.account.credit.type", target = "type")
    @Mapping(source = "operation.type.type", target = "operationType")
    ResponseOperationDto detailsOperationToResponseOperation(Operation operation);
}
