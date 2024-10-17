package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Transaction;
import com.example.demo.repository.TransactionRepository;

import java.util.Optional;
import jakarta.validation.Valid;

// class RecipentApprovement {
//     re
// }

@RestController
@RequestMapping("/transactions")
public class TransactionsController {
    @Autowired
    private TransactionRepository transactionRepository;

    @PostMapping("/sendTransaction")
    public ResponseEntity<Transaction> sendTransactionRequest(@Valid @RequestBody Transaction transaction) {
        try {
            Transaction savedTransaction = transactionRepository.save(transaction);
            return ResponseEntity.status(HttpStatus.OK).body(savedTransaction);
        } catch(Exception err) {
            System.err.println("Error creating transaction request: " + err.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // @PostMapping("/confirmTransactionTerms/{transactionId}")
    // public ResponseEntity<Transaction> confirmTransactionTerms(@PathVariable String transactionId,
    //                                                             @Valid @RequestBody ) {
    //     try {
    //         Optional<Transaction> transactionOptional = transactionRepository.findById(transactionId);
    //     } catch(Exception err) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    //     }
    // }
}
