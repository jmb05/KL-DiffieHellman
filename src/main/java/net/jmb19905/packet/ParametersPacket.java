package net.jmb19905.packet;

import net.jmb19905.net.buffer.BufferWrapper;
import net.jmb19905.net.packet.Packet;

import java.math.BigInteger;

public class ParametersPacket extends Packet {

    public static final String ID = "parameters";

    public BigInteger prime;
    public BigInteger base;
    public boolean caesars;

    public static ParametersPacket create(BigInteger prime, BigInteger base, boolean caesars) {
        ParametersPacket packet = new ParametersPacket();
        packet.prime = prime;
        packet.base = base;
        packet.caesars = caesars;
        return packet;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void deconstruct(BufferWrapper bufferWrapper) {
        bufferWrapper.putBytes(prime.toByteArray());
        bufferWrapper.putBytes(base.toByteArray());
        bufferWrapper.putBoolean(caesars);
    }

    @Override
    public void construct(BufferWrapper bufferWrapper) {
        prime = new BigInteger(bufferWrapper.getBytes());
        base = new BigInteger(bufferWrapper.getBytes());
        caesars = bufferWrapper.getBoolean();
    }
}
