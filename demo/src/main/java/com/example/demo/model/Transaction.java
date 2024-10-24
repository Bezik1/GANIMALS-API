package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.PositiveOrZero;

@Document(collection = "Transactions")
public class Transaction {
    @Id
    private String id;

    private String senderEmail;
    private String recipentEmail;

    private String animalId;

    @PositiveOrZero
    private float amount;

    private boolean recipentApproved;

    private String status;

    public Transaction() {}

    public Transaction(String senderEmail, String recipentEmail, String animalId, float amount) {
        this.senderEmail = senderEmail;
        this.recipentEmail = recipentEmail;
        this.animalId = animalId;
        this.amount = amount;
        this.recipentApproved = false;
        this.status = "Waiting for approvements";
    }

    // Setters
    public void setStatus(String status){
        this.status = status;
    }

    public void setRecipentApproved(boolean recipentApproved){
        this.recipentApproved = recipentApproved;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public void setRecipentEmail(String recipentEmail) {
        this.recipentEmail = recipentEmail;
    }

    public void setAnimalId(String animalId) {
        this.animalId = animalId;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    // Getters
    public String getStatus() {
        return status;
    }

    public boolean getRecipentApproved() {
        return recipentApproved;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public String getRecipentEmail() {
        return recipentEmail;
    }

    public String getAnimalId() {
        return animalId;
    }

    public float getAmount() {
        return amount;
    }
}