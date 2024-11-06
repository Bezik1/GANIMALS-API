package com.example.demo.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Admins")
public class Admin extends User {
    private String role;
    private int expirationTime;

    public Admin() {}

    public Admin(String name, String email, String password, String role, int expirationTime) {
        super(name, email, password);
        this.role = role;
        this.expirationTime = expirationTime;
    }

    public void setExpirationTime(int expirationTime) {
        if(expirationTime <= 0) throw new Error("Expiration Time must not be smaller or equal to zero!");
        this.expirationTime = expirationTime;
    }

    public void setRole(String role) {
        if(role != "Main Admin" | role != "Admin Help") throw new Error("Unknown privaliges");
        this.role = role;
    }

    public int getExpirationTime() {
        return expirationTime;
    }

    public String getRole() {
        return role;
    }
}
