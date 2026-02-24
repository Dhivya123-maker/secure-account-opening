package com.securebank.account_service.repository;

import com.securebank.account_service.entity.Account;
import com.securebank.account_service.entity.AccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long> {

    List<AccountTransaction> findByAccount_AccountIdOrderByTransactionDateDesc(Long accountId);

    Optional<AccountTransaction> findByTransactionRef(String transactionRef);
    List<AccountTransaction> findByAccountAndTransactionDateBetweenOrderByTransactionDateDesc(
            Account account, LocalDateTime from, LocalDateTime to);
}