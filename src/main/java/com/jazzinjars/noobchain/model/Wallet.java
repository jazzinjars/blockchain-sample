package com.jazzinjars.noobchain.model;

import com.jazzinjars.noobchain.NoobChain;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {

    private PublicKey publicKey;
    private PrivateKey privateKey;

    private HashMap<String, TransactionOutput> utxos = new HashMap<String, TransactionOutput>();    //only UTXOs owned by this wallet

    public Wallet() {
        generateKeyPair();
    }

    private void generateKeyPair() {

        try {
            KeyPairGenerator keygen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");

            //Initialize the key generator and generate a KeyPair
            keygen.initialize(ecSpec, random);  //256 bytes provides an acceptable security level
            KeyPair keyPair = keygen.generateKeyPair();

            //Set the public and private key from keyPair
            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

    //returns balance and stores the UTXO's owned by this wallet in this.UTXOs
    public float getBalance() {
        float total = 0;

        for (Map.Entry<String, TransactionOutput> item : NoobChain.utxos.entrySet()) {
            TransactionOutput utxo = item.getValue();
            if (utxo.isMine(publicKey)) {           //if output belongs to me (if coins belongs to me)
                utxos.put(utxo.getId(), utxo);      //add it to our list of unspent transactions
                total += utxo.getValue();
            }
        }
        return total;
    }

    //Generates and returns a new transaction from this wallet
    public Transaction sendFunds(PublicKey recipient, float value) {
        if (getBalance() < value) { //gather balance and check funds
            System.out.println(">> Not Enough funds to send transaction. Transaction Discarded.");
            return null;
        }

        //Create array list of inputs
        ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();

        float total = 0;

        for (Map.Entry<String, TransactionOutput> item : this.utxos.entrySet()) {
            TransactionOutput utxo = item.getValue();
            total += utxo.getValue();
            inputs.add(new TransactionInput(utxo.getId()));
            if (total > value) break;
        }

        Transaction newTransaction = new Transaction(publicKey, recipient, value, inputs);
        newTransaction.generateSignature(privateKey);

        for (TransactionInput input : inputs) {
            utxos.remove(input.getTransactionOutputId());
        }

        return newTransaction;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public HashMap<String, TransactionOutput> getUtxos() {
        return utxos;
    }

}
