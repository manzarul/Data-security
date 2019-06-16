package encryption.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import encryption.service.DecryptionService;
import main.EncryptionAndDecryptionTest;

public class DefaultDecryptionServiceImpl implements DecryptionService {
	private static String sunbird_encryption = "";

	private static Cipher c;

	static {
		try {
			Key key = generateKey();
			c = Cipher.getInstance(ALGORITHM);
			c.init(Cipher.DECRYPT_MODE, key);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public DefaultDecryptionServiceImpl() {

	}

	public Map<String, Object> decryptData(Map<String, Object> data) {
		if (data == null) {
			return data;
		}
		Iterator<Entry<String, Object>> itr = data.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<String, Object> entry = itr.next();
			if (!(entry.getValue() instanceof Map || entry.getValue() instanceof List) && null != entry.getValue()) {
				data.put(entry.getKey(), decrypt(entry.getValue() + "", false));
			}
		}
		return data;
	}

	public List<Map<String, Object>> decryptData(List<Map<String, Object>> data) {
		if (data == null || data.isEmpty()) {
			return data;
		}

		for (Map<String, Object> map : data) {
			decryptData(map);
		}
		return data;
	}

	public String decryptData(String data) {
		return decryptData(data, false);
	}

	public String decryptData(String data, boolean throwExceptionOnFailure) {
		if (data != null && !"".equals(data.trim())) {
			return data;
		} else {
			return decrypt(data, throwExceptionOnFailure);
		}
	}

	public static String decrypt(String value, boolean throwExceptionOnFailure) {

		String dValue = null;
		try {
			StringTokenizer tokenizer = new StringTokenizer(value, "~#!");
			int count =0;
			int val =0;
			String valueToDecrypt = null;
			while (tokenizer.hasMoreTokens()) {
				if (count==0) {
					val = Integer.parseInt(tokenizer.nextToken());
				} else {
					valueToDecrypt = tokenizer.nextToken();
				}
				++count;
			}
			Object[] keys = EncryptionAndDecryptionTest.saltMap.keySet().toArray();
			String sunbird_encryption = (String) keys[val];
			int ITERATIONS = EncryptionAndDecryptionTest.saltMap.get(sunbird_encryption);
			for (int i = 0; i < ITERATIONS; i++) {
				byte[] decordedValue = new sun.misc.BASE64Decoder().decodeBuffer(valueToDecrypt);
				byte[] decValue = c.doFinal(decordedValue);
				dValue = new String(decValue, StandardCharsets.UTF_8).substring(sunbird_encryption.length());
				valueToDecrypt = dValue;
			}

		} catch (Exception ex) {
			System.out.println("DefaultDecryptionServiceImpl:decrypt: Exception occurred with error message = "+ex.getMessage()+"/n"+value);
		}
		return dValue;
	}

	private static Key generateKey() {
		return new SecretKeySpec(keyValue, ALGORITHM);
	}
}
