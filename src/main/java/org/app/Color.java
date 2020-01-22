package org.app;

public class Color {


    public static final String ANSI_RESET = "\u001B[0m";

    // Regular Colors
    public static final String BLACK = "\u001b[30m";   // BLACK
    public static final String RED = "\u001b[31m";     // RED
    public static final String GREEN = "\u001b[32m";   // GREEN
    public static final String YELLOW = "\u001b[33m";  // YELLOW
    public static final String BLUE = "\u001b[34m";    // BLUE
    public static final String PURPLE = "\u001b[35m";  // PURPLE
    public static final String CYAN = "\u001b[36m";    // CYAN
    public static final String WHITE = "\u001b[37m";   // WHITE

    // Bold
    public static final String BLACK_BOLD = "\u001b[1;30m";  // BLACK
    public static final String RED_BOLD = "\u001b[1;31m";    // RED
    public static final String GREEN_BOLD = "\u001b[1;32m";  // GREEN
    public static final String YELLOW_BOLD = "\u001b[1;33m"; // YELLOW
    public static final String BLUE_BOLD = "\u001b[1;34m";   // BLUE
    public static final String PURPLE_BOLD = "\u001b[1;35m"; // PURPLE
    public static final String CYAN_BOLD = "\u001b[1;36m";   // CYAN
    public static final String WHITE_BOLD = "\u001b[1;37m";  // WHITE

    // Underline
    public static final String BLACK_UNDERLINED = "\u001b[4;30m";  // BLACK
    public static final String RED_UNDERLINED = "\u001b[4;31m";    // RED
    public static final String GREEN_UNDERLINED = "\u001b[4;32m";  // GREEN
    public static final String YELLOW_UNDERLINED = "\u001b[4;33m"; // YELLOW
    public static final String BLUE_UNDERLINED = "\u001b[4;34m";   // BLUE
    public static final String PURPLE_UNDERLINED = "\u001b[4;35m"; // PURPLE
    public static final String CYAN_UNDERLINED = "\u001b[4;36m";   // CYAN
    public static final String WHITE_UNDERLINED = "\u001b[4;37m";  // WHITE

    // Background
    public static final String BLACK_BACKGROUND = "\u001b[40m";  // BLACK
    public static final String RED_BACKGROUND = "\u001b[41m";    // RED
    public static final String GREEN_BACKGROUND = "\u001b[42m";  // GREEN
    public static final String YELLOW_BACKGROUND = "\u001b[43m"; // YELLOW
    public static final String BLUE_BACKGROUND = "\u001b[44m";   // BLUE
    public static final String PURPLE_BACKGROUND = "\u001b[45m"; // PURPLE
    public static final String CYAN_BACKGROUND = "\u001b[46m";   // CYAN
    public static final String WHITE_BACKGROUND = "\u001b[47m";  // WHITE

    // High Intensity
    public static final String BLACK_BRIGHT = "\u001b[90m";  // BLACK
    public static final String RED_BRIGHT = "\u001b[91m";    // RED
    public static final String GREEN_BRIGHT = "\u001b[92m";  // GREEN
    public static final String YELLOW_BRIGHT = "\u001b[93m"; // YELLOW
    public static final String BLUE_BRIGHT = "\u001b[94m";   // BLUE
    public static final String PURPLE_BRIGHT = "\u001b[95m"; // PURPLE
    public static final String CYAN_BRIGHT = "\u001b[96m";   // CYAN
    public static final String WHITE_BRIGHT = "\u001b[97m";  // WHITE

    // Bold High Intensity
    public static final String BLACK_BOLD_BRIGHT = "\u001b[1;90m"; // BLACK
    public static final String RED_BOLD_BRIGHT = "\u001b[1;91m";   // RED
    public static final String GREEN_BOLD_BRIGHT = "\u001b[1;92m"; // GREEN
    public static final String YELLOW_BOLD_BRIGHT = "\u001b[1;93m";// YELLOW
    public static final String BLUE_BOLD_BRIGHT = "\u001b[1;94m";  // BLUE
    public static final String PURPLE_BOLD_BRIGHT = "\u001b[1;95m";// PURPLE
    public static final String CYAN_BOLD_BRIGHT = "\u001b[1;96m";  // CYAN
    public static final String WHITE_BOLD_BRIGHT = "\u001b[1;97m"; // WHITE

    // High Intensity backgrounds
    public static final String BLACK_BACKGROUND_BRIGHT = "\u001b[100m";// BLACK
    public static final String RED_BACKGROUND_BRIGHT = "\u001b[101m";// RED
    public static final String GREEN_BACKGROUND_BRIGHT = "\u001b[102m";// GREEN
    public static final String YELLOW_BACKGROUND_BRIGHT = "\u001b[103m";// YELLOW
    public static final String BLUE_BACKGROUND_BRIGHT = "\u001b[104m";// BLUE
    public static final String PURPLE_BACKGROUND_BRIGHT = "\u001b[105m"; // PURPLE
    public static final String CYAN_BACKGROUND_BRIGHT = "\u001b[106m";  // CYAN
    public static final String WHITE_BACKGROUND_BRIGHT = "\u001b[107m";   // WHITE

    //exempel

    //System.out.println(ANSI_RED + "This text is red!" + ANSI_RESET);

   public static void colorExample(){
       System.out.println(RED_BACKGROUND_BRIGHT + "This text has a green background but default text!" + ANSI_RESET);
       System.out.println(GREEN + "This text has red text but a default background!" + ANSI_RESET);
       System.out.println(YELLOW_BACKGROUND_BRIGHT + RED + "This text has a green background and red text!" + ANSI_RESET);
    }


    public static String printArtistColor(String artist){
       return Color.GREEN_BOLD_BRIGHT + artist + Color.ANSI_RESET;
    }
    public static String printAlbumColor(String album){
        return Color.YELLOW_BOLD_BRIGHT + album + Color.ANSI_RESET;
    }
    public static String printSongColor(String song){
        return Color.BLUE_BOLD_BRIGHT + song + Color.ANSI_RESET;
    }

}
