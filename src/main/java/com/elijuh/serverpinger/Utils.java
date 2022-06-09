package com.elijuh.serverpinger;

import lombok.experimental.UtilityClass;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@UtilityClass
class Utils {
    private static final int SEGMENT_BITS = 0x7F;
    private static final int CONTINUE_BIT = 0x80;

    public int readVarInt(DataInputStream in) throws IOException {
        int value = 0, position = 0;
        while (true) {
            byte currentByte = in.readByte();
            value |= (currentByte & SEGMENT_BITS) << position;

            if ((currentByte & CONTINUE_BIT) == 0) break;

            position += 7;

            if (position >= 32) throw new RuntimeException("VarInt is too big");
        }

        return value;
    }

    public void writeVarInt(DataOutputStream out, int value) throws IOException {
        while (true) {
            if ((value & ~SEGMENT_BITS) == 0) {
                out.writeByte(value);
                return;
            }

            out.writeByte((value & SEGMENT_BITS) | CONTINUE_BIT);
            value >>>= 7;
        }
    }
}
