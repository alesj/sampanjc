package org.openblend.sampanjc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class Main {
    private static final Random random = new Random();

    private List<String> fst;
    private List<String> snd;

    private List<List<Match>> tournament;

    public static void main(String[] args) throws Exception {
        if (args == null || args.length < 3) {
            throw new IllegalArgumentException("Invalid args: " + Arrays.toString(args));
        }

        new Main().go(args);
    }


    private void go(String[] args) throws Exception {
        fst = parse(args[0]);
        snd = parse(args[1]);

        if (fst.size() != snd.size()) {
            throw new IllegalStateException("Diff list size!");
        }

        int N = fst.size();

        if (N % 2 != 0) {
            throw new IllegalArgumentException("List not divisible by 2!");
        }

        int rounds = Integer.parseInt(args[2]);

        tournament = new ArrayList<>();

        int counter = 0;
        while (counter < rounds) {
            List<Match> round = new ArrayList<>();
            Set<Integer> used_fst = new HashSet<>();
            Set<Integer> used_snd = new HashSet<>();

            while (used_fst.size() < N) {
                Pair p1 = findNewPair(counter, N, used_fst, used_snd);
                Pair p2 = findNewPair(counter, N, used_fst, used_snd);
                round.add(new Match(p1, p2));
            }

            tournament.add(round);
            counter++;
        }

        counter = 1;
        for (List<Match> round : tournament) {
            System.out.println(String.format("Round %s ---", counter++));
            for (Match m : round) {
                System.out.println(m);
            }
            System.out.println("----");
        }
    }

    /**
     * This can loop forever, but it should do for now. ;-)
     */
    private Pair findNewPair(int counter, int n, Set<Integer> used_fst, Set<Integer> used_snd) {
        while (true) {
            int r_fst = Math.abs(random.nextInt()) % n; // random fst player
            int r_snd = Math.abs(random.nextInt()) % n; // random snd player
            if (used_fst.contains(r_fst) || used_snd.contains(r_snd)) continue;

            String p_fst = fst.get(r_fst);
            String p_snd = snd.get(r_snd);

            Pair pair = new Pair(p_fst, p_snd);
            if (!checkPreviousPairs(pair, counter, tournament)) continue;

            // add used
            used_fst.add(r_fst);
            used_snd.add(r_snd);

            return pair;
        }
    }

    private static boolean checkPreviousPairs(Pair current, int counter, List<List<Match>> tournament) {
        for (int i = 0; i < counter; i++) {
            for (Match m : tournament.get(i)) {
                if (current.equals(m.fst) || current.equals(m.snd)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static List<String> parse(String path) throws Exception {
        File file = new File(path);
        List<String> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
            return list;
        }
    }

    private static class Pair {
        private String fst;
        private String snd;

        public Pair(String fst, String snd) {
            this.fst = fst;
            this.snd = snd;
        }

        @Override
        public int hashCode() {
            return fst.hashCode() + snd.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            Pair pair = (Pair) obj;
            return fst.equals(pair.fst) && snd.equals(pair.snd);
        }

        @Override
        public String toString() {
            return String.format("%s, %s", fst, snd);
        }
    }

    private static class Match {
        private Pair fst;
        private Pair snd;

        public Match(Pair fst, Pair snd) {
            this.fst = fst;
            this.snd = snd;
        }

        @Override
        public String toString() {
            return String.format("%s : %s", fst, snd);
        }
    }
}
