package by.afinny.credit.document;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.UUID;

@Document(collection = "Credit Order")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@Getter
@Setter(AccessLevel.PUBLIC)
public class CreditOrderDocument {

    @Id
    private UUID id;
    private UUID clientId;
    private UUID creditOrderId;
    private LocalDate creationDate;
    private String documentName;
    private String fileFormat;
    private byte[] file;
}
