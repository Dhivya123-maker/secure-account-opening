package com.securebank.customer_service.controller;

import com.securebank.customer_service.dto.request.CreateCustomerRequest;
import com.securebank.customer_service.dto.response.ApiResponse;
import com.securebank.customer_service.dto.response.CustomerResponse;
import com.securebank.customer_service.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request) {
        CustomerResponse response = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Customer created successfully", response));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerById(
            @PathVariable Long customerId) {
        CustomerResponse response = customerService.getCustomerById(customerId);
        return ResponseEntity.ok(ApiResponse.success("Customer fetched successfully", response));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerByUserId(
            @PathVariable Long userId) {
        CustomerResponse response = customerService.getCustomerByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Customer fetched successfully", response));
    }

    @GetMapping("/number/{customerNumber}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerByNumber(
            @PathVariable String customerNumber) {
        CustomerResponse response = customerService.getCustomerByNumber(customerNumber);
        return ResponseEntity.ok(ApiResponse.success("Customer fetched successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getAllCustomers() {
        List<CustomerResponse> response = customerService.getAllCustomers();
        return ResponseEntity.ok(ApiResponse.success("Customers fetched successfully", response));
    }

    @PatchMapping("/{customerId}/kyc-status")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateKycStatus(
            @PathVariable Long customerId,
            @RequestParam String status) {
        CustomerResponse response = customerService.updateKycStatus(customerId, status);
        return ResponseEntity.ok(ApiResponse.success("KYC status updated successfully", response));
    }
    @PutMapping("/{customerId}")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomer(
            @PathVariable Long customerId,
            @Valid @RequestBody CreateCustomerRequest request) {
        CustomerResponse response = customerService.updateCustomer(customerId, request);
        return ResponseEntity.ok(ApiResponse.success("Customer updated successfully", response));
    }
}