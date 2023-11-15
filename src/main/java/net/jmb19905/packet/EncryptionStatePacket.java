package net.jmb19905.packet;

import net.jmb19905.net.buffer.BufferWrapper;
import net.jmb19905.net.packet.Packet;

public class EncryptionStatePacket extends Packet {

    public static final String ID = "encryption-state";

    public boolean encryptionState = true;

    public static EncryptionStatePacket create(boolean state) {
        EncryptionStatePacket packet = new EncryptionStatePacket();
        packet.encryptionState = state;
        return packet;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void deconstruct(BufferWrapper bufferWrapper) {
        bufferWrapper.putBoolean(encryptionState);
    }

    @Override
    public void construct(BufferWrapper bufferWrapper) {
        encryptionState = bufferWrapper.getBoolean();
    }
}
