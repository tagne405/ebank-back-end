package com.tyannick.ebanque.Dtos;

import lombok.Data;

import java.util.List;

@Data
public class AccountHistoryDto {
    private int currentPage;
    private int totalPages;
    private int pageSize;
    private List<AccountOperationDto> accountOperationDto;
    private String accountId;
    private double balance;

}
