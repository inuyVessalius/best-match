package br.ufrn.bestMatch;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Expect;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.infra.results.I_Result;
import org.openjdk.jmh.annotations.*;

import java.io.Serializable;


@JCStressTest
@Outcome(id = "0", expect = Expect.ACCEPTABLE, desc = "less distance should be 0")
@org.openjdk.jcstress.annotations.State
@org.openjdk.jmh.annotations.State(Scope.Benchmark)
public class ThreadManager implements Serializable {
    @Param({"small_file.txt"})
    private String path;
    @Param({"test"})
    String word;
    private static JavaSparkContext sc;

    @Setup
    public void setup() {
        SparkConf conf = new SparkConf().setAppName("BestMatch").setMaster("local[*]");
        sc = new JavaSparkContext(conf);
    }

    @TearDown
    public void tearDown() {
        sc.close();
    }

    public ThreadManager() {
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public ThreadManager(String path, String word, JavaSparkContext context) {
        this.path = path;
        this.word = word;
        sc = context;
    }

    @org.openjdk.jmh.annotations.Benchmark
    @Fork(value = 3, warmups = 2)
    public Word start() {
        Logger.getLogger("org").setLevel(Level.ERROR);
        JavaRDD<String> lines = sc.textFile(path);
        JavaRDD<Word> words = lines.map(line -> new Levenshtein(line, word).compute());


        return words.reduce((p1, p2) -> {
            if (p1.getDistance().equals(p2.getDistance()))
                if (p1.getWord().compareTo(p2.getWord()) < 0)
                    return p1;
                else
                    return p2;
            if (p1.getDistance() - p2.getDistance() < 0)
                return p1;
            else
                return p2;
        });

    }

    @Actor
    public void getDistanceFromSmallFile(I_Result r) {
        setPath("small_file.txt");
        setWord("test");

        r.r1 = start().getDistance();
    }
}