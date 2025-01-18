package com.example.demo.types.Transactions;

import com.example.demo.interfaces.SenderReqInterface;
import com.example.demo.model.Transaction;

public class SendTransactionReq implements SenderReqInterface {
    private Transaction transaction;
    private String senderPassword;

    public SendTransactionReq() {}

    public SendTransactionReq(Transaction transaction, String senderPassword) {
        this.transaction = transaction;
        this.senderPassword = senderPassword;
    }

    // Setters
    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public void setSenderPassword(String senderPassword) {
        this.senderPassword = senderPassword;
    }

    // Getter
    public Transaction getTransaction() {
        return transaction;
    }

    public String getSenderPassword() {
        return senderPassword;
    }
}