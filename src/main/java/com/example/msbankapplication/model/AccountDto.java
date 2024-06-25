package com.example.msbankapplication.model;

import com.example.msbankapplication.enums.CurrencyType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {

    private Long id;
    private String accountId;
    private String accNumb;
    @NotNull
    private CurrencyType currency;
    private Double balance;

}
