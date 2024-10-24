package com.jpmc.midascore.entity;

import com.jpmc.midascore.foundation.Transaction;

import jakarta.persistence.*;

@Entity
public class TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // This indicates a many-to-one relationship
    @JoinColumn(name = "sender_id") // This is the column in the TransactionRecord table
    private UserRecord sender;

    @ManyToOne // This indicates a many-to-one relationship
    @JoinColumn(name = "recipient_id") // This is the column in the TransactionRecord table
    private UserRecord recipient;

    private Float amount; // Assuming the amount is a float

    // Getters and setters...

    public TransactionRecord(UserRecord sender,UserRecord recipient,float amount) {
        this.sender=sender;
        this.recipient=recipient;
        this.amount=amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserRecord getSender() {
        return sender;
    }

    public void setSender(UserRecord sender) {
        this.sender = sender;
    }

    public UserRecord getRecipient() {
        return recipient;
    }

    public void setRecipient(UserRecord recipient) {
        this.recipient = recipient;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }
}
