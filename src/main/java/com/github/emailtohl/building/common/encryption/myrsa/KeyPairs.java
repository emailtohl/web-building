package com.github.emailtohl.building.common.encryption.myrsa;

import java.math.BigInteger;
/**
 * 存放公钥，私钥以及模的对象
 * 
 * @author HeLei
 */
public class KeyPairs {
	private transient BigInteger module;
	private transient BigInteger publicKey;
	private transient BigInteger privateKey;
	private transient int moduleBitLength;
	private transient int mArrayLength;
	private transient int cArrayLength;
	
	public BigInteger getModule() {
		return module;
	}
	public void setModule(BigInteger module) {
		this.module = module;
	}
	public BigInteger getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(BigInteger publicKey) {
		this.publicKey = publicKey;
	}
	public BigInteger getPrivateKey() {
		return privateKey;
	}
	public void setPrivateKey(BigInteger privateKey) {
		this.privateKey = privateKey;
	}
	public int getModuleBitLength() {
		return moduleBitLength;
	}
	public void setModuleBitLength(int moduleBitLength) {
		this.moduleBitLength = moduleBitLength;
	}
	public int getmArrayLength() {
		return mArrayLength;
	}
	public void setmArrayLength(int mArrayLength) {
		this.mArrayLength = mArrayLength;
	}
	public int getcArrayLength() {
		return cArrayLength;
	}
	public void setcArrayLength(int cArrayLength) {
		this.cArrayLength = cArrayLength;
	}
	
	@Override
	public String toString() {
		return "KeyPairs \n[module=" + module + "\n publicKey=" + publicKey
				+ "\n privateKey=" + privateKey + "\n moduleBitLength="
				+ moduleBitLength + "\n mArrayLength=" + mArrayLength
				+ "\n cArrayLength=" + cArrayLength + "]";
	}
}
