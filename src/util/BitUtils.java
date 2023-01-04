package util;

public class BitUtils
{
    public static byte getBits(byte data, int start, int end)
    {
        return (byte)((data >> start) & ((1 << (end - start)) - 1));
    }
}
