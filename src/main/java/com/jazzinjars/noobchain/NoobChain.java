package com.jazzinjars.noobchain;

import com.jazzinjars.noobchain.model.*;
import com.jazzinjars.noobchain.util.StringUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

public class NoobChain {

    public static ArrayList<Block> blockChain = new ArrayList<Block>();
    public static HashMap<String, TransactionOutput> utxos = new HashMap<String, TransactionOutput>();  //list of all unspent transactions

    public static int difficulty = 3;
    public static float minimumTransaction = 0.1f;
    private static Wallet walletA;
    private static Wallet walletB;
    private static Wallet coinBase;

    private static Transaction genesisTransaction;

    public static void main(String[] args) {

        //1- Add our Blocks to the BlockChain ArrayList

        //Setup Bouncey castle as a Security Provider
        Security.addProvider(new BouncyCastleProvider());

        //Create the wallets
        walletA = new Wallet();
        walletB = new Wallet();
        coinBase = new Wallet();

        //2- Create the Genesis Transaction, which send 100 NoobChain -> WalletA
        genesisTransaction = new Transaction(coinBase.getPublicKey(), walletA.getPublicKey(), 100f, null);
        genesisTransaction.generateSignature(coinBase.getPrivateKey()); // manually sign the Genesis Transaction
        genesisTransaction.setTransactionId("0"); //manually set the Transaction ID
        genesisTransaction.getOutputs().add(new TransactionOutput(genesisTransaction.getRecipient(), genesisTransaction.getValue(), genesisTransaction.getTransactionId()));

        //It's important to store our first Transaction in the UTXOs list
        utxos.put(genesisTransaction.getOutputs().get(0).getId(), genesisTransaction.getOutputs().get(0));

        System.out.println("Creating and Mining Genesis Block");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        //3- Testing our BlockChain
        Block block1 = new Block(genesis.getHash());
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
        block1.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 40f));
        addBlock(block1);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block2 = new Block(block1.getHash());
        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
        block2.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 1000f));
        addBlock(block2);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block3 = new Block(block2.getHash());
        System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
        block3.addTransaction(walletB.sendFunds( walletA.getPublicKey(), 20));
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        isChainValid();
    }

    public static boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        HashMap<String,TransactionOutput> tempUTXOs = new HashMap<String,TransactionOutput>(); //a temporary working list of unspent transactions at a given block state.

        tempUTXOs.put(genesisTransaction.getOutputs().get(0).getId(), genesisTransaction.getOutputs().get(0));

        //Loop through blockchain to check hashes
        for (int i=1; i < blockChain.size(); i++) {

            currentBlock = blockChain.get(i);
            previousBlock = blockChain.get(i-1);

            //Compare registered hash and calculated hash
            if (!currentBlock.getHash().equals(currentBlock.calculateHash()) ){
                System.out.println("#Current Hashes not equal");
                return false;
            }
            //Compare previous hash and registered previous hash
            if (!previousBlock.getHash().equals(currentBlock.getPreviousHash()) ) {
                System.out.println("#Previous Hashes not equal");
                return false;
            }
            //Check if hash is solved
            if (!currentBlock.getHash().substring(0, difficulty).equals(hashTarget)) {
                System.out.println("#This block hasn't been mined");
                return false;
            }

            //Loop thru blockchains transactions
            TransactionOutput tempOutput;
            for (int t = 0; t < currentBlock.getTransactions().size(); t++) {
                Transaction currentTransaction = currentBlock.getTransactions().get(t);

                if (!currentTransaction.verifySignature()) {
                    System.out.println("#Signature on Transaction(" + t + ") is Invalid");
                    return false;
                }
                if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
                    return false;
                }

                for(TransactionInput input: currentTransaction.getInputs()) {
                    tempOutput = tempUTXOs.get(input.getTransactionOutputId());

                    if (tempOutput == null) {
                        System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
                        return false;
                    }

                    if (input.getUtxo().getValue() != tempOutput.getValue()) {
                        System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
                        return false;
                    }

                    tempUTXOs.remove(input.getTransactionOutputId());
                }

                for (TransactionOutput output: currentTransaction.getOutputs()) {
                    tempUTXOs.put(output.getId(), output);
                }

                if (currentTransaction.getOutputs().get(0).getRecipient() != currentTransaction.getRecipient()) {
                    System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
                    return false;
                }
                if (currentTransaction.getOutputs().get(1).getRecipient() != currentTransaction.getSender()) {
                    System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
                    return false;
                }
            }
        }

        System.out.println("Blockchain is valid!");
        return true;
    }

    public static void mainNoTransactions(String[] args) {

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

    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockChain.add(newBlock);
    }
}
