package com.tyannick.ebanque.Repository;

import com.tyannick.ebanque.Entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
}
