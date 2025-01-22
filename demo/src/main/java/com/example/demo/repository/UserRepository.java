package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    /**
     * Finding user by email
     * 
     * @param email
     * @return User
     */
    User findByEmail(String email);
}