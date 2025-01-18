package com.example.demo.types.Users;

import com.example.demo.interfaces.SenderReqInterface;

public class BreedAnimalReq implements SenderReqInterface {
    private float amount;
    private String senderEmail;
    private String senderPassword;
    private String animalName;
    private String firstParentId;
    private String secondParentId;

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getSenderPassword() {
        return senderPassword;
    }

    public void setSenderPassword(String senderPassword) {
        this.senderPassword = senderPassword;
    }

    public float getAmount() {
        return amount;
    }

    public String getAnimalName() {
        return animalName;
    }

    public void setAnimalName(String animalName) {
        this.animalName = animalName;
    }

    public String getFirstParentId() {
        return firstParentId;
    }

    public void setFirstParentId(String firstParentId) {
        this.firstParentId = firstParentId;
    }

    public String getSecondParentId() {
        return secondParentId;
    }

    public void setSecondParentId(String secondParentId) {
        this.secondParentId = secondParentId;
    }
}