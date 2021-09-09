package br.ufrn.bestMatch;

public class    Main {
    public static void run(String word, String dictionary) {
        long start = System.currentTimeMillis();

        ThreadManager threadManager = new ThreadManager(dictionary, word);

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
        run("test", "big_file.txt");
    }
}