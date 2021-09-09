package br.ufrn.bestMatch;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.Serializable;

public class Main implements Serializable {
    public static void run(String word, String dictionary) {
        long start = System.currentTimeMillis();

        Logger.getLogger("org").setLevel(Level.ERROR);
        SparkConf conf = new SparkConf().setAppName("BestMatch").setMaster("local[*]");
        ThreadManager threadManager = new ThreadManager(dictionary, word, new JavaSparkContext(conf));

        Word result = threadManager.start();

        long end = System.currentTimeMillis();

        System.out.println("Palavra com a menor distância: " + result.getWord());

        System.out.println("Com distância de: " + result.getDistance());

        System.out.println("Duration: "
                + ((end - start) / 60000) + "min "
                + (((end - start) % 60000) / 1000) + "."
                + (((end - start) % 60000) % 1000) + "s");
    }

    public static void main(String[] args) {
        run("test", "./big_file.txt");
    }
}