package data;

import dbf.Attribute;

public record Land(@Attribute("*") int id, @Attribute("GEN") String name)
{
}
