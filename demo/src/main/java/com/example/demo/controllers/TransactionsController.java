package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Transaction;
import com.example.demo.model.User;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;

import java.util.Optional;
import jakarta.validation.Valid;

class RecipentApprovement {
    private String senderEmail;
    private String recipentEmail;
    private String recipentPassword;
    private boolean accepted;

    //Getters
    public boolean getAccepted() {
        return accepted;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public String getRecipentEmail() {
        return recipentEmail;
    }

    public String getRecipentPassword() {
        return recipentPassword;
    }

    // Setters
    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public void setRecipentEmail(String recipentEmail) {
        this.recipentEmail = recipentEmail;
    }

    public void setRecipentPassword(String recipentPassword) {
        this.recipentPassword = recipentPassword;
    }
}

class SendTransactionReq {
    private Transaction transaction;
    private String senderPassword;

    public SendTransactionReq() {}

    public SendTransactionReq(Transaction transaction, String senderPassword) {
        this.transaction = transaction;
        this.senderPassword = senderPassword;
    }

    // Setters
    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public void setSenderPassword(String senderPassword) {
        this.senderPassword = senderPassword;
    }

    // Getter
    public Transaction getTransaction() {
        return transaction;
    }

    public String getSenderPassword() {
        return senderPassword;
    }
}

@RestController
@RequestMapping("/transactions")
public class TransactionsController {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/sendTransaction")
    public ResponseEntity<Transaction> sendTransactionRequest(@Valid @RequestBody SendTransactionReq sendTransactionReq) {
        try {
            String senderEmail = sendTransactionReq.getTransaction().getSenderEmail();
            String recipentEmail = sendTransactionReq.getTransaction().getRecipentEmail();
            User sender = userRepository.findByEmail(senderEmail);
            User recipent = userRepository.findByEmail(recipentEmail);

            if(sender != null && recipent != null && passwordEncoder.matches(sendTransactionReq.getSenderPassword(), sender.getPassword())) {
                Transaction savedTransaction = transactionRepository.save(sendTransactionReq.getTransaction());
                return ResponseEntity.status(HttpStatus.OK).body(savedTransaction);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch(Exception err) {
            System.err.println("Error creating transaction request: " + err.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/confirmTransactionTerms/{transactionId}")
    public ResponseEntity<Transaction> confirmTransactionTerms(@PathVariable String transactionId,
                                                                @Valid @RequestBody RecipentApprovement recipentApprovement) {
        try {
            Optional<Transaction> transactionOptional = transactionRepository.findById(transactionId);

            if(!transactionOptional.isPresent()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            Transaction transaction = transactionOptional.get();

            User user = userRepository.findByEmail(recipentApprovement.getSenderEmail());

            if(user != null && passwordEncoder.matches(recipentApprovement.getRecipentPassword(), user.getPassword())) {
                transaction.setRecipentApproved(recipentApprovement.getAccepted());
                transaction.setStatus(recipentApprovement.getAccepted() ? "Recipent Accepted" : "Recipent Rejected");

                Transaction updatedTransaction = transactionRepository.save(transaction);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedTransaction);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

        } catch(Exception err) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
