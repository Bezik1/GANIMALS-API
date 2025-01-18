package com.example.demo.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.model.Animal;
import com.example.demo.model.Transaction;
import com.example.demo.model.User;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.types.Response.Response;
import com.example.demo.types.Transactions.SendTransactionReq;

public class TransactionsControllerTest {
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TransactionsController transactionsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Get Transaction Succesfully Test")
    void testGettingTransactionSuccesfully() {
        String recipentEmail = "test@test.com";

        User user = new User("user1", "user1@gmail.com", "123");

        Transaction transaction1 = new Transaction("user1", recipentEmail, "gen123", 50);
        Transaction transaction2 = new Transaction("user2", recipentEmail, "gen456", 50);

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

        when(userRepository.findByEmail(recipentEmail)).thenReturn(user);
        when(transactionRepository.getTransactionsByRecipentEmail(recipentEmail)).thenReturn(transactions);

        Response response = transactionsController.getUserTransactions(recipentEmail);

        assertEquals(OK, response.getStatus());
        assertNotNull(response.getBody());
        assertEquals(transactions, response.getBody());
    }

    @Test
    @DisplayName("Transaction Creation Test")
    void testCreatingTransaction() {
        User user1 = new User(
            "user1",
            "user1@example.com",
            "123"
        );

        User user2 = new User(
            "user2",
            "user2@example.com",
            "456"
        );

        Animal animal1 = new Animal(
            user1.getId(),
            "animal1",
            "gen123"
        );

        Transaction transaction = new Transaction(
            user1.getEmail(),
            user2.getEmail(),
            animal1.getId(),
            50
        );

        SendTransactionReq sendTransactionReq = new SendTransactionReq(
            transaction,
            "123"
        );

    }
}
