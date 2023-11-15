package net.jmb19905;

import ch.bailu.gtk.type.Str;
import com.formdev.flatlaf.FlatDarkLaf;
import net.jmb19905.crypto.AESEncryption;
import net.jmb19905.net.*;
import net.jmb19905.net.event.ActiveEventListener;
import net.jmb19905.net.event.ExceptionEventListener;
import net.jmb19905.net.event.InactiveEventListener;
import net.jmb19905.net.packet.PacketRegistry;
import net.jmb19905.net.tcp.ClientTcpThread;
import net.jmb19905.net.tcp.ServerTcpThread;
import net.jmb19905.packet.*;
import net.jmb19905.util.Logger;
import net.jmb19905.util.ShutdownManager;

import javax.swing.*;
import java.math.BigInteger;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class Main {

    public static final String PARAMETER_FILE = "dhparam.jparam";
    public static final int PORT = 38462;
    public static BigInteger prime;
    public static BigInteger base;
    public static BigInteger publicKey;
    private static BigInteger secret;
    public static BigInteger shared;
    public static byte modulo;
    public static byte[] hash;
    public static boolean sentHandshake = false;
    public static boolean encryptionEnabled = true;
    private static final AtomicReference<SocketAddress> address = new AtomicReference<>();
    private static NetThread thread;
    public static App app;

    public static void main(String[] args) {
        FlatDarkLaf.setup();
        int option = JOptionPane.showOptionDialog(null,
                "Möchten sie einen Server oder einen Klient starten?", "",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                new String[]{"Server", "Klient"}, "Server");
        String signature = switch (option) {
            case JOptionPane.NO_OPTION -> "client";
            case JOptionPane.CANCEL_OPTION | JOptionPane.CLOSED_OPTION -> {
                ShutdownManager.shutdown(0);
                throw new IllegalStateException("Reached unreachable code");
            }
            default -> "server";
        };

        Logger.initLogFile("kldiffiehellman-" + signature);

        if (System.getProperty("os.name").equals("Linux")) {
            app = new GtkApp(signature);
        } else {
            app = new SwingApp();
        }

        PacketRegistry.getInstance().register(TextPacket.class, new TextPacketHandler());
        PacketRegistry.getInstance().register(ParametersPacket.class, new ParametersPacketHandler());
        PacketRegistry.getInstance().register(HandshakePacket.class, new HandshakePacketHandler());
        PacketRegistry.getInstance().register(EncryptionStatePacket.class, new EncryptionSatePacketHandler());

        Endpoint endpoint;

        switch (signature) {
            case  "server" -> {
                endpoint = new Server();
                thread = endpoint.addTcp(PORT);
                var param = ParamParser.parse(PARAMETER_FILE);
                if (param == null) {
                    Logger.fatal("Invalid Parameters: null");
                    ShutdownManager.shutdown(-1);
                    throw new RuntimeException("Reached unreachable code");
                }
                prime = param.prime();
                base = param.base();
                chooseSecret();
                calcPublicKey();
                app.appendMessage("Warten auf Klient...");
                ((ServerTcpThread) thread).addDefaultEventListener((ActiveEventListener) e -> {
                    Logger.info("Server Channel active...");
                    address.set(e.getContext().remoteAddress());
                    e.getContext().send(ParametersPacket.create(prime, base));
                    app.appendMessage("Mit Klient verbunden");
                });
                ((ServerTcpThread) thread).addDefaultEventListener((InactiveEventListener) e -> {
                    Logger.info("Server Channel inactive...");
                    address.set(null);
                    app.appendMessage("Von Klient getrennt");
                });
            }
            case  "client" -> {
                String addressStr = JOptionPane.showInputDialog("Serveradresse eingeben: ", "localhost");
                endpoint = new Client(addressStr);
                thread = endpoint.addTcp(PORT);
                ((ClientTcpThread) thread).addEventListener((ActiveEventListener) e -> {
                    Logger.info("Client Channel active...");
                    address.set(e.getContext().remoteAddress());
                    app.appendMessage("Mit Server verbunden");
                });
                ((ClientTcpThread) thread).addEventListener((InactiveEventListener) e -> {
                    Logger.info("Client Channel inactive...");
                    address.set(null);
                    app.appendMessage("Von Server getrennt");
                    try {
                        Thread.sleep(20);
                        ShutdownManager.shutdown(0);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                ((ClientTcpThread) thread).addEventListener((ExceptionEventListener) e -> {
                    if (e.getCause().getMessage().contains("Connection refused")) {
                        JOptionPane.showMessageDialog(null, "Fehler: Verbindung fehlgeschlagen", "", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Fehler: Unbekannt", "", JOptionPane.ERROR_MESSAGE);
                    }
                    ShutdownManager.shutdown(1);
                });
            }
            default -> throw new IllegalStateException("Unexpected value: " + signature);
        }
        thread.start();
        app.start(args);
    }

    public static void sendMessage(String text) {
        if (Main.shared != null && encryptionEnabled){
            byte[] b = AESEncryption.encrypt(text.getBytes(StandardCharsets.UTF_8), Main.hash);
            NetworkingUtility.send(thread, address.get(), TextPacket.create(b));
        } else if (!encryptionEnabled) {
            NetworkingUtility.send(thread, address.get(), TextPacket.create(text.getBytes(StandardCharsets.UTF_8)));
        }
    }

    public static void sendEncryptionState(boolean state) {
        NetworkingUtility.send(thread, address.get(), EncryptionStatePacket.create(state));
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
        modulo = shared.mod(BigInteger.valueOf(128)).byteValue();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            hash = digest.digest(shared.toByteArray());
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