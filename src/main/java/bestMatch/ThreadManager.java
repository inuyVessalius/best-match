package bestMatch;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ThreadManager {
    String path, word;
    final static int THREADS_NUMBER = 4;

    ThreadManager(String path, String word) {
        this.path = path;
        this.word = word;
    }

    Word start() {
        try {
            List<List<String>> stringsMatrix = new ArrayList<>();
            List<Thread> threads = new ArrayList<>();
            List<Word> closestWords = new ArrayList<>();
            Path paths = Path.of(Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource(path)).toURI());
            long linesCount = Files.lines(paths).count();
            long subSize = linesCount / THREADS_NUMBER;
            long start = 0;
            long end = subSize;


            for (int i = 1; i <= THREADS_NUMBER; i++) {
                closestWords.add(new Word(0, ""));
                stringsMatrix.add(new ArrayList<>());
                if (i != THREADS_NUMBER) {
                    threads.add(new Thread(new Reader(stringsMatrix.get(i - 1), Files.lines(paths), start, end)));
                } else {
                    threads.add(new Thread(new Reader(stringsMatrix.get(i - 1), Files.lines(paths), start, linesCount)));
                }
                threads.get(i - 1).start();
                start += subSize;
                end += subSize;
            }

            do {
                for (int i = 0; i < threads.size(); i++) {
                    if (!threads.get(i).isAlive() && !threads.get(i).getName().equals("Levenshtein")) {
                        threads.set(i, new Thread(new Levenshtein(stringsMatrix.get(i), word, closestWords.get(i))));
                        threads.get(i).setName("Levenshtein");
                        threads.get(i).start();
                    }
                }
            } while (threads.stream().anyMatch(Thread::isAlive));

            closestWords.sort((p1, p2) -> {
                if (p1.getDistance().equals(p2.getDistance()))
                    return p1.getWord().compareTo(p2.getWord());
                return p1.getDistance() - p2.getDistance();
            });

            closestWords.forEach(word1 -> System.out.println("word 1:"+word1.getWord()));

            return closestWords.get(0);
        } catch (IOException | URISyntaxException e) {
            System.err.println("Couldn't read \"" + path + "\" file.");
            return null;
        }
    }
}