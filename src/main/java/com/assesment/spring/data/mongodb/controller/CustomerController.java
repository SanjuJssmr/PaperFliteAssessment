package com.assesment.spring.data.mongodb.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.assesment.spring.data.mongodb.model.CustomerEntity;
import com.assesment.spring.data.mongodb.service.CustomerService;

@RestController
@RequestMapping("/customer")
@CrossOrigin(origins = "http://localhost:8080")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    @PostMapping("/insert")
    public ResponseEntity<CustomerEntity> insertCustomer(@RequestBody CustomerEntity customerData) {
        try {
            logger.info("Starting customer creation for customer Id: {}", customerData.getId());
            CustomerEntity createdData = customerService.createCustomer(customerData);
            if (createdData != null) {
                logger.info("Customer creation successfull for Id: {}", customerData.getId());

                return new ResponseEntity<>(createdData, HttpStatus.CREATED);
            }
            logger.warn("Something went wrong while creating customer Id {}", customerData.getId());

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Internal Server Error in Customer Insert Controller", e.getMessage(), e);

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<CustomerEntity> updateCustomer(@RequestBody CustomerEntity customerData) {
        try {
            logger.info("Starting update for customer Id: {}", customerData.getId());

            CustomerEntity updatedData = customerService.updateCustomer(customerData);
            if (updatedData != null) {
                logger.info("Customer updation successfull for Id: {}", customerData.getId());

                return new ResponseEntity<>(updatedData, HttpStatus.ACCEPTED);
            }
            logger.warn("Something went wrong while updating customer Id {}", customerData.getId());

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Internal Server Error in Customer Update Controller : {}", e.getMessage(), e);

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<CustomerEntity>> getCustomersByAccountId(
            @PathVariable String accountId,
            @RequestParam int page,
            @RequestParam int limit) {
        try {
            Page<CustomerEntity> customers = customerService.getAccountsByCustomerId(accountId, page, limit);
            if (customers.hasContent()) {
                logger.info("Fetched {} customers for accountId: {}", customers.getTotalElements(), accountId);
                List<CustomerEntity> fetchedData = customers.getContent();

                return new ResponseEntity<>(fetchedData, HttpStatus.OK);
            } else {
                logger.warn("No customers found for accountId: {}", accountId);

                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Internal Server Error in Get Customer Accounts Controller : {}", e.getMessage(), e);

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/migration")
    public ResponseEntity<String> deleteMigratedCustomer() {
        try {
            customerService.deleteCustomersWithoutAccounts();
            logger.info("Customer migration completed successfully.");

            return ResponseEntity.ok("Customer migration completed");
        } catch (Exception e) {
            logger.error("Internal Server Error in Customer migration Controller : {}", e.getMessage(), e);

            return new ResponseEntity<>("Customer migration failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
