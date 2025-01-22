package com.example.demo.repository;

import com.example.demo.model.Transaction;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    /**
     * Gets transactions by the recipent email
     * 
     * @param recipentEmail
     * @return Transaction[]
     */
    List<Transaction> getTransactionsByRecipentEmail(String recipentEmail);
}