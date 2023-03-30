package com.tyannick.ebanque.Repository;

import com.tyannick.ebanque.Entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount,String> {
}
