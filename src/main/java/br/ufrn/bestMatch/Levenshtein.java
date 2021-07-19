package br.ufrn.bestMatch;

import java.util.List;

public class Levenshtein implements Runnable {
    List<String> words;
    String text;
    Word closestWord;

    public Levenshtein(List<String> words, String text, Word closestWord) {
        this.words = words;
        this.text = text;
        this.closestWord = closestWord;
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

    public void run() {
        Word auxWord = new Word(Integer.MAX_VALUE, words.get(0));
        for (String word : words) {
            Word result = new Word(calculate(word, text), word);

            if (shouldUpdateWord(auxWord, result))
                auxWord = result;
        }

        closestWord.fromWord(auxWord);
    }
}
