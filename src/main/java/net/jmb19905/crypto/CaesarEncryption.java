package net.jmb19905.crypto;

public class CaesarEncryption {

    public static byte[] encrypt(byte[] in, byte count) {
        byte[] out = new byte[in.length];
        for (int i = 0; i < in.length; i++) {
            out[i] = (byte) (in[i] + count);
        }
        return out;
    }

    public static byte[] decrypt(byte[] in, byte count) {
        return encrypt(in, (byte) -count);
    }

}
