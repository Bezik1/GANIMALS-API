package com.example.demo.controllers;

import com.example.demo.model.Animal;
import com.example.demo.model.User;
import com.example.demo.repository.AnimalRepository;
import com.example.demo.repository.UserRepository;

import jakarta.validation.Valid;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

class BreedAnimalReq {
    String userEmail;
    String userPassword;
    String animalName;
    String firstParentId;
    String secondParentId;
}

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/breedAnimal")
    public ResponseEntity<Animal> breedAnimal(@Valid @RequestBody BreedAnimalReq breedAnimalReq) {
        User user = userRepository.findByEmail(breedAnimalReq.userEmail);

        if (user != null && passwordEncoder.matches(breedAnimalReq.userPassword, user.getPassword())) {
            Optional<Animal> firstParentOpt  = animalRepository.findById(breedAnimalReq.firstParentId);
            Optional<Animal> secondParentOpt = animalRepository.findById(breedAnimalReq.secondParentId);

            if(!firstParentOpt.isPresent() || !secondParentOpt.isPresent()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

            Animal firstParent = firstParentOpt.get();
            Animal secondParent = secondParentOpt.get();

            String firstParentOwner = firstParent.getOwner();
            String secondParentOwner = secondParent.getOwner();

            if(firstParentOwner != user.getId() || secondParentOwner != user.getId()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

            Animal child = firstParent.breed(user.getName(), breedAnimalReq.animalName, secondParent.getGenome());

            return ResponseEntity.status(HttpStatus.OK).body(child);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@Valid @RequestBody User loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail());

        if (user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        try {
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(hashedPassword);

            User savedUser = userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch(DataIntegrityViolationException e) {
            System.err.println("Error creating user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        } catch(Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}