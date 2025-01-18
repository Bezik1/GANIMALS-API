package com.example.demo.controllers;

import com.example.demo.model.Admin;
import com.example.demo.model.Animal;
import com.example.demo.model.User;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.AnimalRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.types.Response.ErrorResponse;
import com.example.demo.types.Response.Response;
import com.example.demo.types.Response.SuccessResponse;
import com.example.demo.types.Users.BreedAnimalReq;

import jakarta.validation.Valid;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("changeRank")
    public Response changeRank(@RequestBody Admin admin) {
        try {
            User user = userRepository.findByEmail(admin.getEmail());
            if(user == null) return ErrorResponse.httpStatus(HttpStatus.BAD_REQUEST).textMessage("User with such id does not exist!");
            userRepository.delete(user);

            String hashedPassword = passwordEncoder.encode(admin.getPassword());
            admin.setPassword(hashedPassword);

            Admin savedAdmin = adminRepository.save(admin);
            return SuccessResponse.httpStatus(HttpStatus.CREATED).build(savedAdmin);
        } catch(DataIntegrityViolationException e) {
            System.err.println("Error creating user: " + e.getMessage());
            return ErrorResponse.httpStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        } catch(Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
            return ErrorResponse.httpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/breedAnimal")
    public Response breedAnimal(@RequestBody BreedAnimalReq breedAnimalReq) {
        if(breedAnimalReq.getAmount() < 0) return ErrorResponse.httpStatus(HttpStatus.BAD_REQUEST).textMessage("Amount must be positive!");

        User user = userRepository.findByEmail(breedAnimalReq.getSenderEmail());
        if((user.getSaldo() - breedAnimalReq.getAmount()) < 0) return ErrorResponse.httpStatus(HttpStatus.BAD_REQUEST).textMessage("You don't have enough amount of money!");

        if (user != null && passwordEncoder.matches(breedAnimalReq.getSenderPassword(), user.getPassword())) {
            Optional<Animal> firstParentOpt  = animalRepository.findById(breedAnimalReq.getFirstParentId());
            Optional<Animal> secondParentOpt = animalRepository.findById(breedAnimalReq.getSecondParentId());

            if(!firstParentOpt.isPresent() || !secondParentOpt.isPresent()) return ErrorResponse.httpStatus(HttpStatus.BAD_REQUEST).textMessage("Some parent does not exist!");

            Animal firstParent = firstParentOpt.get();
            Animal secondParent = secondParentOpt.get();

            String firstParentOwner = firstParent.getOwner();
            String secondParentOwner = secondParent.getOwner();

            if (!firstParentOwner.equals(user.getId()) || !secondParentOwner.equals(user.getId())) return ErrorResponse.httpStatus(HttpStatus.BAD_REQUEST).textMessage("Some Owner does not exist!");

            Animal child = firstParent.breed(user.getId(), breedAnimalReq.getAnimalName(), secondParent.getGenome());
            Animal savedChild = animalRepository.save(child);

            user.setSaldo(user.getSaldo()-breedAnimalReq.getAmount());
            userRepository.save(user);

            return SuccessResponse.httpStatus(HttpStatus.CREATED).build(savedChild);
        } else {
            return ErrorResponse.httpStatus(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/createUserByTextFile")
    public Response createUserByTextFile(@RequestParam("file") MultipartFile file) {
        try {
            if(file.isEmpty()) return ErrorResponse.httpStatus(HttpStatus.BAD_REQUEST).textMessage("File is empty!");

            String[] data = new String(file.getBytes(), StandardCharsets.UTF_8).split("\n");
            String username = data[0];
            String email = data[1];
            String password = data[2];

            User user = new User(username, email, password);
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(hashedPassword);

            User savedUser = userRepository.save(user);
            return SuccessResponse.httpStatus(HttpStatus.CREATED).textMessage("User Created Succesfully!").build(savedUser);
        } catch(DataIntegrityViolationException e) {
            System.err.println("Error creating user: " + e.getMessage());
            return ErrorResponse.httpStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        } catch(Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
            return ErrorResponse.httpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public Response login(@Valid @RequestBody User loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail());
        Admin admin = adminRepository.findByEmail(loginRequest.getEmail());

        if (user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return SuccessResponse.httpStatus(HttpStatus.OK).textMessage("User Loginned Succesfully").build(user);
        } else if(admin != null && passwordEncoder.matches(loginRequest.getPassword(), admin.getPassword())) {
            return SuccessResponse.httpStatus(HttpStatus.OK).textMessage("Admin Loginned Succesfully").build(admin);
        } else {
            return ErrorResponse.httpStatus(HttpStatus.UNAUTHORIZED).textMessage("Password or login are incorrect!");
        }
    }

    @PostMapping
    public Response createUser(@Valid @RequestBody User user) {
        try {
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(hashedPassword);

            User savedUser = userRepository.save(user);
            return SuccessResponse.httpStatus(HttpStatus.CREATED).textMessage("User Created!").build(savedUser);
        } catch(DataIntegrityViolationException e) {
            System.err.println("Error creating user: " + e.getMessage());
            return ErrorResponse.httpStatus(HttpStatus.UNPROCESSABLE_ENTITY).textMessage("Incorrect properties!");
        } catch(Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
            return ErrorResponse.httpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}