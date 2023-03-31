package by.afinny.credit.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PUBLIC)
@ToString
public class ResponseOperationDto implements Comparable<ResponseOperationDto> {

    private UUID operationId;
    private LocalDateTime completedAt;
    private String details;
    private UUID accountId;
    private BigDecimal sum;
    private String operationType;
    private String currencyCode;
    private String type;

    @Override
    public int compareTo(ResponseOperationDto responseOperationDto) {
        return responseOperationDto.getCompletedAt().compareTo(this.getCompletedAt());
    }
}
