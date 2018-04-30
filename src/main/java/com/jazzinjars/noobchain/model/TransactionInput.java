package com.jazzinjars.noobchain.model;

public class TransactionInput {

	private String transactionOutputId;	//Reference to TransactionOutput -> transactionID
	private TransactionOutput utxo;		//Contains the Unspent Transaction Output

	public TransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}

	public String getTransactionOutputId() {
		return transactionOutputId;
	}

	public TransactionOutput getUtxo() {
		return utxo;
	}

	public void setUtxo(TransactionOutput utxo) {
		this.utxo = utxo;
	}
}
