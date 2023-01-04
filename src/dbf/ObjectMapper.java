package dbf;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;

public class ObjectMapper
{
    private record AttributeMapper(int source, Function<String, Object> mapper)
    {

    }

    public static <T> List<T> of(Class<T> cls, DbfFile file)
    {
        try
        {
            return of(cls, file.descriptors(), file.records());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private static final Map<String, Function<String, Object>> converters = Map.of(
            "int", Integer::parseInt,
            "long", Long::parseLong,
            "float", Float::parseFloat,
            "double", Double::parseDouble,
            "String", s -> s
    );

    private static <T> List<T> of(Class<T> cls, List<FieldDescriptor> descriptors, List<List<String>> records) throws InvocationTargetException, InstantiationException, IllegalAccessException
    {
        Constructor<?>[] constructors = cls.getDeclaredConstructors();

        if(constructors.length != 1)
            throw new RuntimeException("Class has to have only one constructor!");

        Map<String, Integer> mapping = getMapping(descriptors);
        List<AttributeMapper> mappers = new ArrayList<>();
        mappers.add(new AttributeMapper(-1, null));
        Parameter[] parameters = constructors[0].getParameters();
        for (int i = 1; i < parameters.length; i++)
        {
            Parameter parameter = parameters[i];
            Attribute annotation = parameter.getAnnotation(Attribute.class);
            String name = annotation.value();
            String type = parameter.getType().getSimpleName();
            Function<String, Object> converter = converters.get(type);
            if(!mapping.containsKey(name))
                throw new RuntimeException("Unknown attribute '" + name + "'!");
            mappers.add(new AttributeMapper(mapping.get(name), converter));
        }

        List<T> lst = new ArrayList<>();
        Object[] args = new Object[parameters.length];
        for (int j = 0; j < records.size(); j++)
        {
            args[0] = j;
            List<String> rec = records.get(j);
            for (int i = 1; i < parameters.length; i++)
            {
                AttributeMapper map = mappers.get(i);
                args[i] = map.mapper().apply(rec.get(map.source()));
            }
            lst.add(cls.cast(constructors[0].newInstance(args)));
        }

        return lst;
    }

    private static Map<String, Integer> getMapping(List<FieldDescriptor> descriptors)
    {
        Map<String, Integer> mapping = new HashMap<>();
        for (int i = 0; i < descriptors.size(); i++)
        {
            mapping.put(descriptors.get(i).name(), i);
        }
        return mapping;
    }
}
