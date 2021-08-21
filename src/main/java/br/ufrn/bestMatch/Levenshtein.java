package br.ufrn.bestMatch;


import java.util.List;
import java.util.concurrent.RecursiveTask;

public class Levenshtein extends RecursiveTask<Word> {
    static final int THRESHOLD = 1000;
    Word closestWord;
    List<String> words;
    String text;

    public Levenshtein(List<String> words, String text) {
        this.words = words;
        this.text = text;
        this.closestWord = new Word(Integer.MAX_VALUE, "");
    }

    public int levenshtein(int[][] matrix, String str1, String str2, int i, int j) {
        if (Math.min(i, j) == 0)
            return Math.max(i, j);

        if (matrix[i - 1][j - 1] != -1)
            return matrix[i - 1][j - 1];

        matrix[i - 1][j - 1] = Math.min(
                Math.min(levenshtein(matrix, str1, str2, i - 1, j) + 1, levenshtein(matrix, str1, str2, i, j - 1) + 1),
                levenshtein(matrix, str1, str2, i - 1, j - 1) + (str1.charAt(i - 1) != str2.charAt(j - 1) ? 1 : 0));

        return matrix[i - 1][j - 1];
    }

    public int calculate(String str1, String str2) {
        int[][] matrix = new int[str1.length()][str2.length()];

        for (int i = 0; i < str1.length(); i++)
            for (int j = 0; j < str2.length(); j++)
                matrix[i][j] = -1;

        return levenshtein(matrix, str1, str2, str1.length(), str2.length());
    }

    public boolean shouldUpdateWord(Word word, Word other) {
        if (word.getDistance().equals(other.getDistance())) {
            return word.getWord().compareTo(other.getWord()) > 0;
        } else return word.getDistance() > other.getDistance();
    }


    @Override
    protected Word compute() {
        Word auxWord = new Word(Integer.MAX_VALUE, words.get(0));
        if (words.size() <= THRESHOLD) {
            for (String word : words) {
                Word result = new Word(calculate(word, text), word);

                if (shouldUpdateWord(auxWord, result))
                    auxWord = result;

            }
            closestWord = auxWord;
        } else {
            Levenshtein left = new Levenshtein(words.subList(0, words.size() / 2), text);
            Levenshtein right = new Levenshtein(words.subList(words.size() / 2, words.size()), text);
            invokeAll(left, right);

            if (shouldUpdateWord(closestWord, left.closestWord))
                closestWord = left.closestWord;

            if (shouldUpdateWord(closestWord, right.closestWord))
                closestWord = right.closestWord;

        }
        return closestWord;
    }
}
