package dbf;

import java.nio.charset.StandardCharsets;
import java.util.List;

public record DbfFile(List<FieldDescriptor> descriptors, List<List<String>> records)
{
    public List<String> get(int index)
    {
        return this.records.get(index);
    }

    public String format()
    {
        StringBuilder builder = new StringBuilder();
        int[] minimums = new int[this.descriptors.size()];
        for (int i = 0; i < this.descriptors.size(); i++)
        {
            FieldDescriptor descriptor = this.descriptors.get(i);
            String text = new String(descriptor.name().getBytes(StandardCharsets.UTF_8));
            int len = text.toCharArray().length;
            int buffer = Math.max(0, descriptor.length() - len);
            builder.append("| ")
                    .append(" ".repeat(buffer))
                    .append(descriptor.name());
            minimums[i] = len;
        }
        builder.append('|');
        List<List<String>> lists = this.records;
        for (int i = 0; i < lists.size(); i++)
        {
            List<String> rec = lists.get(i);
            builder.append('\n');
            for (int v = 0; v < this.descriptors.size(); v++)
            {
                int maximum = Math.max(minimums[v], this.descriptors.get(v).length());
                String text = new String(rec.get(v).getBytes(StandardCharsets.UTF_8));
                int buffer = maximum - text.toCharArray().length;
                builder.append("| ")
                        .append(" ".repeat(buffer))
                        .append(text);
            }
            builder.append('|');
        }
        return builder.toString();
    }
}
