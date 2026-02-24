package com.securebank.account_service.service;

import com.securebank.account_service.dto.event.AccountOpenedEvent;
import com.securebank.account_service.dto.request.CreateAccountRequest;
import com.securebank.account_service.dto.request.TransactionRequest;
import com.securebank.account_service.dto.request.TransferRequest;
import com.securebank.account_service.dto.response.AccountResponse;
import com.securebank.account_service.dto.response.TransactionResponse;
import com.securebank.account_service.entity.Account;
import com.securebank.account_service.entity.AccountNominee;
import com.securebank.account_service.entity.AccountTransaction;
import com.securebank.account_service.repository.AccountNomineeRepository;
import com.securebank.account_service.repository.AccountRepository;
import com.securebank.account_service.repository.AccountTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountTransactionRepository transactionRepository;
    private final AccountNomineeRepository nomineeRepository;
    private final KafkaProducerService kafkaProducerService;

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {
        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .customerId(request.getCustomerId())
                .accountType(request.getAccountType())
                .accountStatus("ACTIVE")
                .balance(BigDecimal.ZERO)
                .currency("INR")
                .branchCode(request.getBranchCode())
                .ifscCode(request.getIfscCode())
                .openingDate(request.getOpeningDate() != null ?
                        request.getOpeningDate() : LocalDate.now())
                .interestRate(getInterestRate(request.getAccountType()))
                .overdraftLimit(BigDecimal.ZERO)
                .createdBy("SYSTEM")
                .build();

        account = accountRepository.save(account);

        if (request.getNomineeName() != null) {
            AccountNominee nominee = AccountNominee.builder()
                    .account(account)
                    .nomineeName(request.getNomineeName())
                    .relationship(request.getNomineeRelationship())
                    .dateOfBirth(request.getNomineeDateOfBirth())
                    .sharePercentage(new BigDecimal("100"))
                    .build();
            nomineeRepository.save(nominee);
        }

        kafkaProducerService.publishAccountOpenedEvent(
                AccountOpenedEvent.builder()
                        .customerId(request.getCustomerId())
                        .accountNumber(account.getAccountNumber())
                        .accountType(request.getAccountType())
                        .email(request.getEmail())
                        .firstName(request.getFirstName())
                        .build()
        );

        return mapToResponse(account);
    }

    public AccountResponse getAccountById(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return mapToResponse(account);
    }

    public AccountResponse getAccountByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return mapToResponse(account);
    }

    public List<AccountResponse> getAccountsByCustomerId(Long customerId) {
        return accountRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public TransactionResponse processTransaction(TransactionRequest request) {
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!"ACTIVE".equals(account.getAccountStatus())) {
            throw new RuntimeException("Account is not active");
        }

        BigDecimal balanceBefore = account.getBalance();
        BigDecimal balanceAfter;

        switch (request.getTransactionType().toUpperCase()) {
            case "CREDIT" -> balanceAfter = balanceBefore.add(request.getAmount());
            case "DEBIT" -> {
                if (balanceBefore.compareTo(request.getAmount()) < 0) {
                    throw new RuntimeException("Insufficient balance");
                }
                balanceAfter = balanceBefore.subtract(request.getAmount());
            }
            default -> throw new RuntimeException("Invalid transaction type");
        }

        account.setBalance(balanceAfter);
        accountRepository.save(account);

        AccountTransaction transaction = AccountTransaction.builder()
                .transactionRef(UUID.randomUUID().toString())
                .account(account)
                .transactionType(request.getTransactionType().toUpperCase())
                .amount(request.getAmount())
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .description(request.getDescription())
                .status("SUCCESS")
                .build();

        transaction = transactionRepository.save(transaction);
        return mapToTransactionResponse(transaction, account.getAccountNumber());
    }

    public List<TransactionResponse> getTransactionHistory(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return transactionRepository
                .findByAccount_AccountIdOrderByTransactionDateDesc(account.getAccountId())
                .stream()
                .map(t -> mapToTransactionResponse(t, accountNumber))
                .toList();
    }

    @Transactional
    public AccountResponse updateAccountStatus(Long accountId, String status) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setAccountStatus(status);
        account.setUpdatedBy("SYSTEM");
        accountRepository.save(account);
        return mapToResponse(account);
    }

    private AccountResponse mapToResponse(Account account) {
        return AccountResponse.builder()
                .accountId(account.getAccountId())
                .accountNumber(account.getAccountNumber())
                .customerId(account.getCustomerId())
                .accountType(account.getAccountType())
                .accountStatus(account.getAccountStatus())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .branchCode(account.getBranchCode())
                .ifscCode(account.getIfscCode())
                .openingDate(account.getOpeningDate())
                .interestRate(account.getInterestRate())
                .overdraftLimit(account.getOverdraftLimit())
                .createdAt(account.getCreatedAt())
                .build();
    }

    private TransactionResponse mapToTransactionResponse(AccountTransaction t,
                                                         String accountNumber) {
        return TransactionResponse.builder()
                .transactionId(t.getTransactionId())
                .transactionRef(t.getTransactionRef())
                .accountNumber(accountNumber)
                .transactionType(t.getTransactionType())
                .amount(t.getAmount())
                .balanceBefore(t.getBalanceBefore())
                .balanceAfter(t.getBalanceAfter())
                .description(t.getDescription())
                .status(t.getStatus())
                .transactionDate(t.getTransactionDate())
                .build();
    }

    private String generateAccountNumber() {
        return "SB" + System.currentTimeMillis();
    }

    private BigDecimal getInterestRate(String accountType) {
        return switch (accountType.toUpperCase()) {
            case "SAVINGS" -> new BigDecimal("4.00");
            case "CURRENT" -> BigDecimal.ZERO;
            case "FIXED_DEPOSIT" -> new BigDecimal("7.50");
            case "RECURRING_DEPOSIT" -> new BigDecimal("6.50");
            default -> BigDecimal.ZERO;
        };
    }

    @Transactional
    public TransactionResponse transfer(TransferRequest request) {
        Account fromAccount = accountRepository.findByAccountNumber(request.getFromAccountNumber())
                .orElseThrow(() -> new RuntimeException("Source account not found"));

        Account toAccount = accountRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new RuntimeException("Destination account not found"));

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // Debit from source
        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        accountRepository.save(fromAccount);

        // Credit to destination
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));
        accountRepository.save(toAccount);

        // Save debit transaction
        AccountTransaction debitTxn = AccountTransaction.builder()
                .account(fromAccount)
                .transactionRef("TXN" + System.currentTimeMillis())
                .transactionType("DEBIT")
                .amount(request.getAmount())
                .balanceBefore(fromAccount.getBalance().add(request.getAmount()))
                .balanceAfter(fromAccount.getBalance())
                .description("Transfer to " + request.getToAccountNumber()
                        + (request.getDescription() != null ? " - " + request.getDescription() : ""))
                .status("SUCCESS")
                .transactionDate(LocalDateTime.now())
                .build();
        transactionRepository.save(debitTxn);

        // Save credit transaction
        AccountTransaction creditTxn = AccountTransaction.builder()
                .account(toAccount)
                .transactionRef("TXN" + System.currentTimeMillis() + 1)
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .balanceBefore(toAccount.getBalance().subtract(request.getAmount()))
                .balanceAfter(toAccount.getBalance())
                .description("Transfer from " + request.getFromAccountNumber()
                        + (request.getDescription() != null ? " - " + request.getDescription() : ""))
                .status("SUCCESS")
                .transactionDate(LocalDateTime.now())
                .build();
        transactionRepository.save(creditTxn);

        return mapToTransactionResponse(debitTxn, request.getFromAccountNumber());
    }

    public List<TransactionResponse> getTransactionsByDateRange(
            String accountNumber, String fromDate, String toDate) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        LocalDateTime from = LocalDate.parse(fromDate).atStartOfDay();
        LocalDateTime to = LocalDate.parse(toDate).atTime(23, 59, 59);

        return transactionRepository
                .findByAccountAndTransactionDateBetweenOrderByTransactionDateDesc(
                        account, from, to)
                .stream()
                .map(t -> mapToTransactionResponse(t, accountNumber))
                .collect(Collectors.toList());
    }
}