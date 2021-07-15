package bestMatch;

public class Word {
    private Integer distance;
    private String word;

    Word(Integer distance, String word) {
        this.distance = distance;
        this.word = word;
    }

    public void fromWord(Word other) {
        this.distance = other.getDistance();
        this.word = other.getWord();
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Integer getDistance() {
        return distance;
    }

    public String getWord() {
        return word;
    }
}