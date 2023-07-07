package com.example.diplomaproject.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestTransactionDTO {

    private String strDate;
    private Double sum;
    private String currency;
}
