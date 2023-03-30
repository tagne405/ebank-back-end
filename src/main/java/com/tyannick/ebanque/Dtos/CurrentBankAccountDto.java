package com.tyannick.ebanque.Dtos;


import com.tyannick.ebanque.Enums.AccountStatus;
import lombok.Data;

import java.util.Date;

@Data

public class CurrentBankAccountDto extends BankAccountDto{

    private String id;
    private Date dateCreated;
    private double balance;
    private AccountStatus status;
    private CustomerDto customerDto;
    private double overDraft;

}
