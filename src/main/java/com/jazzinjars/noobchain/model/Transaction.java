package com.jazzinjars.noobchain.model;

import com.jazzinjars.noobchain.NoobChain;
import com.jazzinjars.noobchain.util.StringUtil;

import java.security.PrivateKey;
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

    //Returns true if new Transaction could be created
    public boolean processTransaction() {
        if (verifySignature() == false) {
            System.out.println(">>Transaction Signature failed to verify");
            return false;
        }

        //gather transaction inputs (Make sure they are unspent)
        for (TransactionInput i : inputs) {
            i.setUtxo(NoobChain.utxos.get(i.getTransactionOutputId()));
        }

        //check if transaction is valid
        if (getInputsValue() < NoobChain.minimumTransaction) {
            System.out.println(">>Transaction Inputs too small: " + getInputsValue());
            return false;
        }

        //generate transaction outputs
        float leftOver = getInputsValue() - value;  //get value of inputs then the left over change
        transactionId = calculateHash();
        outputs.add(new TransactionOutput(this.recipient, value, transactionId));   //send value to recipient
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));   //send the value over 'change' back to sender

        //add outputs to Unspent list
        for (TransactionOutput o : outputs) {
            NoobChain.utxos.put(o.getId(), o);
        }

        //remove transaction inputs from UTXOs lists as spent
        for (TransactionInput i : inputs) {
            if (i.getUtxo() == null) continue;  //if Transaction can't be found skip it
            NoobChain.utxos.remove(i.getUtxo().getId());
        }

        return true;
    }

    //return sum of inputs(UTXOs) values
    public float getInputsValue() {
        float total = 0;
        for (TransactionInput i : inputs) {
            if (i.getUtxo() == null) continue;  //if Transaction can't be found skip it
            total += i.getUtxo().getValue();
        }
        return total;
    }

    //return sum of outputs
    public float getOutputsValue() {
        float total = 0;
        for (TransactionOutput o : outputs) {
            total += o.getValue();
        }
        return total;
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

    //Signs all the data we don't wish to be tampered with
    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value);
        this.signature = StringUtil.applyECDSASig(privateKey, data);
    }

    //Verifies the data we signed hasn't been tampered with
    public boolean verifySignature() {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value);
        return StringUtil.verifyECDASig(sender, data, signature);
    }
}
