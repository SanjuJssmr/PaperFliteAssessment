package com.assesment.spring.data.mongodb.repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.assesment.spring.data.mongodb.model.CustomerEntity;

public interface CustomerRepository extends MongoRepository<CustomerEntity, String> {
     Page<CustomerEntity> findByAccountId(String accountId, Pageable pageable);
}
