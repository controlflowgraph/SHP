package data;

import dbf.Attribute;

public record Segment(@Attribute("*") int id, @Attribute("stadtteil_") String name, @Attribute("SHAPE_Area") double area)
{
}
