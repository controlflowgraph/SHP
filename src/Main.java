import data.Land;
import data.Province;
import data.Segment;
import dbf.DbfFile;
import dbf.DbfReader;
import dbf.ObjectMapper;
import query.Query;
import shp.Point;
import shp.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        provinces();
//        Path location = Path.of("C:\\Users\\admin\\Downloads\\vg2500_geo84\\vg2500_bld.dbf");
//        DbfFile read = DbfReader.read(location, StandardCharsets.ISO_8859_1);
//        new Query<>(ObjectMapper.of(Land.class, read))
//                .with(Land::name, "Baden-Württemberg")
//                .execute()
//                .forEach(System.out::println);
//
//        Path loc = Path.of("C:\\Users\\admin\\Downloads\\Stadtteile_-_Hamburg\\Stadtteile_Hamburg.dbf");
//        DbfFile r = DbfReader.read(loc, StandardCharsets.UTF_8);
//        System.out.println(new Query<>(ObjectMapper.of(Segment.class, r))
//                                   .without(Segment::name, "Neuwerk")
//                                   .execute()
//                                   .size());
    }

    public static void visualizeKnick(String[] args) throws IOException
    {
        ShpFile file = ShpReader.read(Path.of("C:\\Users\\admin\\Downloads\\SH4_BKSH_Knicks_Feldhecken_Baumreihen\\SH4_BKSH_Knicks_Feldhecken_Baumreihen.shp"));
        List<LineShape> shapes = file.getRecordsAs(LineShape.class);

        BoundingBox bounds = shapes.stream()
                .map(LineShape::getBounds)
                .reduce(BoundingBox.MAX, BoundingBox::merge);
        int width = 10000;
        int height = 10000;
        double fx = width / (bounds.maxX() - bounds.minX());
        double fy = -height / (bounds.maxY() - bounds.minY());
        UnaryOperator<Point> transformer = p -> new Point(
                (p.x() - bounds.minX()) * fx,
                (p.y() - bounds.maxY()) * fy
        );

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = image.createGraphics();
        g.setColor(new Color(200, 200, 200));
        List<Shape> res = shapes.stream()
                .map(s -> s.toShape(transformer))
                .flatMap(List::stream)
                .toList();
        for (Shape area : res)
        {
            int v = (int)(255.0 * Math.random());
            g.setColor(new Color(v, v, v));
            g.draw(area);
        }
        g.dispose();
        ImageIO.write(image, "png", new File("result.png"));
    }

    public static void visualize() throws IOException
    {
        DbfFile read = DbfReader.read(Path.of("C:\\Users\\admin\\Downloads\\Stadtteile_-_Hamburg\\Stadtteile_Hamburg.dbf"));
        ShpFile file = ShpReader.read(Path.of("C:\\Users\\admin\\Downloads\\Stadtteile_-_Hamburg\\Stadtteile_Hamburg.shp"));
        System.out.println(read.format());
        List<PolygonShape> base = file.getRecordsAs(PolygonShape.class);

        List<Segment> of = ObjectMapper.of(Segment.class, read)
                .stream()
                .filter(s -> !s.name().equals("Neuwerk"))
                .toList();
        List<PolygonShape> shapes = of.stream()
                .map(s -> base.get(s.id()))
                .toList();
        double maximum = of.stream()
                .mapToDouble(Segment::area)
                .max()
                .getAsDouble();
        double minimum = of.stream()
                .mapToDouble(Segment::area)
                .min()
                .getAsDouble();

        BoundingBox bounds = shapes.stream()
                .map(PolygonShape::getBounds)
                .reduce(BoundingBox.MAX, BoundingBox::merge);
        double ratio = (bounds.maxY() - bounds.minY()) / (bounds.maxX() - bounds.minX());
        double factor = 3000;
        int width = (int)(factor);
        int height = (int) (ratio * factor);
        double fx = width / (bounds.maxX() - bounds.minX());
        double fy = -height / (bounds.maxY() - bounds.minY());
        UnaryOperator<Point> transformer = p -> new Point(
                (p.x() - bounds.minX()) * fx,
                (p.y() - bounds.maxY()) * fy
        );
        List<List<Shape>> areas = shapes.stream()
                .map(p -> p.toShape(transformer))
                .toList();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = image.createGraphics();
        g.setColor(new Color(200, 200, 200));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (int i = 0; i < areas.size(); i++)
        {
            List<Shape> area = areas.get(i);
            int v = (int) (255.0 * ((of.get(i).area() - minimum) / (maximum - minimum)));
            g.setColor(new Color(v, v, v));
            for (Shape shape : area)
            {
                g.fill(shape);
            }
        }

        g.setColor(new Color(100, 0, 100));
        g.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < areas.size(); i++)
        {
            List<Shape> area = areas.get(i);
            for (Shape shape : area)
            {
                g.draw(shape);
            }
        }

        g.setColor(new Color(250, 0, 250));
        g.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));
        for (int i = 0; i < areas.size(); i++)
        {
            Rectangle bounds1 = areas.get(i).get(0).getBounds();
            int px = bounds1.x + bounds1.width / 2;
            int py = bounds1.y + bounds1.height / 2;
            int i1 = SwingUtilities.computeStringWidth(g.getFontMetrics(), of.get(i).name());
            g.drawString(of.get(i).name(), px - i1 / 2, py - 4);
            g.drawLine(px, py, px, py);
        }

        g.dispose();
        ImageIO.write(image, "png", new File("result.png"));
    }
    public static void provinces() throws IOException
    {
        DbfFile dbf = DbfReader.read(Path.of("E:\\Provinces\\ne_10m_admin_1_states_provinces.dbf"));
        ShpFile file = ShpReader.read(Path.of("E:\\Provinces\\ne_10m_admin_1_states_provinces.shp"));
        List<PolygonShape> shapes = file.getRecordsAs(PolygonShape.class);
        List<Province> of = ObjectMapper.of(Province.class, dbf);
//        List<PolygonShape> polygons = new ArrayList<>();
//        for (int i = 0; i < of.size(); i++)
//        {
//            if(of.get(i).code().equals("DE"))
//            {
//                polygons.add(shapes.get(i));
//            }
//        }
        List<PolygonShape> polygons = shapes;

        BoundingBox bounds = polygons.stream()
                .map(PolygonShape::getBounds)
                .reduce(BoundingBox.MAX, BoundingBox::merge);
        double ratio = (bounds.maxY() - bounds.minY()) / (bounds.maxX() - bounds.minX());
        double factor = 3000;
        int width = (int)(factor);
        int height = (int) (ratio * factor);
        double fx = width / (bounds.maxX() - bounds.minX());
        double fy = -height / (bounds.maxY() - bounds.minY());
        UnaryOperator<Point> transformer = p -> new Point(
                (p.x() - bounds.minX()) * fx,
                (p.y() - bounds.maxY()) * fy
        );
        List<Shape> areas = polygons.stream()
                .map(p -> p.toShape(transformer))
                .flatMap(List::stream)
                .toList();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = image.createGraphics();
        g.setColor(new Color(200, 200, 200));
        for (Shape area : areas)
        {
            int v = (int)(255.0 * Math.random());
            g.setColor(new Color(v, v, v));
            g.fill(area);
        }
        g.dispose();
        ImageIO.write(image, "png", new File("result.png"));
    }
}