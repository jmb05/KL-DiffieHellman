package net.jmb19905.packet;

import net.jmb19905.net.buffer.BufferWrapper;
import net.jmb19905.net.packet.Packet;

import java.math.BigInteger;

public class HandshakePacket extends Packet {

    public static final String ID = "handshake";

    public BigInteger publicKey;

    public static HandshakePacket create(BigInteger publicKey) {
        HandshakePacket packet = new HandshakePacket();
        packet.publicKey = publicKey;
        return packet;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void deconstruct(BufferWrapper bufferWrapper) {
        bufferWrapper.putBytes(publicKey.toByteArray());
    }

    @Override
    public void construct(BufferWrapper bufferWrapper) {
        publicKey = new BigInteger(bufferWrapper.getBytes());
    }
}
