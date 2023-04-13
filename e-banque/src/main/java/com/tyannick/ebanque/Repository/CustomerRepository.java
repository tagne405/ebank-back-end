package com.tyannick.ebanque.Repository;

import com.tyannick.ebanque.Entities.Customer;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends JpaRepository<Customer,Long> {

    @Query("select c from Customer c where c.name like :kw")
    List<Customer> searchCustomer(@Param(value ="kw") String keyword);

    // List<Customer> findByNameContains(String keyword);
}
