package util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class DataStream
{
    private final byte[] data;
    private int index;

    public DataStream(byte[] data)
    {
        this(data, 0);
    }

    public DataStream(byte[] data, int offset)
    {
        this.data = data;
        this.index = offset;
    }

    public void skip(int bytes)
    {
        this.index += bytes;
    }

    public int getLength()
    {
        return this.data.length;
    }

    public int getCurrentIndex()
    {
        return this.index;
    }

    public byte nextByte()
    {
        return this.data[this.index++];
    }

    public byte peekByte()
    {
        return this.data[this.index];
    }

    public byte[] nextBytes(int length)
    {
        byte[] bytes = new byte[length];
        for(int i = 0; i < length; i++)
        {
            bytes[i] = nextByte();
        }
        return bytes;
    }
    public String nextBytesAsString(int length)
    {
        return nextBytesAsString(length, StandardCharsets.UTF_8);
    }

    public String nextBytesAsString(int length, Charset charset)
    {
        return new String(nextBytes(length), charset);
    }

    public long nextByteAsLong()
    {
        return Byte.toUnsignedLong(this.data[this.index++]);
    }

    public int nextByteAsInt()
    {
        return Byte.toUnsignedInt(this.data[this.index++]);
    }

    public short nextShortLE()
    {
        return (short)(nextByteAsInt() | nextByteAsInt() << 8);
    }

    public short nextShortBE()
    {
        return (short)(nextByteAsInt() << 8 | nextByteAsInt());
    }

    public double nextDoubleBE()
    {
        long l = 0;
        for(int i = 0; i < 8; i++)
        {
            l |= nextByteAsLong() << (56 - (i * 8));
        }
        return Double.longBitsToDouble(l);
    }

    public double nextDoubleLE()
    {
        long l = 0;
        for(int i = 0; i < 8; i++)
        {
            l |= nextByteAsLong() << (i * 8);
        }
        return Double.longBitsToDouble(l);
    }

    public int nextIntegerBE()
    {
        return nextByteAsInt() << 24 | nextByteAsInt() << 16 | nextByteAsInt() << 8 | nextByteAsInt();
    }

    public int nextIntegerLE()
    {
        return nextByteAsInt() | nextByteAsInt() << 8 | nextByteAsInt() << 16 | nextByteAsInt() << 24;
    }
}
