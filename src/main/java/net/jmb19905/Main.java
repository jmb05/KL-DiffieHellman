package net.jmb19905;

import ch.bailu.gtk.type.Str;
import net.jmb19905.crypto.AESEncryption;
import net.jmb19905.crypto.CaesarEncryption;
import net.jmb19905.net.*;
import net.jmb19905.net.event.ActiveEventListener;
import net.jmb19905.net.event.InactiveEventListener;
import net.jmb19905.net.packet.PacketRegistry;
import net.jmb19905.net.tcp.ClientTcpThread;
import net.jmb19905.net.tcp.ServerTcpThread;
import net.jmb19905.packet.*;
import net.jmb19905.util.Logger;
import net.jmb19905.util.ShutdownManager;

import java.math.BigInteger;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class Main {

    public static boolean caesars = false;
    public static String parameterFile = "dhparam.jparam";
    public static BigInteger prime;
    public static BigInteger base;
    public static BigInteger publicKey;
    private static BigInteger secret;
    public static BigInteger shared;
    public static byte modulo;
    public static byte[] hash;
    public static boolean sentHandshake = false;
    private static final AtomicReference<SocketAddress> address = new AtomicReference<>();
    private static NetThread thread;
    public static App app;

    public static void main(String[] args) {
        if (args.length == 0) {
            Logger.error("Specify Type (and Mode) as argument");
            Logger.info("Example: \"startcommand server\", \"startcommand server-simple\", \"startcommand client\"");
            ShutdownManager.shutdown(-1);
        }

        Logger.initLogFile("kldiffiehellman-" + args[0]);

        if (System.getProperty("os.name").equals("Linux")) {
            app = new GtkApp(args[0]);
        } else {
            app = new SwingApp();
        }

        PacketRegistry.getInstance().register(TextPacket.class, new TextPacketHandler());
        PacketRegistry.getInstance().register(ParametersPacket.class, new ParametersPacketHandler());
        PacketRegistry.getInstance().register(HandshakePacket.class, new HandshakePacketHandler());

        Endpoint endpoint;

        switch (args[0].substring(0, 6)) {
            case  "server" -> {
                if (args[0].endsWith("simple")) {
                    caesars = true;
                    parameterFile = "dhparam-simple.jparam";
                    Logger.info("Mode: Simple");
                } else {
                    Logger.info("Mode: Normal");
                }
                endpoint = new Server();
                thread = endpoint.addTcp(38462);
                var param = ParamParser.parse(parameterFile);
                if (param == null) {
                    Logger.fatal("Invalid Parameters: null");
                    ShutdownManager.shutdown(-1);
                    throw new RuntimeException("Reached unreachable code");
                }
                prime = param.prime();
                base = param.base();
                chooseSecret();
                calcPublicKey();
                ((ServerTcpThread) thread).addDefaultEventListener((ActiveEventListener) e -> {
                    Logger.info("Server Channel active...");
                    address.set(e.getContext().remoteAddress());
                    e.getContext().send(ParametersPacket.create(prime, base, caesars));
                    app.appendMessage("Client connected");
                });
                ((ServerTcpThread) thread).addDefaultEventListener((InactiveEventListener) e -> {
                    Logger.info("Server Channel inactive...");
                    address.set(null);
                    app.appendMessage("Client disconnected");
                });
            }
            case  "client" -> {
                endpoint = new Client("localhost");
                thread = endpoint.addTcp(38462);
                ((ClientTcpThread) thread).addEventListener((ActiveEventListener) e -> {
                    Logger.info("Client Channel active...");
                    address.set(e.getContext().remoteAddress());
                    app.appendMessage("Connected to Server");
                });
                ((ClientTcpThread) thread).addEventListener((InactiveEventListener) e -> {
                    Logger.info("Client Channel inactive...");
                    address.set(null);
                    app.appendMessage("Disconnected from Server");
                });
            }
            default -> throw new IllegalStateException("Unexpected value: " + args[0]);
        }
        thread.start();

        app.start(args);
    }

    public static void sendMessage(String text) {
        if (Main.shared != null){
            byte[] b;
            if (Main.caesars) {
                b = CaesarEncryption.encrypt(text.getBytes(StandardCharsets.UTF_8), Main.modulo);
            } else {
                b = AESEncryption.encrypt(text.getBytes(StandardCharsets.UTF_8), Main.hash);
            }
            NetworkingUtility.send(thread, address.get(), TextPacket.create(b));
        }
    }

    public static boolean isDisconnected() {
        return address.get() == null;
    }

    public static void chooseSecret() {
        SecureRandom random = new SecureRandom();
        secret = randomBigInteger(prime, random);
    }

    public static void calcPublicKey() {
        publicKey = base.modPow(secret, prime);
    }

    public static void calcShared(BigInteger otherPublic) {
        shared = otherPublic.modPow(secret, prime);
        Logger.info(" Shared:       " + shared + " Bits: " + shared.bitLength());
        Logger.info(" Other Public: " + otherPublic + " Bits: " + otherPublic.bitLength());
        Logger.info(" Own Public:   " + publicKey + " Bits: " + publicKey.bitLength());
        Logger.info(" Secret:       " + secret + " Bits: " + secret.bitLength());
        Logger.info(" Prime:        " + prime + " Bits: " + prime.bitLength());
        modulo = shared.mod(BigInteger.valueOf(128)).byteValue();
        Logger.info(" Caesar: " + modulo);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            hash = digest.digest(shared.toByteArray());
            String base64hash = Base64.getEncoder().encodeToString(hash);
            Logger.info("Hash: " + base64hash);
        } catch (NoSuchAlgorithmException e) {
            Logger.error(e);
        }
    }

    public static Str s(String string) {
        return new Str(string);
    }

    private static BigInteger randomBigInteger(BigInteger upperLimit, Random random) {
        BigInteger randomNumber;
        do {
            randomNumber = new BigInteger(upperLimit.bitLength(), random);
        } while (randomNumber.compareTo(upperLimit) >= 0);
        return randomNumber;
    }

}