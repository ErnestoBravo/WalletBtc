package cl.wallet.servicio.impl;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECPoint;

import org.bitcoinj.core.Base58;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;

import cl.wallet.servicio.IWallet;
import cl.wallet.to.Response;

@Service
public class WalletImpl implements IWallet{
	
	public Response walletGen() {
		
		Response response= new Response();
		
		try {
//			Generar par de claves ECDSA
//			Creacion de algoritmosKeyPairGenerator
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
			
//			La curva eliptica especificada es secp256k1 (utilizada por Bitcoin)
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
			keyGen.initialize(ecSpec);
		
			KeyPair kp = keyGen.generateKeyPair();
			PublicKey pub = kp.getPublic();
			PrivateKey pvt = kp.getPrivate();
						
//			Clave privada de ECDSA
			ECPrivateKey epvt = (ECPrivateKey)pvt;
			String sepvt = adjustTo64(epvt.getS().toString(16)).toUpperCase();
									
//			Clave publica de ECDSA
			ECPublicKey epub = (ECPublicKey)pub;
			ECPoint pt = epub.getW();
			String sx = adjustTo64(pt.getAffineX().toString(16)).toUpperCase();
			String sy = adjustTo64(pt.getAffineY().toString(16)).toUpperCase();
			String bcPub = "04" + sx + sy;
			
//			Hash SHA-256 y RIPEMD-160
			MessageDigest sha = MessageDigest.getInstance("SHA-256");
			byte[] s1 = sha.digest(bcPub.getBytes("UTF-8"));
						
			Security.addProvider(new BouncyCastleProvider());
			MessageDigest rmd = MessageDigest.getInstance("RipeMD160", "BC");
			byte[] r1 = rmd.digest(s1);
			
			
//			Agregar un byte de versión 0x00 al comienzo del hash (MainNet)
			byte[] r2 = new byte[r1.length + 1];
			r2[0] = 0;
			for (int i = 0 ; i < r1.length ; i++) {
				r2[i+1] = r1[i];
			}
			
//			Agregar un byte de versión 0x6F al comienzo del hash (TestNet)
//			byte[] r2 = new byte[r1.length + 1];
//			r2[0] = 0;
//			for (int i = 0 ; i < r1.length ; i++) {
//				r2[i+1] = r1[i];
//			}
			
//			Repetir el hash SHA-256 dos veces
			byte[] s2 = sha.digest(r2);
			byte[] s3 = sha.digest(s2);
			
			
//			Los primeros 4 bytes del segundo resultado hash se utilizan como la suma de comprobación de la dirección. Está unido a la RIPEMD160Hash Esta es una dirección de Bitcoin de 25 bytes.
			byte[] a1 = new byte[25];
			for (int i = 0 ; i < r2.length ; i++) {
				a1[i] = r2[i];
			}
			for (int i = 0 ; i < 5 ; i++) {
				a1[20 + i] = s3[i];
			}
			
//			Codificación de direcciones con Base58
			String wallet = Base58.encode(a1);
			
			response.setHash(sepvt);
			response.setWallet(wallet);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}
	
	static private String adjustTo64(String s) {
	    switch(s.length()) {
	    case 62: return "00" + s;
	    case 63: return "0" + s;
	    case 64: return s;
	    default:
	        throw new IllegalArgumentException("not a valid key: " + s);
	    }
	}
		
}
