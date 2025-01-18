package com.example.demo.types.Transactions;

public class RecipentApprovementReq {
    private String senderEmail;
    private String recipentEmail;
    private String recipentPassword;
    private boolean accepted;

    //Getters
    public boolean getAccepted() {
        return accepted;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public String getRecipentEmail() {
        return recipentEmail;
    }

    public String getRecipentPassword() {
        return recipentPassword;
    }

    // Setters
    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public void setRecipentEmail(String recipentEmail) {
        this.recipentEmail = recipentEmail;
    }

    public void setRecipentPassword(String recipentPassword) {
        this.recipentPassword = recipentPassword;
    }
}