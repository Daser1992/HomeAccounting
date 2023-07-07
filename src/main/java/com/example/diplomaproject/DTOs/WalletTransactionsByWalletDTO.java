package com.example.diplomaproject.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransactionsByWalletDTO {

    private Long id;
    private Double suma;
    private String purpose;
    private String sign;
    private String date;
    private String walletCurrency;
    private String walletName;
}
