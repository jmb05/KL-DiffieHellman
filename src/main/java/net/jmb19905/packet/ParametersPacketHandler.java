package net.jmb19905.packet;

import net.jmb19905.Main;
import net.jmb19905.net.handler.HandlingContext;
import net.jmb19905.net.packet.PacketHandler;
import net.jmb19905.util.Logger;

public class ParametersPacketHandler implements PacketHandler<ParametersPacket> {
    @Override
    public void handle(HandlingContext handlingContext, ParametersPacket packet) {
        Logger.info("Received Parameters");
        Main.prime = packet.prime;
        Main.base = packet.base;
        Main.chooseSecret();
        Main.calcPublicKey();
        handlingContext.send(HandshakePacket.create(Main.publicKey));
        Main.sentHandshake = true;
    }
}
