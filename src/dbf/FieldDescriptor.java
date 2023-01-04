package dbf;

public record FieldDescriptor(String name, char type, int length)
{
    @Override
    public String toString()
    {
        return this.name;
    }
}
