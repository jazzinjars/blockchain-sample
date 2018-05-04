package com.jazzinjars.noobchain.model;

import com.jazzinjars.noobchain.util.StringUtil;

import java.util.ArrayList;
import java.util.Date;

public class Block {

    private String hash;
    private String previousHash;
    private String merkleRoot;
    private ArrayList<Transaction> transactions = new ArrayList<Transaction>();  //our data will be an array of Transactions
    private String data;    //our data will be a simple message
    private long timeStamp; //as number of milliseconds since 1/1/1970
    private int nonce;

    //Block Constructor
    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    //Calculate hash based on blocks contents
    public String calculateHash() {
        String calculatedHash = StringUtil.applySHA256(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot);
        return calculatedHash;
    }

    public void mineBlock(int difficulty) {
        merkleRoot = StringUtil.getMerkleRoot(transactions);
        String target = StringUtil.getDifficultyString(difficulty); //Create a block with difficulty 0
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            this.hash = calculateHash();
        }
        System.out.println("Block Mined!!!" + hash);
    }

    //Add Transactions to this Block
    public boolean addTransaction(Transaction transaction) {
        //process transaction and check if valid, unless block is genesis block then ignore
        if (transaction == null) return false;
        if (previousHash != "0") {
            if (!transaction.processTransaction()) {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block!");
        return true;
    }

    public String getHash() {
        return this.hash;
    }

    public String getPreviousHash() {
        return this.previousHash;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public String getMerkleRoot() {
        return merkleRoot;
    }

}
