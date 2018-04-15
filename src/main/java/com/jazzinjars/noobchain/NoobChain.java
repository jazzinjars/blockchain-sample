package com.jazzinjars.noobchain;

import com.jazzinjars.noobchain.model.Block;
import com.jazzinjars.noobchain.util.StringUtil;

import java.util.ArrayList;

public class NoobChain {

    public static ArrayList<Block> blockChain = new ArrayList<Block>();
    public static int difficulty = 5;

    public static void main(String[] args) {

        //add our blocks to the blockchain ArrayList
        System.out.println("Trying to Mine block 1... ");
        addBlock(new Block("Hi im the first block", "0"));

        System.out.println("Trying to Mine block 2... ");
        addBlock(new Block("Yo im the second block", blockChain.get(blockChain.size()-1).getHash()));

        System.out.println("Trying to Mine block 3... ");
        addBlock(new Block("Hey im the third block", blockChain.get(blockChain.size()-1).getHash()));

        System.out.println("\nBlockchain is Valid: " + isChainValid());

        String blockchainJson = StringUtil.getJson(blockChain);
        System.out.println("\nThe block chain: ");
        System.out.println(blockchainJson);
    }

    public static boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        //loop through blockchain to check hashes
        for (int i = 1; i < blockChain.size(); i++) {
            currentBlock = blockChain.get(i);
            previousBlock = blockChain.get(i - 1);

            //compare registered hash and calculated hash
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                System.out.println("Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
            //check if hash is solved
            if(!currentBlock.getHash().substring(0, difficulty).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }
        }
        return true;
    }

    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockChain.add(newBlock);
    }
}
