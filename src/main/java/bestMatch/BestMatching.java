package bestMatch;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BestMatching {
    List<Word> wordsAndDistance = new ArrayList<>();

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

    public ConcurrentLinkedQueue<String> read(String path) {
        ConcurrentLinkedQueue<String> data = new ConcurrentLinkedQueue<>();

        try {
            Path paths = Path.of(Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource(path)).toURI());

            long linesCount = Files.lines(paths).count();

            long subSize = linesCount / 8;
            List<Thread> threads = new ArrayList<>();

            long start = 0;
            long end = subSize;

            for (int i = 1; i <= 8; i++) {
                if (i != 8) {
                    threads.add(new Thread(new Reader(data, Files.lines(paths), start, end)));
                    threads.get(i - 1).start();
                    start = subSize;
                    subSize *= i + 1;
                } else {
                    threads.add(new Thread(new Reader(data, Files.lines(paths), start, linesCount)));
                }
            }

            while (true) {
                if (threads.get(0).isAlive())
                    break;
            }


        } catch (IOException | URISyntaxException e) {
            System.err.println("Couldn't read \"" + path + "\" file.");
        }

        return data;
    }

    public Word start(String path, String word) {
        long start = System.currentTimeMillis();
        ConcurrentLinkedQueue<String> data = read(path);
        long end = System.currentTimeMillis();

        System.out.println("Duration: "
                + ((end - start) / 60000) + "min "
                + (((end - start) % 60000) / 1000) + "."
                + (((end - start) % 60000) % 1000) + "s");

//        for (String str : data) {
//            wordsAndDistance.add(new Word(calculate(word, str), str));
//        }
//
//        wordsAndDistance.sort((p1, p2) -> {
//            if (p1.getDistance().equals(p2.getDistance()))
//                return p1.getWord().compareTo(p2.getWord());
//            return p1.getDistance() - p2.getDistance();
//        });
//
//        write(word);
//
//        return wordsAndDistance.get(0);
        return new Word(1, "s");
    }

    public void write(String word) {
        try {
            FileWriter fw = new FileWriter(word + ".csv");
            fw.write("Words;Similarity to \"" + word + "\"\n");

            for (Word p : wordsAndDistance) {
                String str = p.getWord() + ";" + p.getDistance().toString() + "\n";
                fw.write(str);
            }

            fw.close();
        } catch (IOException e) {
            System.err.println("Could not write \"output/" + word + ".csv\".");
        }
    }
}