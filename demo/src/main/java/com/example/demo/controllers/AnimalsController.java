package com.example.demo.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Animal;
import com.example.demo.model.Transaction;
import com.example.demo.model.User;
import com.example.demo.repository.AnimalRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;

import jakarta.validation.Valid;

class ChangeOwnerReq {
    private String transactionId;
    private String newOwnerEmail;
    private String newOwnerPassword;

    public ChangeOwnerReq() {}

    public ChangeOwnerReq(String animalId, String newOwnerEmail, String transactionId, String newOwnerPassword) {
        this.newOwnerEmail = newOwnerEmail;
        this.transactionId = transactionId;
        this.newOwnerPassword = newOwnerPassword;
    }

    // Getters
    public String getTransactionId() {
        return transactionId;
    }

    public String getNewOwnerEmail() {
        return newOwnerEmail;
    }

    public String getNewOwnerPassword() {
        return newOwnerPassword;
    }

    // Setters
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setNewOwnerEmail(String newOwnerEmail) {
        this.newOwnerEmail = newOwnerEmail;
    }

    public void setNewOwnerPassword(String newOwnerPassword) {
        this.newOwnerPassword = newOwnerPassword;
    }
}

@RestController
@RequestMapping("/animals")
public class AnimalsController {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @PostMapping("/createWildAnimal")
    public ResponseEntity<Animal> createWildAnimal() {
        String wildAnimalGenCode = Animal.generateWildAnimalGeneticCode();
        Animal wildAnimal = new Animal("", "puszek", wildAnimalGenCode);
        Animal savedWildAnimal = animalRepository.save(wildAnimal);

        return ResponseEntity.status(HttpStatus.OK).body(savedWildAnimal);
    }

    @PutMapping("/changeOwner")
    public ResponseEntity<Animal> changeOwner(@Valid @RequestBody ChangeOwnerReq changeOwnerReq) {
        try {
            Optional<Transaction> transactionOptional = transactionRepository.findById(changeOwnerReq.getTransactionId());

            if(!transactionOptional.isPresent()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            Transaction transaction = transactionOptional.get();
            String animalId = transaction.getAnimalId();

            Optional<Animal> animalOptional = animalRepository.findById(animalId);
            if(!animalOptional.isPresent()) {
                System.err.print("Is animal present?: " + animalOptional.isPresent());
                System.err.println("AnimalId: " + animalId);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            Animal animal = animalOptional.get();

            User user = userRepository.findByEmail(changeOwnerReq.getNewOwnerEmail());

            boolean loginCondition = passwordEncoder.matches(changeOwnerReq.getNewOwnerPassword(), user.getPassword());
            boolean transactionCondition = (user.getEmail().equals(transaction.getSenderEmail()) && transaction.getRecipentApproved());

            if(user != null && (loginCondition && transactionCondition)) {
                animal.setOwner(user.getId());
                Animal updatedAnimal = animalRepository.save(animal);

                return ResponseEntity.status(HttpStatus.OK).body(updatedAnimal);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }


        } catch(Exception e) {
            System.err.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
