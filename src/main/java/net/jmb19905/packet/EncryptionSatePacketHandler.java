package net.jmb19905.packet;

import net.jmb19905.Main;
import net.jmb19905.net.handler.HandlingContext;
import net.jmb19905.net.packet.PacketHandler;
import net.jmb19905.util.Logger;

public class EncryptionSatePacketHandler implements PacketHandler<EncryptionStatePacket> {
    @Override
    public void handle(HandlingContext handlingContext, EncryptionStatePacket encryptionStatePacket) {
        Main.encryptionEnabled = encryptionStatePacket.encryptionState;
        if (Main.encryptionEnabled) {
            Logger.info("Encryption activated (remote)");
            Main.app.appendMessage("Verschlüsselung wurde aktiviert ");
        } else {
            Logger.info("Encryption deactivated (remote)");
            Main.app.appendMessage("Verschlüsselung wurde deaktiviert ");
        }
    }
}
