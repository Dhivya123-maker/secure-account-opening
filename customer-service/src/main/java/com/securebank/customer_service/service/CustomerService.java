package com.securebank.customer_service.service;

import com.securebank.customer_service.dto.request.CreateCustomerRequest;
import com.securebank.customer_service.dto.response.CustomerResponse;
import com.securebank.customer_service.entity.*;
import com.securebank.customer_service.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerContactRepository contactRepository;
    private final CustomerAddressRepository addressRepository;
    private final CustomerEmploymentRepository employmentRepository;

    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        if (customerRepository.existsByUserId(request.getUserId())) {
            throw new RuntimeException("Customer already exists for this user");
        }
        if (request.getPanNumber() != null &&
                customerRepository.existsByPanNumber(request.getPanNumber())) {
            throw new RuntimeException("PAN number already registered");
        }
        if (request.getAadharNumber() != null &&
                customerRepository.existsByAadharNumber(request.getAadharNumber())) {
            throw new RuntimeException("Aadhar number already registered");
        }
        if (contactRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        Customer customer = Customer.builder()
                .userId(request.getUserId())
                .customerNumber(generateCustomerNumber())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .nationality(request.getNationality())
                .panNumber(request.getPanNumber())
                .aadharNumber(request.getAadharNumber())
                .kycStatus("PENDING")
                .customerStatus("ACTIVE")
                .createdBy("SYSTEM")
                .build();

        customer = customerRepository.save(customer);

        CustomerContact contact = CustomerContact.builder()
                .customer(customer)
                .email(request.getEmail())
                .phone(request.getPhone())
                .alternatePhone(request.getAlternatePhone())
                .build();
        contactRepository.save(contact);

        CustomerAddress address = CustomerAddress.builder()
                .customer(customer)
                .addressType("PERMANENT")
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .country(request.getCountry())
                .isPrimary(true)
                .build();
        addressRepository.save(address);

        CustomerEmployment employment = CustomerEmployment.builder()
                .customer(customer)
                .employmentType(request.getEmploymentType())
                .employerName(request.getEmployerName())
                .designation(request.getDesignation())
                .annualIncome(request.getAnnualIncome())
                .yearsOfExperience(request.getYearsOfExperience())
                .build();
        employmentRepository.save(employment);

        return mapToResponse(customer, contact, address, employment);
    }

    public CustomerResponse getCustomerById(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return buildResponse(customer);
    }

    public CustomerResponse getCustomerByUserId(Long userId) {
        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return buildResponse(customer);
    }

    public CustomerResponse getCustomerByNumber(String customerNumber) {
        Customer customer = customerRepository.findByCustomerNumber(customerNumber)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return buildResponse(customer);
    }

    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    @Transactional
    public CustomerResponse updateKycStatus(Long customerId, String kycStatus) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customer.setKycStatus(kycStatus);
        customer.setUpdatedBy("SYSTEM");
        customerRepository.save(customer);
        return buildResponse(customer);
    }

    private CustomerResponse buildResponse(Customer customer) {
        CustomerContact contact = contactRepository
                .findByCustomer_CustomerId(customer.getCustomerId()).orElse(null);
        CustomerAddress address = addressRepository
                .findByCustomer_CustomerId(customer.getCustomerId())
                .stream().filter(CustomerAddress::isPrimary).findFirst().orElse(null);
        CustomerEmployment employment = employmentRepository
                .findByCustomer_CustomerId(customer.getCustomerId()).orElse(null);
        return mapToResponse(customer, contact, address, employment);
    }

    private CustomerResponse mapToResponse(Customer customer, CustomerContact contact,
                                           CustomerAddress address,
                                           CustomerEmployment employment) {
        return CustomerResponse.builder()
                .customerId(customer.getCustomerId())
                .userId(customer.getUserId())
                .customerNumber(customer.getCustomerNumber())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .dateOfBirth(customer.getDateOfBirth())
                .gender(customer.getGender())
                .nationality(customer.getNationality())
                .panNumber(customer.getPanNumber())
                .aadharNumber(customer.getAadharNumber())
                .kycStatus(customer.getKycStatus())
                .customerStatus(customer.getCustomerStatus())
                .email(contact != null ? contact.getEmail() : null)
                .phone(contact != null ? contact.getPhone() : null)
                .addressLine1(address != null ? address.getAddressLine1() : null)
                .addressLine2(address != null ? address.getAddressLine2() : null)
                .city(address != null ? address.getCity() : null)
                .state(address != null ? address.getState() : null)
                .pincode(address != null ? address.getPincode() : null)
                .country(address != null ? address.getCountry() : null)
                .employmentType(employment != null ? employment.getEmploymentType() : null)
                .employerName(employment != null ? employment.getEmployerName() : null)
                .designation(employment != null ? employment.getDesignation() : null)
                .annualIncome(employment != null ? employment.getAnnualIncome() : null)
                .createdAt(customer.getCreatedAt())
                .build();
    }

    @Transactional
    public CustomerResponse updateCustomer(Long customerId, CreateCustomerRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Update basic fields
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setGender(request.getGender());
        customer.setNationality(request.getNationality());
        customer.setPanNumber(request.getPanNumber());
        customer.setAadharNumber(request.getAadharNumber());
        customer.setUpdatedBy("SYSTEM");
        customerRepository.save(customer);

        // Update contact
        CustomerContact contact = contactRepository
                .findByCustomer_CustomerId(customerId).orElse(null);
        if (contact != null) {
            contact.setEmail(request.getEmail());
            contact.setPhone(request.getPhone());
            contactRepository.save(contact);
        }

        // Update address
        CustomerAddress address = addressRepository
                .findByCustomer_CustomerId(customerId)
                .stream().filter(CustomerAddress::isPrimary).findFirst().orElse(null);
        if (address != null) {
            address.setAddressLine1(request.getAddressLine1());
            address.setAddressLine2(request.getAddressLine2());
            address.setCity(request.getCity());
            address.setState(request.getState());
            address.setPincode(request.getPincode());
            address.setCountry(request.getCountry());
            addressRepository.save(address);
        }

        // Update employment
        CustomerEmployment employment = employmentRepository
                .findByCustomer_CustomerId(customerId).orElse(null);
        if (employment != null) {
            employment.setEmploymentType(request.getEmploymentType());
            employment.setEmployerName(request.getEmployerName());
            employment.setDesignation(request.getDesignation());
            employment.setAnnualIncome(request.getAnnualIncome());
            employment.setYearsOfExperience(request.getYearsOfExperience());
            employmentRepository.save(employment);
        }

        return mapToResponse(customer, contact, address, employment);
    }

    private String generateCustomerNumber() {
        return "CUST" + System.currentTimeMillis();
    }
}