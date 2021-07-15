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
    final static int THREADS_NUMBER = 8;

    ThreadManager(String path, String word) {
        this.path = path;
        this.word = word;
    }

    Word start() {
        try {
            List<Thread> threads = new ArrayList<>();
            List<Word> closestWords = new ArrayList<>();
            Path paths = Path.of(Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource(path)).toURI());
            long linesCount = Files.lines(paths).count();
            long offset = linesCount / THREADS_NUMBER;
            long start = 0;
            long end = offset;

            List<String> lines = Files.readAllLines(paths);

            for (int i = 1; i <= THREADS_NUMBER; i++) {
                closestWords.add(new Word(0, ""));

                threads.add(new Thread(new Levenshtein(lines.subList((int) start, (int) end - 1), word, closestWords.get(i - 1))));
                threads.get(i - 1).start();

                start = offset * i;
                end = offset * (i + 1);
                if (i == THREADS_NUMBER - 1) {
                    end = linesCount;
                }
            }

            do {
            } while (threads.stream().anyMatch(Thread::isAlive));

            closestWords.sort((p1, p2) -> {
                if (p1.getDistance().equals(p2.getDistance()))
                    return p1.getWord().compareTo(p2.getWord());
                return p1.getDistance() - p2.getDistance();
            });

            return closestWords.get(0);
        } catch (IOException | URISyntaxException e) {
            System.err.println("Couldn't read \"" + path + "\" file.");
            return new Word(0, "");
        }
    }
}