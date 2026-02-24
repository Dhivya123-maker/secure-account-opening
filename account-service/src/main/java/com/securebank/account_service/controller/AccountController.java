package com.securebank.account_service.controller;

import com.securebank.account_service.dto.request.CreateAccountRequest;
import com.securebank.account_service.dto.request.TransactionRequest;
import com.securebank.account_service.dto.request.TransferRequest;
import com.securebank.account_service.dto.response.AccountResponse;
import com.securebank.account_service.dto.response.ApiResponse;
import com.securebank.account_service.dto.response.TransactionResponse;
import com.securebank.account_service.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(
            @Valid @RequestBody CreateAccountRequest request) {
        AccountResponse response = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Account created successfully", response));
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccountById(
            @PathVariable Long accountId) {
        AccountResponse response = accountService.getAccountById(accountId);
        return ResponseEntity.ok(ApiResponse.success("Account fetched successfully", response));
    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccountByNumber(
            @PathVariable String accountNumber) {
        AccountResponse response = accountService.getAccountByNumber(accountNumber);
        return ResponseEntity.ok(ApiResponse.success("Account fetched successfully", response));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getAccountsByCustomerId(
            @PathVariable Long customerId) {
        List<AccountResponse> response = accountService.getAccountsByCustomerId(customerId);
        return ResponseEntity.ok(ApiResponse.success("Accounts fetched successfully", response));
    }

    @PostMapping("/transaction")
    public ResponseEntity<ApiResponse<TransactionResponse>> processTransaction(
            @Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = accountService.processTransaction(request);
        return ResponseEntity.ok(ApiResponse.success("Transaction processed successfully", response));
    }

    @GetMapping("/{accountNumber}/transactions")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionHistory(
            @PathVariable String accountNumber) {
        List<TransactionResponse> response = accountService.getTransactionHistory(accountNumber);
        return ResponseEntity.ok(ApiResponse.success("Transactions fetched successfully", response));
    }

    @PatchMapping("/{accountId}/status")
    public ResponseEntity<ApiResponse<AccountResponse>> updateAccountStatus(
            @PathVariable Long accountId,
            @RequestParam String status) {
        AccountResponse response = accountService.updateAccountStatus(accountId, status);
        return ResponseEntity.ok(ApiResponse.success("Account status updated successfully", response));
    }
    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(
            @Valid @RequestBody TransferRequest request) {
        TransactionResponse response = accountService.transfer(request);
        return ResponseEntity.ok(ApiResponse.success("Transfer successful", response));
    }

    @GetMapping("/{accountNumber}/transactions/filter")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionsByDateRange(
            @PathVariable String accountNumber,
            @RequestParam String fromDate,
            @RequestParam String toDate) {
        List<TransactionResponse> response = accountService
                .getTransactionsByDateRange(accountNumber, fromDate, toDate);
        return ResponseEntity.ok(ApiResponse.success("Transactions fetched successfully", response));
    }
}