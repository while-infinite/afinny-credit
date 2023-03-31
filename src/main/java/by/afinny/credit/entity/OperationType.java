package by.afinny.credit.entity;

import by.afinny.credit.entity.constant.Type;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "operation_type")
@NoArgsConstructor
@Getter
@Setter(AccessLevel.PUBLIC)
@ToString
public class OperationType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 30, nullable = false)
    private Type type;

    @Column(name = "is_debit", nullable = false)
    private Boolean debit;
}
