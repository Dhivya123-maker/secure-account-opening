package com.securebank.customer_service.repository;

import com.securebank.customer_service.entity.CustomerContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerContactRepository extends JpaRepository<CustomerContact, Long> {

    Optional<CustomerContact> findByCustomer_CustomerId(Long customerId);

    boolean existsByEmail(String email);
}