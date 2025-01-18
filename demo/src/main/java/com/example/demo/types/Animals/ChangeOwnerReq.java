package com.example.demo.types.Animals;

public class ChangeOwnerReq {
    private String transactionId;
    private String newOwnerEmail;
    private String newOwnerPassword;

    public ChangeOwnerReq() {}

    public ChangeOwnerReq(String animalId, String newOwnerEmail, String transactionId, String newOwnerPassword) {
        this.newOwnerEmail = newOwnerEmail;
        this.transactionId = transactionId;
        this.newOwnerPassword = newOwnerPassword;
    }

    // Getters
    public String getTransactionId() {
        return transactionId;
    }

    public String getNewOwnerEmail() {
        return newOwnerEmail;
    }

    public String getNewOwnerPassword() {
        return newOwnerPassword;
    }

    // Setters
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setNewOwnerEmail(String newOwnerEmail) {
        this.newOwnerEmail = newOwnerEmail;
    }

    public void setNewOwnerPassword(String newOwnerPassword) {
        this.newOwnerPassword = newOwnerPassword;
    }
}