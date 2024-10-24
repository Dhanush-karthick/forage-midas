package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@Service
public class TransactionService {

    @Autowired
    private UserRepository userRecordRepository;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Autowired
    private RestTemplate restTemplate; // Inject the RestTemplate to call the Incentive API

    @Transactional
    public boolean processTransaction(Long senderId, Long recipientId, float amount) {
        UserRecord sender = userRecordRepository.findById(senderId).orElse(null);
        UserRecord recipient = userRecordRepository.findById(recipientId).orElse(null);

        // Validate the transaction
        if (sender == null || recipient == null || sender.getBalance() < amount) {
            return false;  
        }

        // Deduct the amount from the sender's balance
        sender.setBalance(sender.getBalance() - amount);

        // Create the transaction object to send to the Incentive API
        Transaction transaction = new Transaction(senderId, recipientId, amount);

        // Call the Incentive API
        ResponseEntity<Incentive> response = restTemplate.postForEntity(
            "http://localhost:8080/incentive", 
            transaction, 
            Incentive.class
        );

        Incentive incentive = response.getBody();
        float incentiveAmount = incentive != null ? incentive.getAmount() : 0;

        // Add the incentive to the recipient's balance
        recipient.setBalance(recipient.getBalance() + amount + incentiveAmount);

        // Persist updated user balances and transaction record
        TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, amount);
        transactionRecordRepository.save(transactionRecord);

        userRecordRepository.save(sender);
        userRecordRepository.save(recipient);

        // Logging to verify the balance updates
        System.out.println("Sender: " + sender.toString());
        System.out.println("Recipient: " + recipient.toString());

        return true;
    }
}
