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
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JCStressTest
@Outcome(id = "0", expect = Expect.ACCEPTABLE, desc = "less distance should be 0")
@org.openjdk.jcstress.annotations.State
@org.openjdk.jmh.annotations.State(Scope.Benchmark)
public class ThreadManager {
    @Param({"small_file.txt"})
    private String path;
    @Param({"test"})
    String word;
    private final static int THREADS_NUMBER = 8;

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
            Path paths = Paths.get("./", path);


            return Files.lines(paths).parallel().map(s ->
                    new Levenshtein(s, word).compute()
            ).sorted((p1, p2) -> {
                if (p1.getDistance().equals(p2.getDistance()))
                    return p1.getWord().compareTo(p2.getWord());
                return p1.getDistance() - p2.getDistance();
            }).collect(Collectors.toList()).get(0);

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