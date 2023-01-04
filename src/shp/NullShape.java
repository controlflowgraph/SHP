package shp;

import java.awt.*;
import java.util.List;
import java.util.function.UnaryOperator;

public class NullShape implements ShpRecord
{
    @Override
    public BoundingBox getBounds()
    {
        return new BoundingBox(0, 0, 0, 0, 0, 0, 0, 0);
    }

    @Override
    public List<Shape> toShape(UnaryOperator<Point> transformer)
    {
        return List.of();
    }
}
