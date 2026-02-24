package com.securebank.customer_service.repository;

import com.securebank.customer_service.entity.CustomerEmployment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerEmploymentRepository extends JpaRepository<CustomerEmployment, Long> {

    Optional<CustomerEmployment> findByCustomer_CustomerId(Long customerId);
}