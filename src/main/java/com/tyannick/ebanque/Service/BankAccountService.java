package com.tyannick.ebanque.Service;

import com.tyannick.ebanque.Dtos.*;
import com.tyannick.ebanque.Exception.BalanceNotSufficientException;
import com.tyannick.ebanque.Exception.BankAccountNotFoundException;
import com.tyannick.ebanque.Exception.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {
    
    CustomerDto saveCustomer(CustomerDto customerDto);

    CurrentBankAccountDto saveCurrentBankAccount(double initialBalance, double overDraf, Long customerId) throws CustomerNotFoundException;
    
    SavingBankAccountDto saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException;
    
    List<CustomerDto> listCustomer();
    
    BankAccountDto getBankAccount(String accountId) throws BankAccountNotFoundException;
    
    void debit(String accountId, double account, String description) throws BankAccountNotFoundException, BalanceNotSufficientException;
    
    void credit(String accountId, double account, String description) throws BankAccountNotFoundException;
    
    void transfer(String accountIdSource,String accoundIdDestination, double account) throws BankAccountNotFoundException, BalanceNotSufficientException;
    
    List<BankAccountDto> bankAccountsList();
    
    CustomerDto getCustomer(Long customerId) throws CustomerNotFoundException;

    CustomerDto UpdateCustomer(CustomerDto customerDto);

    void deleteCustomer(Long customerId);

    List<AccountOperationDto> accountHistory(String accountId);

    AccountHistoryDto getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException;
}
