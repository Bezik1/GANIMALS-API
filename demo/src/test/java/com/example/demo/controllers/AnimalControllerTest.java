package com.example.demo.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

import java.util.Arrays;
import java.util.Optional;

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
import com.example.demo.repository.AnimalRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.types.Animals.ChangeOwnerReq;
import com.example.demo.types.Response.Response;

public class AnimalControllerTest {
    static final int WILD_ANIMAL_GENCODE_LENGTH = 44;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AnimalsController animalsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Wild Animal Generation Test")
    void testWildAnimalGeneration() {
        String wildAnimalGenCode = Animal.generateWildAnimalGeneticCode();
        Animal wildAnimal = new Animal("", "puszek", wildAnimalGenCode);

        when(animalRepository.save(any(Animal.class))).thenReturn(wildAnimal);

        Response response = animalsController.createWildAnimal();

        assertNotNull(response.getBody());
        assertEquals(CREATED, response.getStatus());
        assertEquals(wildAnimalGenCode.length(), WILD_ANIMAL_GENCODE_LENGTH);
        assertEquals(WILD_ANIMAL_GENCODE_LENGTH, ((Animal) response.getBody()).getGenome().length());
        assertEquals(wildAnimal, response.getBody());
    }

    @Test
    @DisplayName("Getting User Animals Test")
    void testUserAnimalsGetting() {
        String userId = "user123";
        Animal animal1 = new Animal(userId, "Burek", "gen123");
        Animal animal2 = new Animal(userId, "Reksio", "gen456");

        when(animalRepository.findByOwner(userId)).thenReturn(Arrays.asList(animal1, animal2));

        Response response = animalsController.getUserAnimals(userId);

        assertNotNull(response.getBody());
        assertEquals(OK, response.getStatus());
    }

    @Test
    @DisplayName("Change Owner Test")
    void testChangeOwner() {
        String transactionId = "txn123";
        String newOwnerEmail = "newowner@example.com";
        String newOwnerPassword = "password";
        String animalId = "animal123";
        String oldOwnerId = "oldOwnerId";

        Transaction transaction = new Transaction();
        transaction.setAnimalId(animalId);
        transaction.setSenderEmail(newOwnerEmail);
        transaction.setRecipentApproved(true);

        User newOwner = new User();
        newOwner.setEmail(newOwnerEmail);
        newOwner.setPassword("encodedPassword");
        newOwner.setId("newOwnerId");

        Animal animal = new Animal(oldOwnerId, "Burek", "gen789");

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(userRepository.findByEmail(newOwnerEmail)).thenReturn(newOwner);
        when(passwordEncoder.matches(newOwnerPassword, "encodedPassword")).thenReturn(true);
        when(animalRepository.findById(animalId)).thenReturn(Optional.of(animal));
        when(animalRepository.save(any(Animal.class))).thenReturn(animal);

        ChangeOwnerReq changeOwnerReq = new ChangeOwnerReq();
        changeOwnerReq.setTransactionId(transactionId);
        changeOwnerReq.setNewOwnerEmail(newOwnerEmail);
        changeOwnerReq.setNewOwnerPassword(newOwnerPassword);

        Response response = animalsController.changeOwner(changeOwnerReq);

        assertNotNull(response.getBody());
        assertEquals(OK, response.getStatus());
        assertEquals(newOwner.getId(), ((Animal) response.getBody()).getOwner());
    }
}