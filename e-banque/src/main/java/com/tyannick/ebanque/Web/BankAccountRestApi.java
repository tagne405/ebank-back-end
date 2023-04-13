package com.tyannick.ebanque.Web;

import com.tyannick.ebanque.Dtos.AccountHistoryDto;
import com.tyannick.ebanque.Dtos.AccountOperationDto;
import com.tyannick.ebanque.Dtos.BankAccountDto;
import com.tyannick.ebanque.Dtos.CreditDto;
import com.tyannick.ebanque.Dtos.DebitDto;
import com.tyannick.ebanque.Dtos.TransferRequestDto;
import com.tyannick.ebanque.Exception.BalanceNotSufficientException;
import com.tyannick.ebanque.Exception.BankAccountNotFoundException;
import com.tyannick.ebanque.Service.BankAccountService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin("*")
public class BankAccountRestApi {
    private BankAccountService bankAccountService;

    public BankAccountRestApi(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping("/accounts/{accountId}")
    public BankAccountDto getBankAccount(@PathVariable String accountId) throws BankAccountNotFoundException {
        return bankAccountService.getBankAccount(accountId);
    }

    @GetMapping("/accounts")
    public List<BankAccountDto> listAccounts(){
        return bankAccountService.bankAccountsList();
    }

    @GetMapping("/accounts/{accountId}/operations")
    public List<AccountOperationDto> getHistory(@PathVariable String accountId){
        return bankAccountService.accountHistory(accountId);
    }

    @GetMapping("/accounts/{accountId}/pageOperations")
    public AccountHistoryDto getAccountHistory(@PathVariable String accountId,
                                               @RequestParam(name = "page",defaultValue = "0") int page,
                                               @RequestParam(name = "size",defaultValue = "2") int size) throws BankAccountNotFoundException {
        return bankAccountService.getAccountHistory(accountId,page,size);
    }

    @PostMapping("/accounts/debit")
    public DebitDto debit(@RequestBody DebitDto debitDto) throws BankAccountNotFoundException, BalanceNotSufficientException{
        this.bankAccountService.debit(debitDto.getAccountId(), debitDto.getAmount(), debitDto.getDescription());
        return debitDto;
    }

    @PostMapping("/accounts/credit")
    public CreditDto credit(@RequestBody CreditDto creditDto) throws BankAccountNotFoundException, BalanceNotSufficientException{
        this.bankAccountService.debit(creditDto.getAccountId(), creditDto.getAmount(), creditDto.getDescription());
        return creditDto;
    }

    @PostMapping("/accounts/transfer")
    public void transfer(@RequestBody TransferRequestDto transferRequestDto) throws BankAccountNotFoundException, BalanceNotSufficientException{
        this.bankAccountService.transfer(transferRequestDto.getAccountSource(),
                                         transferRequestDto.getAccountDestination(),
                                         transferRequestDto.getAmount());
    }
}
