package shp;

import java.awt.*;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class PolygonShape implements ShpRecord
{
    private final BoundingBox bounds;
    private final Point[][] points;

    public PolygonShape(BoundingBox bounds, Point[][] points)
    {
        this.bounds = bounds;
        this.points = points;
    }

    private List<Area> toAreas(UnaryOperator<Point> transformer)
    {
        List<Polygon> outside = new ArrayList<>();
        List<Polygon> inside = new ArrayList<>();
        split(this.points, outside, inside, transformer);
        List<Area> areas = outside.stream()
                .map(Area::new)
                .toList();
        addHoles(areas, outside, inside);
        return areas;
    }

    private void addHoles(List<Area> areas, List<Polygon> outside, List<Polygon> inside)
    {
        for (Polygon in : inside)
        {
            boolean found = false;
            for (int j = 0; !found && j < outside.size(); j++)
            {
                Polygon out = outside.get(j);
                int[] xpoints = in.xpoints;
                int[] ypoints = in.ypoints;
                for (int i = 0; !found && i < in.npoints; i++)
                {
                    if (out.contains(new java.awt.Point(xpoints[i], ypoints[i])))
                    {
                        areas.get(j).subtract(new Area(in));
                        found = true;
                    }
                }
            }

            if(!found);
//                throw new RuntimeException("Found matching polygon!");
        }
    }

    private void split(Point[][] points, List<Polygon> outside, List<Polygon> inside, UnaryOperator<Point> transformer)
    {
        Polygon[] polygons = new Polygon[points.length];
        boolean[] contained = new boolean[points.length];
        for (int i = 0; i < points.length; i++)
        {
            polygons[i] = toPolygon(points[i], transformer);
        }
        for (int i = 0; i < polygons.length; i++)
        {
            int[] xs = polygons[i].xpoints;
            int[] ys = polygons[i].ypoints;
            boolean found = false;
            for (int k = i + 1; !found && k < polygons.length; k++)
            {
                int cont = 0;
                for (int p = 0; p < xs.length; p++)
                {
                    if(polygons[k].contains(xs[p], ys[p]))
                    {
                        cont++;
                    }
                }
                found = cont == xs.length;
            }
            contained[i] = found;
        }

        for (int i = 0; i < points.length; i++)
        {
            if(contained[i])
            {
                inside.add(polygons[i]);
            }
            else
            {
                outside.add(polygons[i]);
            }
        }
//        for (Point[] point : points)
//        {
//
//            if (isClockwise(point))
//            {
//                outside.add(polygon);
//            }
//            else
//            {
//                inside.add(polygon);
//            }
//        }
    }

    private Polygon toPolygon(Point[] polygon, UnaryOperator<Point> transformer)
    {
        int[] xs = new int[polygon.length];
        int[] ys = new int[polygon.length];
        for (int i = 0; i < polygon.length; i++)
        {
            Point p = transformer.apply(polygon[i]);
            xs[i] = (int) p.x();
            ys[i] = (int) p.y();
        }
        return new Polygon(xs, ys, polygon.length);
    }

    private boolean isClockwise(Point[] polygon)
    {
        double sum = 0.0;
        for (int i = 0; i < polygon.length; i++)
        {
            Point a = polygon[i];
            Point b = polygon[(i + 1) % polygon.length];
            sum += (b.x() - a.x()) * (b.y() * a.y());
        }
        return sum > 0;
    }

    @Override
    public BoundingBox getBounds()
    {
        return this.bounds;
    }

    @Override
    public List<Shape> toShape(UnaryOperator<Point> transformer)
    {
        return toAreas(transformer)
                .stream()
                .map(Shape.class::cast)
                .toList();
    }

    public Point[][] getPoints()
    {
        return this.points;
    }
}
