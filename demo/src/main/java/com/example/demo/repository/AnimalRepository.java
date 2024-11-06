package com.example.demo.repository;

import com.example.demo.model.Animal;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimalRepository extends MongoRepository<Animal, String> {
    List<Animal> findByOwner(String owner);
}