package br.ufrn.bestMatch;

import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Expect;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.infra.results.I_Result;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@JCStressTest
@Outcome(id = "0", expect = Expect.ACCEPTABLE, desc = "less distance should be 0")
@org.openjdk.jcstress.annotations.State
@org.openjdk.jmh.annotations.State(Scope.Benchmark)
public class BestMatching {
    @Param({"small_file.txt"})
    private String path;
    @Param({"test"})
    String word;

    public BestMatching() {
    }

    public BestMatching(String path, String word) {
        this.path = path;
        this.word = word;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setWord(String word) {
        this.word = word;
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

    public List<String> read(String path) {

        try {
            Path paths = Paths.get(path);

            return Files.readAllLines(paths);

        } catch (IOException e) {
            System.err.println("Couldn't read \"" + path + "\" file.");
            return null;
        }
    }

    @org.openjdk.jmh.annotations.Benchmark
    @Fork(value = 3, warmups = 2)
    public Word start() {
        List<String> words = read(path);

        Word auxWord = new Word(Integer.MAX_VALUE, words.get(0));
        for (String s : words) {
            Word result = new Word(calculate(s, word), s);

            if (auxWord.getDistance().equals(result.getDistance())) {
                if (auxWord.getWord().compareTo(result.getWord()) > 0)
                    auxWord = result;
            } else if (auxWord.getDistance() > result.getDistance())
                auxWord = result;
        }

        return auxWord;
    }

    @Actor
    public void getDistanceFromSmallFile(I_Result r) {
        setPath("small_file.txt");
        setWord("test");

        r.r1 = start().getDistance();
    }
}