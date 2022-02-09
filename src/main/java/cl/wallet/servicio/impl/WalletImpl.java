package cl.wallet.servicio.impl;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.Security;
import org.bitcoinj.core.Base58;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;

import cl.wallet.servicio.IWallet;
import cl.wallet.to.Request;
import cl.wallet.to.Response;

@Service
public class WalletImpl implements IWallet {

//	private static Wallet wallet;

	public Response walletGen(Request request) {

		Response response = new Response();
		Security.addProvider(new BouncyCastleProvider());

		try {
//		Generar par de claves ECDSA (Elliptic Curve Digital Signature Algorithm)
			BigInteger privKey = Keys.createEcKeyPair().getPrivateKey();
			BigInteger pubKey = Sign.publicKeyFromPrivate(privKey);

			String bcPub = compressPubKey(pubKey);

			MessageDigest sha = MessageDigest.getInstance("SHA-256");
			byte[] s1 = sha.digest(hexStringToByteArray(bcPub));

			MessageDigest rmd = MessageDigest.getInstance("RipeMD160");
			byte[] r1 = rmd.digest(s1);

			byte[] r2;

			if (!request.getTestNet()) {
//			Agregar un byte de versión 0x00 al comienzo del hash (MainNet), empiezan por “1” o “3”.
				r2 = new byte[r1.length + 1];
				r2[0] = (byte) 0x00;
				for (int i = 0; i < r1.length; i++) {
					r2[i + 1] = r1[i];
				}
			} else {
//			Agregar un byte de versión 0x6F al comienzo del hash (TestNet), empiezan por “m” o “2“.
				r2 = new byte[r1.length + 1];
				r2[0] = (byte) (byte) 0x6F;
				for (int i = 0; i < r1.length; i++) {
					r2[i + 1] = r1[i];
				}
			}
//		Repetir el hash SHA-256 dos veces
			byte[] s2 = sha.digest(r2);
			byte[] s3 = sha.digest(s2);

//		Los primeros 4 bytes del segundo resultado hash se utilizan como la suma de comprobación de la dirección. Está unido a la RIPEMD160Hash Esta es una dirección de Bitcoin de 25 bytes.
			byte[] a1 = new byte[25];
			for (int i = 0; i < r2.length; i++) {
				a1[i] = r2[i];
			}
			for (int i = 0; i < 4; i++) {
				a1[21 + i] = s3[i];
			}

//		Codificación de direcciones con Base58
			String wallet = Base58.encode(a1);

			response.setBigIntegerPrivKey(privKey);
			response.setPrivKey(privKey.toString(16));
			response.setWallet(wallet);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

	private static String compressPubKey(BigInteger pubKey) {
		String pubKeyYPrefix = pubKey.testBit(0) ? "03" : "02";
		String pubKeyHex = pubKey.toString(16);
		String pubKeyX = pubKeyHex.substring(0, 64);
		return pubKeyYPrefix + pubKeyX;
	}

	private static String bytesToHex(byte[] hashInBytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < hashInBytes.length; i++) {
			sb.append(Integer.toString((hashInBytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

	private static byte[] hexStringToByteArray(String s) {
		byte[] b = new byte[s.length() / 2];
		for (int i = 0; i < b.length; i++) {
			int index = i * 2;
			int v = Integer.parseInt(s.substring(index, index + 2), 16);
			b[i] = (byte) v;
		}
		return b;
	}

}
