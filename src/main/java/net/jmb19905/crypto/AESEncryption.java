package net.jmb19905.crypto;

import net.jmb19905.util.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AESEncryption {

    public static byte[] encrypt(byte[] in, byte[] keyB) {
        try {
            Key key = new SecretKeySpec(keyB, "AES");
            Cipher c = Cipher.getInstance("AES");
            c.init(1, key);
            byte[] encVal = c.doFinal(in);
            return Base64.getEncoder().withoutPadding().encode(encVal);
        } catch (InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                 BadPaddingException var5) {
            Logger.log(var5, "Error encrypting", Logger.Level.ERROR);
        } catch (IllegalArgumentException var6) {
            Logger.log(var6, "Error encrypting! Tried to encrypt without other PublicKey", Logger.Level.WARN);
        }

        return in;
    }

    public static byte[] decrypt(byte[] encryptedData, byte[] keyB) {
        try {
            Key key = new SecretKeySpec(keyB, "AES");
            Cipher c = Cipher.getInstance("AES");
            c.init(2, key);
            byte[] decodedValue = Base64.getDecoder().decode(encryptedData);
            return c.doFinal(decodedValue);
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | IllegalArgumentException | InvalidKeyException var5) {
            Logger.error(var5, "Error decrypting");
        } catch (BadPaddingException var6) {
            Logger.error(var6, "Error decrypting - wrong key");
        }

        return encryptedData;
    }

}
