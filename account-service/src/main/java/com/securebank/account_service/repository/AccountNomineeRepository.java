package com.securebank.account_service.repository;

import com.securebank.account_service.entity.AccountNominee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountNomineeRepository extends JpaRepository<AccountNominee, Long> {

    List<AccountNominee> findByAccount_AccountId(Long accountId);
}