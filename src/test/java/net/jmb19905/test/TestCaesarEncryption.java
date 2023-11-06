package net.jmb19905.test;

import net.jmb19905.crypto.CaesarEncryption;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class TestCaesarEncryption {

    @Test
    void test() {
        byte[] testArray = {120, 5};
        System.out.println(Arrays.toString(CaesarEncryption.encrypt(testArray, (byte) 10)));
        byte b1 = 120;
        byte b2 = 10;
        System.out.println((byte) (b1 + b2));
        String test = "Helloü";
        System.out.println(Arrays.toString(test.getBytes(StandardCharsets.UTF_8)));
        var enc = CaesarEncryption.encrypt(test.getBytes(StandardCharsets.UTF_8), (byte) 98);
        System.out.println(Arrays.toString(enc));
        System.out.println(new String(enc));
        var dec = CaesarEncryption.decrypt(enc, (byte) 98);
        System.out.println(Arrays.toString(dec));
        System.out.println(new String(dec, StandardCharsets.UTF_8));
    }

}
