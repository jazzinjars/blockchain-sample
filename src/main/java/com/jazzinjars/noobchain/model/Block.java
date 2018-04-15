package com.jazzinjars.noobchain.model;

import com.jazzinjars.noobchain.util.StringUtil;

import java.util.Date;

public class Block {

    private String hash;
    private String previousHash;
    private String data;    //our data will be a simple message
    private long timeStamp; //as number of milliseconds since 1/1/1970
    private int nonce;

    //Block Constructor
    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    //Calculate hash based on blocks contents
    public String calculateHash() {
        String calculatedHash = StringUtil.applySHA256(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + data);
        return calculatedHash;
    }

    public void mineBlock(int difficulty) {
        String target = StringUtil.getDifficultyString(difficulty); //Create a block with difficulty 0
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            this.hash = calculateHash();
        }
        System.out.println("Block Mined!!!" + hash);
    }

    public String getHash() {
        return this.hash;
    }

    public String getPreviousHash() {
        return this.previousHash;
    }
}
