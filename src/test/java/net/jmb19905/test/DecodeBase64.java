package net.jmb19905.test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;

public class DecodeBase64 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Input encoded (Base64): ");
        String s = scanner.next();
        byte[] decoded = Base64.getDecoder().decode(s);
        String asString = new String(decoded, StandardCharsets.UTF_8);
        System.out.println(asString);
        scanner.next();
    }

}
