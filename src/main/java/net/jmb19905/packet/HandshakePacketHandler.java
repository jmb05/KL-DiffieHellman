package net.jmb19905.packet;

import net.jmb19905.Main;
import net.jmb19905.net.handler.HandlingContext;
import net.jmb19905.net.packet.PacketHandler;
import net.jmb19905.util.Logger;

public class HandshakePacketHandler implements PacketHandler<HandshakePacket> {
    @Override
    public void handle(HandlingContext handlingContext, HandshakePacket handshakePacket) {
        Logger.info("Received Handshake");
        Main.calcShared(handshakePacket.publicKey);
        if (!Main.sentHandshake) {
            handlingContext.send(HandshakePacket.create(Main.publicKey));
            Main.sentHandshake = true;
        }
    }
}
