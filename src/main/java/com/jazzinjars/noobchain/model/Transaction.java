package com.jazzinjars.noobchain.model;

import com.jazzinjars.noobchain.util.StringUtil;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class Transaction {

    public String transactionId;    //this is also the hast of the transaction
    public PublicKey sender;     //senders address/public key
    public PublicKey recipient;     //recipient address/public key
    public float value;
    public byte[] signature;        //this is to prevent anybody else from spending funds in our wallet

    public List<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public List<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    private static int sequence = 0;    //a rough count of how many transactions have been generated

    public Transaction(PublicKey from, PublicKey to, float value, List<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
    }

    //This calculates the transaction hash(which will be used as its id)
    private String calculateHash() {
        sequence++; //increase the sequence to avoid 2 transactions having the same hash
        return StringUtil.applySHA256(
                StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(recipient) +
                        Float.toString(value) + sequence
                );

    }
}
