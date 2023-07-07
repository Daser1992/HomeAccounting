package com.example.diplomaproject.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "transactions")
public class WalletTransaction {
    @Id
    @GeneratedValue
    private Long id;
    private Double suma;
    private String purpose;
    private String sign;
    private String date;
    private Integer month;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    public WalletTransaction(Double suma, String purpose, String sign, String date, Integer month) {
        this.suma = suma;
        this.purpose = purpose;
        this.sign = sign;
        this.date = date;
        this.month = month;
    }
}
