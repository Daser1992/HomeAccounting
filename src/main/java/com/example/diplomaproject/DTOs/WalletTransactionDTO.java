package com.example.diplomaproject.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransactionDTO {

    private String walletName;
    private Double sum;
    private String purpose;
    private String sign;

}
