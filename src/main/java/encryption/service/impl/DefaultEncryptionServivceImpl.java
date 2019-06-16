/** */
package encryption.service.impl;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import encryption.service.EncryptionService;
import main.EncryptionAndDecryptionTest;

/**
 * Default data encryption service
 *
 * @author Manzarul
 */
public class DefaultEncryptionServivceImpl implements EncryptionService {
  private static Cipher c;
  private static int size = EncryptionAndDecryptionTest.saltMap.size();
  static {
    try {
      Key key = generateKey();
      c = Cipher.getInstance(ALGORITHM);
      c.init(Cipher.ENCRYPT_MODE, key);
    } catch (Exception e) {
       System.out.println(e.getMessage());
    }
  }

  public DefaultEncryptionServivceImpl() {
   
  }

  public Map<String, Object> encryptData(Map<String, Object> data) throws Exception {
      if (data == null) {
        return data;
      }
      Iterator<Entry<String, Object>> itr = data.entrySet().iterator();
      while (itr.hasNext()) {
        Entry<String, Object> entry = itr.next();
        if (!(entry.getValue() instanceof Map || entry.getValue() instanceof List)
            && null != entry.getValue()) {
          data.put(entry.getKey(), encrypt(entry.getValue() + ""));
        }
      }
    return data;
  }

  public List<Map<String, Object>> encryptData(List<Map<String, Object>> data) throws Exception {
      if (data == null || data.isEmpty()) {
        return data;
      }
      for (Map<String, Object> map : data) {
        encryptData(map);
      }
    return data;
  }


  public String encryptData(String data) throws Exception {
      if (null != data) {
        return encrypt(data);
      } else {
        return data;
      }
  }

  /**
   * this method is used to encrypt the password.
   *
   * @param value String password
   * @param encryption_key
   * @return encrypted password.
   * @throws NoSuchPaddingException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeyException
   * @throws BadPaddingException
   * @throws IllegalBlockSizeException
   * @throws UnsupportedEncodingException
   */
  @SuppressWarnings("restriction")
  public static String encrypt(String value)
      throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		String valueToEnc = null;
		String eValue = value;
		List<String> val = getSaltAndIterationv(eValue);
		int ITERATIONS = Integer.parseInt(val.get(1));
		String encryption_key = val.get(0);
		for (int i = 0; i < ITERATIONS; i++) {
			valueToEnc = encryption_key + eValue;
			byte[] encValue = c.doFinal(valueToEnc.getBytes(StandardCharsets.UTF_8));
			eValue = new sun.misc.BASE64Encoder().encode(encValue);
		}
		return val.get(2)+"~#!"+eValue;
	}

  private static Key generateKey() {
    return new SecretKeySpec(keyValue, ALGORITHM);
  }
  
	public static List<String> getSaltAndIterationv(String value) {
		List<String> val = new ArrayList<>();
		int module = value.hashCode() % size;
		if(module<=0) {
			module =0;
		}
		Object[] keys = EncryptionAndDecryptionTest.saltMap.keySet().toArray();
		String key = (String) keys[module];
		val.add(key);
		val.add(EncryptionAndDecryptionTest.saltMap.get(key)+"");
		val.add(module + "");
		return val;
	}
  
}
