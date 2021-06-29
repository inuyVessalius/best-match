package bestMatch;

public class Pair {
    private final Integer first;
    private final String second;

    public Pair() {
        this.first = null;
        this.second = null;
    }

    public Pair(Integer first, String second) {
        this.first = first;
        this.second = second;
    }

    public Integer getFirst() {
        return first;
    }

    public String getSecond() {
        return second;
    }
}