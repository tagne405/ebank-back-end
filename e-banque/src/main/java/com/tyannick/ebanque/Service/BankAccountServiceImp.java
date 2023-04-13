package com.tyannick.ebanque.Service;

import com.tyannick.ebanque.Dtos.*;
import com.tyannick.ebanque.Entities.*;
import com.tyannick.ebanque.Enums.AccountStatus;
import com.tyannick.ebanque.Enums.OperationType;
import com.tyannick.ebanque.Exception.BalanceNotSufficientException;
import com.tyannick.ebanque.Exception.BankAccountNotFoundException;
import com.tyannick.ebanque.Exception.CustomerNotFoundException;
import com.tyannick.ebanque.Mappers.BankAccountMapperImpl;
import com.tyannick.ebanque.Repository.AccountOperationRepository;
import com.tyannick.ebanque.Repository.BankAccountRepository;
import com.tyannick.ebanque.Repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
//@Log4j
public class BankAccountServiceImp implements BankAccountService {
    private BankAccountRepository bankAccountRepository;
    private CustomerRepository customerRepository;
    private AccountOperationRepository accountOperationRepository;
    Logger log= LoggerFactory.getLogger(this.getClass().getName());
    private BankAccountMapperImpl dtoMapper;
    /*
    contructeur pour les Ioc
     */
    public BankAccountServiceImp(BankAccountRepository bankAccountRepository,
                                 CustomerRepository customerRepository,
                                 AccountOperationRepository accountOperationRepository, BankAccountMapperImpl dtoMapper) {
        this.bankAccountRepository = bankAccountRepository;
        this.customerRepository = customerRepository;
        this.accountOperationRepository = accountOperationRepository;
        this.dtoMapper = dtoMapper;
    }


    @Override
    public CustomerDto saveCustomer(CustomerDto customerDto) {
        log.info("Saving new Customer");
        Customer customer=dtoMapper.fromCustomerDto(customerDto);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }


    @Override
    public CurrentBankAccountDto saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
        Customer customer=customerRepository.findById(customerId).orElse(null);
        if (customer==null)
            throw new CustomerNotFoundException("Customer not found");
        CurrentAccount currentAccount=new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setBalance(initialBalance);
        currentAccount.setDateCreated(new Date());
        currentAccount.setCustomer(customer);
        currentAccount.setOverdraft(overDraft);
        currentAccount.setStatus(AccountStatus.CREATED);
        CurrentAccount saveCurrentAccount = bankAccountRepository.save(currentAccount);
        return dtoMapper.fromCurrentBankAccount(saveCurrentAccount);
    }

    @Override
    public SavingBankAccountDto saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException {
        Customer customer =customerRepository.findById(customerId).orElse(null);
        if(customer==null)
            throw new CustomerNotFoundException("Customer not found Exception");
        SavingAccount savingAccount=new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setBalance(initialBalance);
        savingAccount.setInterestingRate(interestRate);
        savingAccount.setDateCreated(new Date());
        savingAccount.setStatus(AccountStatus.CREATED);
        savingAccount.setCustomer(customer);
        SavingAccount savedBankAccount=bankAccountRepository.save(savingAccount);
        return dtoMapper.fromSavingBankAccount(savedBankAccount);

    }

    @Override
    public List<CustomerDto> listCustomer() {
        List<Customer> customers= customerRepository.findAll();
        /*
        map pour transformer une objet en une autre objet.
        collect pour tranformer tout ca en une liste
         */
        List<CustomerDto> costomerDtos = customers.stream()
                .map(cust -> dtoMapper.fromCustomer(cust))
                 .collect(Collectors.toList());
//        List<CustomerDto> customerDtos=new ArrayList<>();
//        for(Customer customer:customers){
//            CustomerDto customerDto=dtoMapper.fromCustomer(customer);
//            customerDtos.add(customerDto);
//        }
        return costomerDtos;
    }

    @Override
    public BankAccountDto getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount=bankAccountRepository.findById(accountId)
                .orElseThrow(()->new BankAccountNotFoundException("Bank Account Not Found Exception"));
        if(bankAccount instanceof SavingAccount){
            SavingAccount savingAccount=(SavingAccount) bankAccount;
            return dtoMapper.fromSavingBankAccount(savingAccount);
        }else {
            CurrentAccount currentAccount= (CurrentAccount) bankAccount;
            return dtoMapper.fromCurrentBankAccount(currentAccount);
        }
    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount bankAccount=bankAccountRepository.findById(accountId)
                .orElseThrow(()->new BankAccountNotFoundException("Bank Account Not Found Exception"));
        if(bankAccount.getBalance()<amount)
            throw new BalanceNotSufficientException("Balance Not Sufficient");
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setDateOperation(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance()-amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {
        BankAccount bankAccount=bankAccountRepository.findById(accountId)
                .orElseThrow(()->new BankAccountNotFoundException("Bank Account Not Found Exception"));
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setDateOperation(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance()+amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException {
        debit(accountIdSource,amount,"Tranfer to"+accountIdDestination);
        credit(accountIdDestination,amount,"Transfer from"+accountIdSource);
    }

    @Override
    public List<BankAccountDto> bankAccountsList(){
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        List<BankAccountDto> bankAccountDtos = bankAccounts.stream().map(bankAccount -> {
            if (bankAccount instanceof SavingAccount) {
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return dtoMapper.fromSavingBankAccount(savingAccount);
            } else {
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return dtoMapper.fromCurrentBankAccount(currentAccount);
            }
        }).collect(Collectors.toList());

        return bankAccountDtos;
    }
    @Override
    public CustomerDto getCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(()->new CustomerNotFoundException("Customer Not Found"));
        return dtoMapper.fromCustomer(customer);
    }

    @Override
    public CustomerDto UpdateCustomer(CustomerDto customerDto) {
        log.info("Saving new Customer");
        Customer customer=dtoMapper.fromCustomerDto(customerDto);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public void deleteCustomer(Long customerId){
        customerRepository.deleteById(customerId);
    }

    @Override
    public List<AccountOperationDto> accountHistory(String accountId){
        List<AccountOperation> accountOperations =  accountOperationRepository.findByBankAccountId(accountId);
       List<AccountOperationDto> accountOperationDtos= accountOperations.stream().map(history->dtoMapper.fromAccountOperation(history)).collect(Collectors.toList());
        return accountOperationDtos;
    }

    @Override
    public AccountHistoryDto getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
        BankAccount bankAccount=bankAccountRepository.findById(accountId).orElse(null);
        if(bankAccount==null)
            throw new BankAccountNotFoundException("Bank Account Not Foud");
        Page<AccountOperation> accountOperationPage = accountOperationRepository.findByBankAccountId(accountId, PageRequest.of(page, size));
        AccountHistoryDto accountHistoryDto=new AccountHistoryDto();

        List<AccountOperationDto> accountOperationDtos = accountOperationPage.getContent().stream().map(op->dtoMapper.fromAccountOperation(op)).collect(Collectors.toList());

        accountHistoryDto.setAccountOperationDto(accountOperationDtos);
        accountHistoryDto.setAccountId(bankAccount.getId());
        accountHistoryDto.setBalance(bankAccount.getBalance());
        accountHistoryDto.setPageSize(size);
        accountHistoryDto.setCurrentPage(page);
        accountHistoryDto.setTotalPages(accountOperationPage.getTotalPages());
        return accountHistoryDto;
    }


    @Override
    public List<CustomerDto> searchCustomers(String keyword) {
        List<Customer> customer=customerRepository.searchCustomer(keyword);
        List<CustomerDto> searchCust = customer.stream().map(cust->dtoMapper.fromCustomer(cust)).collect(Collectors.toList());
        return searchCust;
    }

}
