package bestMatch;

import java.util.List;
import java.util.stream.Stream;

public class Reader implements Runnable {
    List<String> data;
    Stream<String> stream;
    long start, end;

    public Reader(List<String> data, Stream<String> stream, long start, long end) {
        this.data = data;
        this.stream = stream;
        this.start = start;
        this.end = end;
    }

    public void run() {
        stream.skip(start).limit(end).forEach(s -> data.add(s));
        stream.close();
    }
}
