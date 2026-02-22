package bg.softuni.smartbudgetapp.models;


import jakarta.persistence.*;

@Entity
@Table(name = "currencies")
public class CurrencyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private CurrencyEnum code;

    public CurrencyEntity() {
    }

    public CurrencyEntity(CurrencyEnum code) {
        this.code = code;
    }

    public Long getId() {
        return id;
    }

    public CurrencyEnum getCode() {
        return code;
    }

    public void setCode(CurrencyEnum code) {
        this.code = code;
    }

}
