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
import java.util.ArrayList;
import java.util.List;

@JCStressTest
@Outcome(id = "0", expect = Expect.ACCEPTABLE, desc = "less distance should be 0")
@org.openjdk.jcstress.annotations.State
@org.openjdk.jmh.annotations.State(Scope.Benchmark)
public class ThreadManager {
    private static volatile ThreadManager instance;
    @Param({"big_file.txt"})
    private String path;
    @Param({"gdhnWPTz8EXLidldp0WMXkpcyrKFU6RwVUN9fQWt9kaJ0rhdrYjv4EBoXAfY2bJOyH4trSn7MtNy2QzsMARl0tNY3W12igNKgrWi"})
    String word;
    private final static int THREADS_NUMBER = 8;
    volatile Word closestWords;

    public static ThreadManager getInstance(String path, String word) {
        ThreadManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized (ThreadManager.class) {
            if (instance == null) {
                instance = new ThreadManager(path, word);
            }
            return instance;
        }
    }

    public ThreadManager() {
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public ThreadManager(String path, String word) {
        this.path = path;
        this.word = word;
    }

    @org.openjdk.jmh.annotations.Benchmark
    @Fork(value = 3, warmups = 2)
    public Word start() {
        try {
            List<Thread> threads = new ArrayList<>();
            Path paths = Paths.get(path);
            long linesCount = Files.lines(paths).count();
            long offset = linesCount / THREADS_NUMBER;
            long start = 0;
            long end = offset;

            List<String> lines = Files.readAllLines(paths);
            closestWords = new Word(Integer.MAX_VALUE, lines.get(0));

            for (int i = 1; i <= THREADS_NUMBER; i++) {
                threads.add(new Thread(new Levenshtein(lines.subList((int) start, (int) end - 1), word, closestWords)));
                threads.get(i - 1).start();

                start = offset * i;
                end = offset * (i + 1);
                if (i == THREADS_NUMBER - 1) {
                    end = linesCount;
                }
            }

            do {
            } while (threads.stream().anyMatch(Thread::isAlive));

            return closestWords;
        } catch (IOException e) {
            System.err.println("Couldn't read \"" + path + "\" file.");
            return new Word(0, "");
        }
    }

    @Actor
    public void getDistanceFromSmallFile(I_Result r) {
        setPath("small_file.txt");
        setWord("test");

        r.r1 = start().getDistance();
    }
}