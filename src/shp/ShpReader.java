package shp;

import util.DataStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ShpReader
{
    public static ShpFile read(Path path) throws IOException
    {
        DataStream stream = new DataStream(new FileInputStream(path.toFile()).readAllBytes());
        int magic = stream.nextIntegerBE();
        stream.skip(20);
        int length = stream.nextIntegerBE();
        int version = stream.nextIntegerLE();
        int type = stream.nextIntegerLE();
        BoundingBox bounds = parseXYZM(stream);
        List<ShpRecord> records = new ArrayList<>();
        while(stream.getCurrentIndex() < stream.getLength())
        {
            records.add(parseRecord(stream));
        }
        return new ShpFile(bounds, records);
    }

    private static BoundingBox parseXY(DataStream stream)
    {
        double minX = stream.nextDoubleLE();
        double minY = stream.nextDoubleLE();
        double maxX = stream.nextDoubleLE();
        double maxY = stream.nextDoubleLE();
        return new BoundingBox(minX, minY, 0.0, 0.0, maxX, maxY, 0.0, 0.0);
    }

    private static BoundingBox parseXYZM(DataStream stream)
    {
        double minX = stream.nextDoubleLE();
        double minY = stream.nextDoubleLE();
        double maxX = stream.nextDoubleLE();
        double maxY = stream.nextDoubleLE();
        double minZ = stream.nextDoubleLE();
        double maxZ = stream.nextDoubleLE();
        double minM = stream.nextDoubleLE();
        double maxM = stream.nextDoubleLE();
        return new BoundingBox(minX, minY, minZ, minM, maxX, maxY, maxZ, maxM);
    }

    private static ShpRecord parseRecord(DataStream stream)
    {
        int number = stream.nextIntegerBE();
        int length = stream.nextIntegerBE();
        int type = stream.nextIntegerLE();
        return switch (type)
                {
                    case 0 -> parseNull(stream);
                    case 3 -> parseLine(stream);
                    case 5 -> parsePolygon(stream);
                    default -> throw new RuntimeException("Unknown record type '%s'!".formatted(type));
                };
    }

    private static ShpRecord parseNull(DataStream stream)
    {
        return new NullShape();
    }

    private static ShpRecord parseLine(DataStream stream)
    {
        BoundingBox bounds = parseXY(stream);
        int numParts = stream.nextIntegerLE();
        int numPoints = stream.nextIntegerLE();
        int[] starts = new int[numParts + 1];
        for (int i = 0; i < numParts; i++)
        {
            starts[i] = stream.nextIntegerLE();
        }

        starts[numParts] = numPoints;
        Point[][] points = new Point[numParts][];
        for (int i = 0; i < numParts; i++)
        {
            Point[] ring = new Point[starts[i + 1] - starts[i]];
            for (int j = 0; j < ring.length; j++)
            {
                double x = stream.nextDoubleLE();
                double y = stream.nextDoubleLE();
                ring[j] = new Point(x, y);
            }
            points[i] = ring;
        }
        return new LineShape(bounds, points);
    }
    private static ShpRecord parsePolygon(DataStream stream)
    {
        BoundingBox bounds = parseXY(stream);
        int numParts = stream.nextIntegerLE();
        int numPoints = stream.nextIntegerLE();
        int[] starts = new int[numParts + 1];
        for (int i = 0; i < numParts; i++)
        {
            starts[i] = stream.nextIntegerLE();
        }

        starts[numParts] = numPoints;
        Point[][] points = new Point[numParts][];
        for (int i = 0; i < numParts; i++)
        {
            Point[] ring = new Point[starts[i + 1] - starts[i]];
            for (int j = 0; j < ring.length; j++)
            {
                double x = stream.nextDoubleLE();
                double y = stream.nextDoubleLE();
                ring[j] = new Point(x, y);
            }
            points[i] = ring;
        }
        return new PolygonShape(bounds, points);
    }
}
