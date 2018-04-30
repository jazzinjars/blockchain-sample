package com.jazzinjars.noobchain.model;

import com.jazzinjars.noobchain.util.StringUtil;

import java.security.PublicKey;

public class TransactionOutput {

	private String id;
	private PublicKey recipient;		//Also known as the new owner of theses coins
	private float value;				//The amount of coins they own
	private String parentTransactionId;	//The ID of the transaction this output was created in

	public TransactionOutput(PublicKey recipient, float value, String parentTransactionId) {
		this.recipient = recipient;
		this.value = value;
		this.parentTransactionId = parentTransactionId;
		this.id = StringUtil.applySHA256(StringUtil.getStringFromKey(recipient) + Float.toString(value) + parentTransactionId);
	}

	public boolean isMine(PublicKey publicKey) {
		return (publicKey == recipient);
	}

	public String getId() {
		return id;
	}

	public PublicKey getRecipient() {
		return recipient;
	}

	public float getValue() {
		return value;
	}

	public String getParentTransactionId() {
		return parentTransactionId;
	}

}
