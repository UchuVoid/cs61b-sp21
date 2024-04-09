package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

/**
 * A client that uses the synthesizer package to replicate a plucked guitar string sound
 */
public class GuitarHero {
    private final String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
    private int numFrequency = 32;
    public static final double CONCERT_A = 440.0;
    public static final double CONCERT_C = CONCERT_A * Math.pow(2, 3.0 / 12.0);

    private double calculateFrequency(int i) {
        return 440 * Math.pow(2, (i - 24) / 12);
    }

    private int calculatePianoKey(char key) {
        char[] keys = keyboard.toCharArray();
        for (int i = 0; i < keys.length; i++) {
            if (key == keys[i]) {
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        /* create two guitar strings, for concert A and C */
        GuitarHero gh = new GuitarHero();
        GuitarString[] stringA = new GuitarString[gh.numFrequency];

        for (int i = 0; i < gh.numFrequency; i++) {
            stringA[i] = new GuitarString(gh.calculateFrequency(i));
        }
        while (true) {
            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int index = gh.calculatePianoKey(key);
                if (index != -1) {
                    stringA[index].pluck();
                } else {
                    continue;
                }
            }
            double sample = 0;
            for (int i = 0; i < gh.numFrequency; i++) {
                sample += stringA[i].sample();
            }

            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            for (int i = 0; i < gh.numFrequency; i++) {
                stringA[i].tic();
            }

        }
    }
}


