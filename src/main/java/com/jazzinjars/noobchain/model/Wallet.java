package com.jazzinjars.noobchain.model;

import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class Wallet {

    private PublicKey publicKey;
    private PrivateKey privateKey;

    public Wallet() {
        generateKeyPair();
    }

    private void generateKeyPair() {

        try {
            KeyPairGenerator keygen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");

            //Initialize the key generator and generate a KeyPair
            keygen.initialize(ecSpec, random);  //256 bytes provides an acceptable secuirty level
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

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

}
