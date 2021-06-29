package bestMatch;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BestMatchingSequential {
    List<Pair> wordsAndDistance = new ArrayList<>();

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

    public int similarityDistance(String str1, String str2) {
        int[][] matrix = new int[str1.length()][str2.length()];

        for (int i = 0; i < str1.length(); i++)
            for (int j = 0; j < str2.length(); j++)
                matrix[i][j] = -1;

        return levenshtein(matrix, str1, str2, str1.length(), str2.length());
    }

    public ArrayList<String> readDataset(String path) {
        ArrayList<String> data = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource(path)).getPath()));

            String line;
            while ((line = reader.readLine()) != null) {
                data.add(line);
            }
            reader.close();

        } catch (IOException e) {
            System.err.println("Couldn't read \"" + path + "\" file.");
        }

        return data;
    }

    public Pair calculate(String path, String word) {
        ArrayList<String> data = readDataset(path);

        for (String str : data) {
            wordsAndDistance.add(new Pair(similarityDistance(word, str), str));
        }

        wordsAndDistance.sort((p1, p2) -> {
            if (p1.getFirst().equals(p2.getFirst()))
                return p1.getSecond().compareTo(p2.getSecond());
            return p1.getFirst() - p2.getFirst();
        });

        write(word);

        return wordsAndDistance.get(0);
    }

    public void write(String word) {
        try {
            FileWriter fw = new FileWriter("output/" + word + ".csv");
            fw.write("Words;Similarity to \"" + word + "\"\n");

            for (Pair p : wordsAndDistance) {
                String str = p.getSecond() + ";" + p.getFirst().toString() + "\n";
                fw.write(str);
            }

            fw.close();
        } catch (IOException e) {
            System.err.println("Could not write \"output/" + word + ".csv\".");
        }
    }
}