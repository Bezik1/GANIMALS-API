package com.example.demo.controllers;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.example.demo.types.Animals.ChangeOwnerReq;
import com.example.demo.types.Animals.Genom;
import com.example.demo.types.Response.ErrorResponse;
import com.example.demo.types.Response.Response;
import com.example.demo.types.Response.SuccessResponse;

import jakarta.validation.Valid;

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

    /**
     * Send the genome properties of the ganimal in the form of the txt file
     * @param animalId
     * @return Text File
     */
    @GetMapping("/download/{animalId}")
    public ResponseEntity<byte[]> downloadAnimalTraits(@PathVariable String animalId) {
        try {
            Optional<Animal> animalOptional = animalRepository.findById(animalId);

            if (animalOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Animal not found.".getBytes(StandardCharsets.UTF_8));
            }

            Animal animal = animalOptional.get();
            Genom animalGenom = Animal.analyzeParentString(animal.getGenome());

            String fileContent = ("Name: " + animal.getName() + "\n" +
                                "Base Color: " + "#" + animalGenom.baseColor.hex + "\n" +
                                "Eye Color: " + "#" + animalGenom.eyeColor.hex + "\n" +
                                "Special Color: " + "#" + animalGenom.specialColor.hex + "\n" +
                                "Is Carnivour: " + animalGenom.isCarnivour + "\n" +
                                "Has Spikes: " + animalGenom.hasSpikes + "\n" +
                                "Has Claws: " + animalGenom.hasClaws + "\n" +
                                "Gender: " + animalGenom.gender + "\n");

            byte[] fileBytes = fileContent.getBytes(StandardCharsets.UTF_8);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", "animal_traits.txt");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileBytes);
        } catch (Exception e) {
            String errorMessage = "Error generating file.";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorMessage.getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * Returns user ganimals
     * @param userId
     * @return Ganimal[]
     */
    @PostMapping("/getUserAnimals/{userId}")
    public Response getUserAnimals(@PathVariable String userId) {
        try {
            List<Animal> animals = animalRepository.findByOwner(userId);
            return SuccessResponse.httpStatus(HttpStatus.OK).build(animals);
        } catch(Exception err) {
            return ErrorResponse.httpStatus(HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Creates wild animal without an owner to the database
     * @return
     */
    @PostMapping("/createWildAnimal")
    public Response createWildAnimal() {
        String wildAnimalGenCode = Animal.generateWildAnimalGeneticCode();
        Animal wildAnimal = new Animal("", "puszek", wildAnimalGenCode);
        Animal savedWildAnimal = animalRepository.save(wildAnimal);

        return SuccessResponse.httpStatus(HttpStatus.CREATED).build(savedWildAnimal);
    }


    /**
     * Changes the owner of the ganimal after checking if transaction has been
     * approved by the recipent, then the function changes owner of the ganimal
     *
     * @param changeOwnerReq
     * @return
     */
    @PutMapping("/changeOwner")
    public Response changeOwner(@Valid @RequestBody ChangeOwnerReq changeOwnerReq) {
        try {
            Optional<Transaction> transactionOptional = transactionRepository.findById(changeOwnerReq.getTransactionId());

            if(!transactionOptional.isPresent()) return ErrorResponse.httpStatus(HttpStatus.BAD_REQUEST);
            Transaction transaction = transactionOptional.get();
            String animalId = transaction.getAnimalId();

            Optional<Animal> animalOptional = animalRepository.findById(animalId);
            if(!animalOptional.isPresent()) {
                System.err.print("Is animal present?: " + animalOptional.isPresent());
                System.err.println("AnimalId: " + animalId);
                return ErrorResponse.httpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            Animal animal = animalOptional.get();

            User user = userRepository.findByEmail(changeOwnerReq.getNewOwnerEmail());

            boolean loginCondition = passwordEncoder.matches(changeOwnerReq.getNewOwnerPassword(), user.getPassword());
            boolean transactionCondition = (user.getEmail().equals(transaction.getRecipentEmail()) && transaction.getRecipentApproved());

            if(user != null && (loginCondition && transactionCondition)) {
                animal.setOwner(user.getId());
                Animal updatedAnimal = animalRepository.save(animal);

                return SuccessResponse.httpStatus(HttpStatus.OK).build(updatedAnimal);
            } else {
                return ErrorResponse.httpStatus(HttpStatus.UNAUTHORIZED);
            }


        } catch(Exception e) {
            System.err.println(e);
            return ErrorResponse.httpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
