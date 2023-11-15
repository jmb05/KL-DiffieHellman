package net.jmb19905.packet;

import net.jmb19905.Main;
import net.jmb19905.crypto.AESEncryption;
import net.jmb19905.net.handler.HandlingContext;
import net.jmb19905.net.packet.PacketHandler;
import net.jmb19905.util.Logger;

import java.nio.charset.StandardCharsets;

public class TextPacketHandler implements PacketHandler<TextPacket> {
    @Override
    public void handle(HandlingContext handlingContext, TextPacket textPacket) {
        byte[] text = textPacket.text;
        if (Main.shared != null && Main.encryptionEnabled) {
            byte[] b = AESEncryption.decrypt(text, Main.hash);
            String textStr = new String(b, StandardCharsets.UTF_8);
            Logger.info("Message: " + textStr);
            Main.app.appendMessage("<Anderer> " + textStr);
        } else if (!Main.encryptionEnabled) {
            String textStr = new String(textPacket.text, StandardCharsets.UTF_8);
            Logger.info("Message (unencrypted): " + textStr);
            Main.app.appendMessage("<Anderer> " + textStr);
        }
    }
}
