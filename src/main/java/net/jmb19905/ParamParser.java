package net.jmb19905;

import net.jmb19905.util.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;

public class ParamParser {

    public static Parameters parse(String filename) {
        String primeS;
        String baseS;
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            primeS = reader.readLine().toUpperCase().replaceAll(":", "");
            baseS = reader.readLine();
        } catch (IOException e) {
            Logger.warn(e, "Could not read parameter file: " + filename);
            return null;
        }
        BigInteger prime = new BigInteger(primeS, 16);
        BigInteger base = new BigInteger(baseS);
        return new Parameters(prime, base);
    }

    public record Parameters(BigInteger prime, BigInteger base) {}

}
