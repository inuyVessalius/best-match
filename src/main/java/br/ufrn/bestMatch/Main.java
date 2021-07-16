package br.ufrn.bestMatch;

public class Main {
    private static final String DICTIONARY = "big_file.txt";

    public static void run(String word) {
        long start = System.currentTimeMillis();

        ThreadManager threadManager = new ThreadManager(DICTIONARY, word);

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
        final String word = "test";
        run(word);
    }
}