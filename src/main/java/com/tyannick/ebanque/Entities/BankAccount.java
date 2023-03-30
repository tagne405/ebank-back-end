package com.tyannick.ebanque.Entities;

import com.tyannick.ebanque.Enums.AccountStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE",length = 5,discriminatorType = DiscriminatorType.STRING)
@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class BankAccount {
    @Id
    private String id;
    private Date dateCreated;
    private double balance;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @ManyToOne
    Customer customer;
    
    @OneToMany(mappedBy = "bankAccount")
    List<AccountOperation> accountOperation;

}
