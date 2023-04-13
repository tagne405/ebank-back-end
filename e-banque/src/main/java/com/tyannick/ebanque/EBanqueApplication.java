package com.tyannick.ebanque;

import com.tyannick.ebanque.Dtos.BankAccountDto;
import com.tyannick.ebanque.Dtos.CurrentBankAccountDto;
import com.tyannick.ebanque.Dtos.CustomerDto;
import com.tyannick.ebanque.Dtos.SavingBankAccountDto;
import com.tyannick.ebanque.Entities.*;
import com.tyannick.ebanque.Enums.AccountStatus;
import com.tyannick.ebanque.Enums.OperationType;
import com.tyannick.ebanque.Exception.BalanceNotSufficientException;
import com.tyannick.ebanque.Exception.BankAccountNotFoundException;
import com.tyannick.ebanque.Exception.CustomerNotFoundException;
import com.tyannick.ebanque.Repository.AccountOperationRepository;
import com.tyannick.ebanque.Repository.BankAccountRepository;
import com.tyannick.ebanque.Repository.CustomerRepository;
import com.tyannick.ebanque.Service.BankAccountService;

import org.apache.catalina.authenticator.SpnegoAuthenticator.AcceptAction;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;


@SpringBootApplication

public class EBanqueApplication {

    public static void main(String[] args) {
        SpringApplication.run(EBanqueApplication.class, args);
    }

    // @Bean
    CommandLineRunner commandLineRunner(BankAccountService bankAccountService){
        return args -> {
           Stream.of("Hassan","Imane","Mohamed").forEach(name->{
               CustomerDto customer=new CustomerDto();
               customer.setName(name);
               customer.setEmail(name+"@gmail.com");
               bankAccountService.saveCustomer(customer);
           });
           bankAccountService.listCustomer().forEach(customer->{
               try {
                   bankAccountService.saveCurrentBankAccount(Math.random()*90000,9000,customer.getId());
                   bankAccountService.saveSavingBankAccount(Math.random()*120000,5.5,customer.getId());

               } catch (CustomerNotFoundException e) {
                   e.printStackTrace();
               }
           });
            List<BankAccountDto> bankAccounts = bankAccountService.bankAccountsList();
            for (BankAccountDto bankAccount:bankAccounts){
                for (int i = 0; i <10 ; i++) {
                    String accountId;
                    if(bankAccount instanceof SavingBankAccountDto){
                        accountId=((SavingBankAccountDto) bankAccount).getId();
                    } else{
                        accountId=((CurrentBankAccountDto) bankAccount).getId();
                    }
                    bankAccountService.credit(accountId,10000+Math.random()*120000,"Credit");
                    bankAccountService.debit(accountId,1000+Math.random()*9000,"Debit");
                }
            }
        };
    }

    @Bean
    CommandLineRunner Start(BankAccountService bankAccountService){
        return args -> {
            Stream.of("Tagne","Yannick","Ivan","Joel","Jean").forEach(name->{
                // Customer custom=new Customer();
                CustomerDto customer=new CustomerDto();
                customer.setName(name);
                customer.setEmail(name+"@gmail.com");
                bankAccountService.saveCustomer(customer);
            });
            bankAccountService.listCustomer().forEach(customer -> {
                try {
                    bankAccountService.saveCurrentBankAccount(Math.random()*90000,9000,customer.getId());
                   bankAccountService.saveSavingBankAccount(Math.random()*120000,5.5,customer.getId());

                    List<BankAccountDto> bankAccounts = bankAccountService.bankAccountsList();
                    for (BankAccountDto bankAccount:bankAccounts){
                        for (int i = 0; i < 4; i++) {
                            String accountId;
                            if(bankAccount instanceof SavingBankAccountDto){
                                accountId=((SavingBankAccountDto) bankAccount).getId();
                                
                            }else{
                                accountId=((CurrentBankAccountDto) bankAccount).getId();
                            }
                            bankAccountService.credit(accountId,1000+Math.random()*1000,"credit");
                            bankAccountService.debit(accountId,500+Math.random()*450,"debit");
                        
                        }
                    }
                } catch (CustomerNotFoundException e) {
                   e.printStackTrace();
                } catch (BankAccountNotFoundException | BalanceNotSufficientException e) {
                    e.printStackTrace();
                }; 
            });    
            
        };
    }

    // @Bean
    CommandLineRunner Start(CustomerRepository customerRepository,
                            AccountOperationRepository accountOperationRepository,
                            BankAccountRepository bankAccountRepository){
        return args -> {
            Stream.of("Tagne","Yannick","Jean","Marc").forEach(name->{
                Customer customer=new Customer();
                customer.setName(name);
                customer.setEmail(name+"@gmail.com");
                customerRepository.save(customer);
            });

            customerRepository.findAll().forEach(cust->{
                CurrentAccount currentAccount=new CurrentAccount();
                currentAccount.setId(UUID.randomUUID().toString());
                currentAccount.setDateCreated(new Date());
                currentAccount.setBalance(Math.random()*9000);
                currentAccount.setStatus(AccountStatus.CREATED);
                currentAccount.setOverdraft(8000);
                currentAccount.setCustomer(cust);
                bankAccountRepository.save(currentAccount);

                SavingAccount savingAccount=new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());
                savingAccount.setDateCreated(new Date());
                savingAccount.setBalance(Math.random()*5);
                savingAccount.setStatus(AccountStatus.CREATED);
                savingAccount.setInterestingRate(2.4);
                savingAccount.setCustomer(cust);
                bankAccountRepository.save(savingAccount);
            });
            bankAccountRepository.findAll().forEach(op->{
                for (int i = 0; i < 5 ; i++) {
                    AccountOperation accountOperation=new AccountOperation();
                    accountOperation.setDateOperation(new Date());
                    accountOperation.setAmount(Math.random()*2);
                    accountOperation.setType(Math.random()>0.5? OperationType.DEBIT:OperationType.CREDIT);
                    accountOperation.setBankAccount(op);
                    accountOperationRepository.save(accountOperation);
                }


            });

        };
    }

}
