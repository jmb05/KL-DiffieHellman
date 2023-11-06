package net.jmb19905.packet;

import net.jmb19905.net.buffer.BufferWrapper;
import net.jmb19905.net.packet.Packet;

public class TextPacket extends Packet {

    public static final String ID = "text";

    public byte[] text;

    public static TextPacket create(byte[] text) {
        TextPacket packet = new TextPacket();
        packet.text = text;
        return packet;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void deconstruct(BufferWrapper bufferWrapper) {
        bufferWrapper.putBytes(text);
    }

    @Override
    public void construct(BufferWrapper bufferWrapper) {
        text = bufferWrapper.getBytes();
    }
}
