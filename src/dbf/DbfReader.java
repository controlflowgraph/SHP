package dbf;

import util.DataStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static util.BitUtils.getBits;

public class DbfReader
{
    public static DbfFile read(Path path) throws IOException
    {
        return read(path, StandardCharsets.ISO_8859_1);
    }

    public static DbfFile read(Path path, Charset charset) throws IOException
    {
        DataStream stream = new DataStream(new FileInputStream(path.toFile()).readAllBytes());
        byte flags = stream.nextByte();
        byte version = getBits(flags, 0, 2);
        int year = stream.nextByte() + 1900;
        int month = stream.nextByte();
        int day = stream.nextByte();
        String lastModified = year + "-" + month + "-" + day;
        int recordCount = stream.nextIntegerLE();
        short headerLength = stream.nextShortLE();
        short recordLength = stream.nextShortLE();
        stream.skip(2);
        byte transaction = stream.nextByte();
        byte encryption = stream.nextByte();
        stream.skip(12);
        byte mdx = stream.nextByte();
        byte driver = stream.nextByte();
        stream.skip(2);
        List<FieldDescriptor> descriptors = new ArrayList<>();
        descriptors.add(new FieldDescriptor("*", 'C', 1));
        int descriptorCount = (headerLength - 32) / 32;
        for (int i = 0; i < descriptorCount; i++)
        {
            String name = stream.nextBytesAsString(11).trim();
            char type = (char) stream.nextByte();
            stream.skip(4);
            int length = stream.nextByteAsInt();
            byte count = stream.nextByte();
            short area = stream.nextShortLE();
            byte example = stream.nextByte();
            stream.skip(10);
            byte mdxPresent = stream.nextByte();
            descriptors.add(new FieldDescriptor(name, type, length));
        }
        stream.skip(1); // skip 0x0d
        List<List<String>> records = new ArrayList<>();
        for (int i = 0; i < recordCount; i++)
        {
            List<String> values = new ArrayList<>();
            for (FieldDescriptor descriptor : descriptors)
            {
                byte[] str = stream.nextBytes(descriptor.length());
                String s = new String(str, charset);
                String s1 = new String(s.getBytes(StandardCharsets.UTF_8));
                values.add(s1.trim());
            }
            records.add(values);
        }
        stream.skip(1); // skip 0x0a

        return new DbfFile(descriptors, records);
    }
}
