package com.example.diplomaproject.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "wallets")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private Double sum = 0D;
    private String currency = "грн";

    @ManyToOne
    @JoinColumn(name = "user_id")
    private CustomUser customUser;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL )
    private List<WalletTransaction> walletTransactions = new ArrayList<>();

    public void addWalletTransaction(WalletTransaction wt){
        walletTransactions.add(wt);
    }


    public Wallet(String name) {
        this.name = name;
    }
}
