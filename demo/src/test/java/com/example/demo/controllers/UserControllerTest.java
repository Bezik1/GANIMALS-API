package com.example.demo.controllers;
import com.example.demo.model.Admin;
import com.example.demo.model.Animal;
import com.example.demo.model.User;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.AnimalRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.types.Response.Response;
import com.example.demo.types.Users.BreedAnimalReq;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Rank Updating Successfully Test")
    void testChangeRank_Success() {
        Admin admin = new Admin();
        admin.setEmail("admin@example.com");
        admin.setPassword("password");

        User user = new User();
        user.setEmail("admin@example.com");

        when(userRepository.findByEmail(admin.getEmail())).thenReturn(user);
        doNothing().when(userRepository).delete(user);
        when(passwordEncoder.encode(admin.getPassword())).thenReturn("hashedPassword");
        when(adminRepository.save(admin)).thenReturn(admin);

        Response response = userController.changeRank(admin);

        assertEquals(CREATED, response.getStatus());
        assertEquals(admin, response.getBody());
    }

    @Test
    @DisplayName("Rank Updating User Not Found Test")
    void testChangeRank_UserNotFound() {
        Admin admin = new Admin();
        admin.setEmail("nonexistent@example.com");

        when(userRepository.findByEmail(admin.getEmail())).thenReturn(null);

        Response response = userController.changeRank(admin);

        assertEquals(BAD_REQUEST, response.getStatus());
    }

    @Test
    @DisplayName("Breeding Succesfully Test")
    void testBreedAnimal_Success() {
        User user = new User();
        user.setId("user1");
        user.setEmail("user@example.com");
        user.setPassword("hashedPassword");
        user.setSaldo(100);

        Animal parent1 = new Animal();
        parent1.setId("parent1");
        parent1.setOwner("user1");

        Animal parent2 = new Animal();

        while(Animal.analyzeParentString(parent1.getGenome()).gender == Animal.analyzeParentString(parent2.getGenome()).gender) {
            parent2 = new Animal();
        }

        parent2.setId("parent2");
        parent2.setOwner("user1");

        Animal child = new Animal();

        BreedAnimalReq req = new BreedAnimalReq();
        req.setSenderEmail("user@example.com");
        req.setSenderPassword("password");
        req.setAmount(50);
        req.setFirstParentId("parent1");
        req.setSecondParentId("parent2");
        req.setAnimalName("child");

        when(userRepository.findByEmail(req.getSenderEmail())).thenReturn(user);
        when(passwordEncoder.matches(req.getSenderPassword(), user.getPassword())).thenReturn(true);
        when(animalRepository.findById("parent1")).thenReturn(Optional.of(parent1));
        when(animalRepository.findById("parent2")).thenReturn(Optional.of(parent2));
        when(animalRepository.save(any(Animal.class))).thenReturn(child);

        Response response = userController.breedAnimal(req);

        assertEquals(CREATED, response.getStatus());
        assertEquals(child, response.getBody());
    }

    @Test
    @DisplayName("Breeding Insufficient Funds Test")
    void testBreedAnimal_InsufficientFunds() {
        User user = new User();
        user.setId("user1");
        user.setEmail("user@example.com");
        user.setPassword("hashedPassword");
        user.setSaldo(30);

        BreedAnimalReq req = new BreedAnimalReq();
        req.setSenderEmail("user@example.com");
        req.setSenderPassword("password");
        req.setAmount(50);

        when(userRepository.findByEmail(req.getSenderPassword())).thenReturn(user);

        Response response = userController.breedAnimal(req);

        assertEquals(BAD_REQUEST, response.getStatus());
    }

    @Test
    @DisplayName("Login Successfully Test")
    void testLogin_Success_User() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("hashedPassword");

        User loginRequest = new User();
        loginRequest.setEmail("user@example.com");
        loginRequest.setPassword("password");

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(user);
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);

        Response response = userController.login(loginRequest);

        assertEquals(OK, response.getStatus());
        assertEquals(user, response.getBody());
    }

    @Test
    @DisplayName("Unauthorized Login Test")
    void testLogin_Unauthorized() {
        User loginRequest = new User();
        loginRequest.setEmail("user@example.com");
        loginRequest.setPassword("password");

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(null);

        Response response = userController.login(loginRequest);

        assertEquals(UNAUTHORIZED, response.getStatus());
    }

    @Test
    @DisplayName("User Successfully Created Test")
    void testCreateUser_Success() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("password");

        User savedUser = new User();
        savedUser.setEmail("user@example.com");
        savedUser.setPassword("hashedPassword");

        when(passwordEncoder.encode(user.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(user)).thenReturn(savedUser);

        Response response = userController.createUser(user);

        assertEquals(savedUser.getPassword(), ((User) response.getBody()).getPassword());
        assertEquals(CREATED, response.getStatus());
        assertEquals(savedUser, response.getBody());
    }
}
