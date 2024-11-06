package com.example.demo.controllers;

import com.example.demo.model.Admin;
import com.example.demo.model.Animal;
import com.example.demo.model.User;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.AnimalRepository;
import com.example.demo.repository.UserRepository;

import jakarta.validation.Valid;

import java.util.Optional;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

class BreedAnimalReq {
    private float amount;
    private String userEmail;
    private String userPassword;
    private String animalName;
    private String firstParentId;
    private String secondParentId;

    public String getUserEmail() {
        return userEmail;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public float getAmount() {
        return amount;
    }

    public String getAnimalName() {
        return animalName;
    }

    public void setAnimalName(String animalName) {
        this.animalName = animalName;
    }

    public String getFirstParentId() {
        return firstParentId;
    }

    public void setFirstParentId(String firstParentId) {
        this.firstParentId = firstParentId;
    }

    public String getSecondParentId() {
        return secondParentId;
    }

    public void setSecondParentId(String secondParentId) {
        this.secondParentId = secondParentId;
    }
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

    @Autowired
    private AdminRepository adminRepository;

    //private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("changeRank")
    public ResponseEntity<Admin> changeRank(@RequestBody Admin admin) {
        try {
            User user = userRepository.findByEmail(admin.getEmail());
            if(user == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            userRepository.delete(user);

            String hashedPassword = passwordEncoder.encode(admin.getPassword());
            admin.setPassword(hashedPassword);

            Admin savedAdmin = adminRepository.save(admin);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAdmin);
        } catch(DataIntegrityViolationException e) {
            System.err.println("Error creating user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        } catch(Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/breedAnimal")
    public ResponseEntity<Animal> breedAnimal(@RequestBody BreedAnimalReq breedAnimalReq) {
        if(breedAnimalReq.getAmount() < 0) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        User user = userRepository.findByEmail(breedAnimalReq.getUserEmail());
        if((user.getSaldo() - breedAnimalReq.getAmount()) < 0) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        if (user != null && passwordEncoder.matches(breedAnimalReq.getUserPassword(), user.getPassword())) {
            Optional<Animal> firstParentOpt  = animalRepository.findById(breedAnimalReq.getFirstParentId());
            Optional<Animal> secondParentOpt = animalRepository.findById(breedAnimalReq.getSecondParentId());

            if(!firstParentOpt.isPresent() || !secondParentOpt.isPresent()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

            Animal firstParent = firstParentOpt.get();
            Animal secondParent = secondParentOpt.get();

            String firstParentOwner = firstParent.getOwner();
            String secondParentOwner = secondParent.getOwner();

            if (!firstParentOwner.equals(user.getId()) || !secondParentOwner.equals(user.getId())) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

            Animal child = firstParent.breed(user.getId(), breedAnimalReq.getAnimalName(), secondParent.getGenome());
            Animal savedChild = animalRepository.save(child);

            user.setSaldo(user.getSaldo()-breedAnimalReq.getAmount());
            User userSaved = userRepository.save(user);

            return ResponseEntity.status(HttpStatus.OK).body(savedChild);
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