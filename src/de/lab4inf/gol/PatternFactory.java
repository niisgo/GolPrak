package de.lab4inf.gol;

public class PatternFactory {
    private static final String[] NAMES = {"Cross", "Glider", "Blinker", "Worst"};

    public static String[] getPatternNames() {
        return NAMES.clone();
    }

    public static boolean[][] getPattern(String name) {
        switch (name) {
            case "Glider":
                return gliderPattern();
            case "Blinker":
                return blinkerPattern();
            case "Worst":
                return worstPattern();
            case "Cross":
            default:
                return crossPattern();
        }
    }

    public static boolean[][] gliderPattern() {
        boolean[][] p = new boolean[3][3];
        p[0][1] = true;
        p[1][2] = true;
        p[2][0] = true; p[2][1] = true; p[2][2] = true;
        return p;
    }

    public static boolean[][] blinkerPattern() {
        return new boolean[][] {{true, true, true}};
    }

    public static boolean[][] crossPattern() {
        return new boolean[][] {{false, true, false}, {true, true, true}, {false, true, false}};
    }

    public static boolean[][] worstPattern() {
        return new boolean[][] {{false, true, false}, {true, false, true}, {true, false, true}, {false, true, false}};
    }
}
