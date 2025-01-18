package com.example.demo.repository;

import com.example.demo.model.Transaction;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> getTransactionsByRecipentEmail(String recipentEmail);
}