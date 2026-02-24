package com.securebank.customer_service.repository;

import com.securebank.customer_service.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByUserId(Long userId);

    Optional<Customer> findByCustomerNumber(String customerNumber);

    Optional<Customer> findByPanNumber(String panNumber);

    Optional<Customer> findByAadharNumber(String aadharNumber);

    boolean existsByUserId(Long userId);

    boolean existsByPanNumber(String panNumber);

    boolean existsByAadharNumber(String aadharNumber);
}