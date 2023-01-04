package shp;

import java.util.List;
import java.util.Objects;

public class ShpFile
{
    private final BoundingBox bounds;
    private final List<ShpRecord> records;

    public ShpFile(BoundingBox bounds, List<ShpRecord> records)
    {
        this.bounds = bounds;
        this.records = records;
    }

    public BoundingBox getBounds()
    {
        return this.bounds;
    }

    public <T extends ShpRecord> List<T> getOnlyRecordsOf(Class<T> cls)
    {
        return this.records.stream()
                .map(cls::cast)
                .filter(Objects::nonNull)
                .toList();
    }

    public <T extends ShpRecord> List<T> getRecordsAs(Class<T> cls)
    {
        return this.records.stream()
                .map(cls::cast)
                .toList();
    }

    public List<ShpRecord> getRecords()
    {
        return this.records;
    }
}
