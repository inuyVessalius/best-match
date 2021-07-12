package bestMatch;

public record Word(Integer distance, String word) {

    public Integer getDistance() {
        return distance;
    }

    public String getWord() {
        return word;
    }
}