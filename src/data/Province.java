package data;

import dbf.Attribute;

public record Province(@Attribute("*") int id, @Attribute("name_en") String name, @Attribute("iso_a2") String code)
{
}
