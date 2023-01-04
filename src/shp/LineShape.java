package shp;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class LineShape implements ShpRecord
{
    private final BoundingBox bounds;
    private final Point[][] points;

    public LineShape(BoundingBox bounds, Point[][] points)
    {
        this.bounds = bounds;
        this.points = points;
    }

    @Override
    public BoundingBox getBounds()
    {
        return this.bounds;
    }

    @Override
    public List<Shape> toShape(UnaryOperator<Point> transformer)
    {
        List<Shape> drawables = new ArrayList<>();
        for (Point[] point : this.points)
        {
            Point last = transformer.apply(point[0]);
            for (int i = 1; i < point.length; i++)
            {
                Point current = transformer.apply(point[i]);
                drawables.add(new Line2D.Double(last.x(), last.y(), current.x(), current.y()));
                last = current;
            }
        }
        return drawables;
    }

    public Point[][] getPoints()
    {
        return this.points;
    }
}
