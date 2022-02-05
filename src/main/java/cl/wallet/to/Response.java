package cl.wallet.to;

import java.math.BigInteger;

public class Response {

	private BigInteger bigIntegerPrivKey;
	private String privKey;
	private String wallet;
	
	public BigInteger getBigIntegerPrivKey() {
		return bigIntegerPrivKey;
	}
	public void setBigIntegerPrivKey(BigInteger bigIntegerPrivKey) {
		this.bigIntegerPrivKey = bigIntegerPrivKey;
	}
	public String getPrivKey() {
		return privKey;
	}
	public void setPrivKey(String privKey) {
		this.privKey = privKey;
	}
	public String getWallet() {
		return wallet;
	}
	public void setWallet(String wallet) {
		this.wallet = wallet;
	}


	
}
