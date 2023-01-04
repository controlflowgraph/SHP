package shp;

public record BoundingBox(double minX, double minY, double minZ, double minM, double maxX, double maxY, double maxZ, double maxM)
{
    public static final BoundingBox MAX = new BoundingBox(
            Double.POSITIVE_INFINITY,
            Double.POSITIVE_INFINITY,
            Double.POSITIVE_INFINITY,
            Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY,
            Double.NEGATIVE_INFINITY,
            Double.NEGATIVE_INFINITY,
            Double.NEGATIVE_INFINITY
    );

    public BoundingBox merge(BoundingBox box)
    {
        return new BoundingBox(
                Math.min(this.minX, box.minX),
                Math.min(this.minY, box.minY),
                Math.min(this.minZ, box.minZ),
                Math.min(this.minM, box.minM),
                Math.max(this.maxX, box.maxX),
                Math.max(this.maxY, box.maxY),
                Math.max(this.maxZ, box.maxZ),
                Math.max(this.maxM, box.maxM)
        );
    }
}
