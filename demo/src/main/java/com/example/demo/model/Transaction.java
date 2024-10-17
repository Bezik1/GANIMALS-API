package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.PositiveOrZero;

@Document(collection = "Transactions")
public class Transaction {
    @Id
    private String id;

    private String senderId;
    private String recipentId;

    private String animalId;

    @PositiveOrZero
    private float amount;

    private boolean senderApproved;
    private boolean recipentApproved;

    private String status;

    public Transaction() {}

    public Transaction(String senderId, String recipentId, String animalId, float amount) {
        this.senderId = senderId;
        this.recipentId = recipentId;
        this.animalId = animalId;
        this.senderApproved = false;
        this.recipentApproved = false;
        this.status = "Waiting for approvements";
    }

    // Setters
    public void setStatus(String status){
        this.status = status;
    }

    public void setSenderApproved(boolean senderApproved){
        this.senderApproved = senderApproved;
    }

    public void setRecipentApproved(boolean recipentApproved){
        this.recipentApproved = recipentApproved;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setRecipentId(String recipentId) {
        this.recipentId = recipentId;
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

    public boolean getSenderApproved() {
        return senderApproved;
    }

    public boolean getRecipentApproved() {
        return recipentApproved;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getRecipentId() {
        return recipentId;
    }

    public String getAnimalId() {
        return animalId;
    }

    public float getAmount() {
        return amount;
    }
}