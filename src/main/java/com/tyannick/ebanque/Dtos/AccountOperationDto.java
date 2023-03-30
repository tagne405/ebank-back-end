package com.tyannick.ebanque.Dtos;

import com.tyannick.ebanque.Enums.OperationType;
import lombok.Data;
import java.util.Date;


@Data
public class AccountOperationDto {
    private Long id;
    private Date dateOperation;
    private double amount;
    private double balance;
    private OperationType type;
    private String description;
}
