package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
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
import com.example.demo.types.Response.ErrorResponse;
import com.example.demo.types.Response.Response;
import com.example.demo.types.Response.SuccessResponse;
import com.example.demo.types.Transactions.RecipentApprovementReq;
import com.example.demo.types.Transactions.SendTransactionReq;

import java.util.List;
import java.util.Optional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/transactions")
public class TransactionsController {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/getTransactions/{recipentEmail}")
    public Response getUserTransactions(@PathVariable String recipentEmail) {
        try {
            User user = userRepository.findByEmail(recipentEmail);
            if(user == null) return ErrorResponse.httpStatus(HttpStatus.NOT_FOUND);

            List<Transaction> userTransactions = transactionRepository.getTransactionsByRecipentEmail(recipentEmail);
            return SuccessResponse.httpStatus(HttpStatus.OK).build(userTransactions);
        } catch(Exception e) {
            return ErrorResponse.httpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/sendTransaction")
    public Response sendTransactionRequest(@Valid @RequestBody SendTransactionReq sendTransactionReq) {
        try {
            String senderEmail = sendTransactionReq.getTransaction().getSenderEmail();
            String recipentEmail = sendTransactionReq.getTransaction().getRecipentEmail();
            User sender = userRepository.findByEmail(senderEmail);
            User recipent = userRepository.findByEmail(recipentEmail);

            if(sender != null && recipent != null && passwordEncoder.matches(sendTransactionReq.getSenderPassword(), sender.getPassword())) {
                Transaction savedTransaction = transactionRepository.save(sendTransactionReq.getTransaction());
                return SuccessResponse.httpStatus(HttpStatus.OK).build(savedTransaction);
            } else {
                return ErrorResponse.httpStatus(HttpStatus.UNAUTHORIZED);
            }
        } catch(Exception err) {
            System.err.println("Error creating transaction request: " + err.getMessage());
            return ErrorResponse.httpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/confirmTransactionTerms/{transactionId}")
    public Response confirmTransactionTerms(@PathVariable String transactionId,
                                                                @Valid @RequestBody RecipentApprovementReq recipentApprovement) {
        try {
            Optional<Transaction> transactionOptional = transactionRepository.findById(transactionId);

            if(!transactionOptional.isPresent()) return ErrorResponse.httpStatus(HttpStatus.NOT_FOUND);
            Transaction transaction = transactionOptional.get();

            User user = userRepository.findByEmail(recipentApprovement.getRecipentEmail());
            User senderUser = userRepository.findByEmail(recipentApprovement.getSenderEmail());

            if(user != null && passwordEncoder.matches(recipentApprovement.getRecipentPassword(), user.getPassword())) {
                transaction.setRecipentApproved(recipentApprovement.getAccepted());
                transaction.setStatus(recipentApprovement.getAccepted() ? "Recipent Accepted" : "Recipent Rejected");

                user.setSaldo(user.getSaldo()-transaction.getAmount());
                senderUser.setSaldo(senderUser.getSaldo()+transaction.getAmount());

                userRepository.save(user);
                userRepository.save(senderUser);

                Transaction updatedTransaction = transactionRepository.save(transaction);
                return SuccessResponse.httpStatus(HttpStatus.ACCEPTED).build(updatedTransaction);
            } else {
                return ErrorResponse.httpStatus(HttpStatus.UNAUTHORIZED);
            }

        } catch(Exception err) {
            return ErrorResponse.httpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
