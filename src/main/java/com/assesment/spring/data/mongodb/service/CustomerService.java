package com.assesment.spring.data.mongodb.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.assesment.spring.data.mongodb.controller.CustomerController;
import com.assesment.spring.data.mongodb.model.AccountEntity;
import com.assesment.spring.data.mongodb.model.CustomerEntity;
import com.assesment.spring.data.mongodb.repository.AccountRepository;
import com.assesment.spring.data.mongodb.repository.CustomerRepository;

@Service
public class CustomerService {
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private AccountRepository accountRepo;

    public CustomerEntity createCustomer(CustomerEntity customerData) {
        try {
            Optional<AccountEntity> accountEntityOptional = accountRepo.findById(customerData.getAccountId().getId());
            if (!accountEntityOptional.isPresent()) {
                logger.warn("Account not found with Id: {}", customerData.getAccountId().getId());

                return null;
            }
            CustomerEntity savedCustomer = customerRepo.save(customerData);

            return savedCustomer;
        } catch (Exception e) {
            logger.error("Internal Server Error in Customer Insert Service : {}", e.getMessage());

            throw new RuntimeException("Internal Server Error in Customer Insert Service", e);
        }
    }

    public CustomerEntity updateCustomer(CustomerEntity customerData) {
        try {
            Optional<CustomerEntity> customerOpt = customerRepo.findById(customerData.getId());

            if (!customerOpt.isPresent()) {
                logger.warn("Customer not found with Id: {}", customerData.getId());

                return null;
            }

            CustomerEntity existingCustomer = customerOpt.get();

            if (customerData.getFirstName() != null) {
                existingCustomer.setFirstName(customerData.getFirstName());
            }
            if (customerData.getLastName() != null) {
                existingCustomer.setLastName(customerData.getLastName());
            }

            if (customerData.getAccountId() != null) {
                Optional<AccountEntity> accountOpt = accountRepo.findById(customerData.getAccountId().getId());
                if (accountOpt.isPresent()) {
                    existingCustomer.setAccountId(accountOpt.get());
                } else {
                    logger.warn("Account not found with Id: {}", customerData.getAccountId().getId());

                    return null;
                }
            }
            CustomerEntity updatedCustomer = customerRepo.save(existingCustomer);

            return updatedCustomer;
        } catch (Exception e) {
            logger.error("Internal Server Error in Customer Update Service : {}", e.getMessage());

            throw new RuntimeException("Internal Server Error in Customer Update Service", e);
        }
    }

    public Page<CustomerEntity> getAccountsByCustomerId(String accountId, int page, int limit) {
        try {
            Pageable pageable = PageRequest.of(page, limit);
            Page<CustomerEntity> customers = customerRepo.findByAccountId(accountId, pageable);

            return customers;
        } catch (Exception e) {
            logger.error("Internal Server Error in Get Customer account Service : {}", e.getMessage());

            throw new RuntimeException("Internal Server Error in Get Customer account Service", e);
        }
    }

    public void deleteCustomersWithoutAccounts() {
        try {
            int pageSize = 2;
            int pageNumber = 0;
            Page<CustomerEntity> customerPage;

            while (true) {
                Pageable pageable = PageRequest.of(pageNumber, pageSize);
                customerPage = customerRepo.findAll(pageable);
                logger.info("Processing page: {}", pageNumber);

                if (!customerPage.hasContent()) {
                    break;
                }

                for (CustomerEntity customer : customerPage.getContent()) {
                    if (customer.getAccountId() != null) {
                        continue;
                    }
                    logger.info("Customer with ID: {} has no account reference", customer.getId());
                    logger.info("Deleting customer with ID: {} as the account is not found", customer.getId());
                    customerRepo.delete(customer);
                }
                pageNumber++;
            }
        } catch (Exception e) {
            logger.error("Internal Server Error in Delete Customer Wihtout Account Service : {}", e.getMessage());

            throw new RuntimeException("Internal Server Error in Delete Customer Wihtout Account Service", e);
        }
    }
}
