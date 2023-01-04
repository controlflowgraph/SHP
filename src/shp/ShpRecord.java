package shp;

import java.awt.*;
import java.util.List;
import java.util.function.UnaryOperator;

public interface ShpRecord
{
    BoundingBox getBounds();
    List<Shape> toShape(UnaryOperator<Point> transformer);
}
