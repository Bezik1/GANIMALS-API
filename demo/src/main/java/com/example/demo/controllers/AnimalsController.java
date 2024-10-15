package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Animal;
import com.example.demo.repository.AnimalRepository;

@RestController
@RequestMapping("/animals")
public class AnimalsController {
    @Autowired
    private AnimalRepository animalRepository;

    @PostMapping("/createWildAnimal")
    public ResponseEntity<Animal> createWildAnimal() {
        String wildAnimalGenCode = Animal.generateWildAnimalGeneticCode();
        Animal wildAnimal = new Animal("", "puszek", wildAnimalGenCode);
        Animal savedWildAnimal = animalRepository.save(wildAnimal);

        return ResponseEntity.status(HttpStatus.OK).body(savedWildAnimal);
    }
}
