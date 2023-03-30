package com.tyannick.ebanque.Dtos;


import java.util.Date;

import com.tyannick.ebanque.Enums.AccountStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class SavingBankAccountDto extends BankAccountDto{

    private String id;
    private Date dateCreated;
    private double balance;
    private AccountStatus status;
    private CustomerDto customerDto;
    private double interestRate;

}
