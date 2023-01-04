package query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class Query<T>
{
    private final List<T> elements;
    private final List<Predicate<T>> conditions = new ArrayList<>();

    public Query(List<T> elements)
    {
        this.elements = elements;
    }

    public Query<T> with(Predicate<T> condition)
    {
        this.conditions.add(condition);
        return this;
    }

    public <V> Query<T> with(Function<T, V> extractor, V value)
    {
        with(e -> Objects.equals(extractor.apply(e), value));
        return this;
    }

    public Query<T> without(Predicate<T> condition)
    {
        this.conditions.add(Predicate.not(condition));
        return this;
    }

    public <V> Query<T> without(Function<T, V> extractor, Predicate<V> check)
    {
        without(e -> check.test(extractor.apply(e)));
        return this;
    }

    public <V> Query<T> without(Function<T, V> extractor, V value)
    {
        without(v -> Objects.equals(extractor.apply(v), value));
        return this;
    }

    public List<T> execute()
    {
        List<T> current = this.elements;
        for (Predicate<T> condition : this.conditions)
        {
            List<T> filtered = new ArrayList<>();
            for (T t : current)
            {
                if(condition.test(t))
                {
                    filtered.add(t);
                }
            }
            current = filtered;
        }
        return current;
    }
}
