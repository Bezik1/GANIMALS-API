package com.example.demo.repository;

import com.example.demo.model.Animal;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimalRepository extends MongoRepository<Animal, String> {
    /**
     * Return all of the ganimals that are owned by some user
     * 
     * @param owner
     * @return Ganimal[]
     */
    List<Animal> findByOwner(String owner);
}