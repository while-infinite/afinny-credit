package by.afinny.credit.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = Agreement.TABLE_NAME)
@Getter
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agreement {

    public static final String TABLE_NAME = "agreement";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "number", nullable = false, length = 20)
    private String number;

    @Column(name = "agreement_date", nullable = false)
    private LocalDate agreementDate;

    @Column(name = "termination_date", nullable = false)
    private LocalDate terminationDate;

    @Column(name = "responsible_specialist_id", length = 20)
    private String responsibleSpecialistId;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "credit_id",referencedColumnName = "id")
    private Credit credit;
}
