package main;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import encryption.service.impl.DefaultDecryptionServiceImpl;
import encryption.service.impl.DefaultEncryptionServivceImpl;

public class EncryptionAndDecryptionTest {
	public static Map<String, Integer> saltMap = new HashMap<>();
// Better to store keys at system setup time. it should be one time
//activity. and then cache it in-memory with key and id as primary key.	
	static {
		saltMap.put("qwzxer#@!", 4);
		saltMap.put("vySqwZ87#!*", 7);
		saltMap.put("3sDwQAZPO@!", 3);
		saltMap.put("&#WDCwaz", 8);
	}

	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		if (saltMap.size() == 0) {
			if (args == null || args.length == 0) {
				System.err.println("Please provide slat and number of iterations, like that \"xyz 3\" \"abc 4\" ");
				System.exit(1);
			} else {
				for (String str : args) {
					String[] splitedVal = str.split(" ");
					if (splitedVal.length == 2) {
						saltMap.put(splitedVal[0].trim(), new Integer(splitedVal[1]));
					} else {
						saltMap.put(splitedVal[0].trim(), 3);
					}
				}
			}
			System.out.println("Total number of keys==" + saltMap.size());
		} else {
			long time = System.currentTimeMillis();
			System.out.println("Enceryption start time:" + time);
			List<String> value = new ArrayList<>();
			for (int i = 0; i < 250000; i++) {
				value.add("manzarul07-" + i + "-@gamil.com");
			}
			List<String> encryptedval = new ArrayList<>();
			for (String val : value) {
				String encVal = DefaultEncryptionServivceImpl.encrypt(val);
				System.out.println("Encrypted value for " + val + " ency ==" + encVal);
				encryptedval.add(encVal);
			}
			// Do decryption of all encrypted key
			for (String encval : encryptedval) {
				System.out.println(DefaultDecryptionServiceImpl.decrypt(encval, true));
			}
			System.out.println("Enceryption end time:" + (System.currentTimeMillis()-time));	
		}

	}

}
