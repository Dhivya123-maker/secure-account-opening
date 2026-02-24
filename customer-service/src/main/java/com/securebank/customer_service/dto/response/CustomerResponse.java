package com.securebank.customer_service.dto.response;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponse {

    private Long customerId;
    private Long userId;
    private String customerNumber;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String nationality;
    private String panNumber;
    private String aadharNumber;
    private String kycStatus;
    private String customerStatus;
    private String email;
    private String phone;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String pincode;
    private String country;
    private String employmentType;
    private String employerName;
    private String designation;
    private java.math.BigDecimal annualIncome;
    private LocalDateTime createdAt;
}