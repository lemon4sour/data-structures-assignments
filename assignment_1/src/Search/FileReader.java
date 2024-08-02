package Search;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public abstract class FileReader {

    static String DELIMITERS = "[-+=" +
            " " +        //space
            "\r\n " +    //carriage return line fit
            "1234567890" + //numbers
            "’'\"" +       // apostrophe
            "(){}<>\\[\\]" + // brackets
            ":" +        // colon
            "," +        // comma
            "‒–—―" +     // dashes
            "…" +        // ellipsis
            "!" +        // exclamation mark
            "." +        // full stop/period
            "«»" +       // guillemets
            "-‐" +       // hyphen
            "?" +        // question mark
            "‘’“”" +     // quotation marks
            ";" +        // semicolon
            "/" +        // slash/stroke
            "⁄" +        // solidus
            "␠" +        // space?
            "·" +        // interpunct
            "&" +        // ampersand
            "@" +        // at sign
            "*" +        // asterisk
            "\\" +       // backslash
            "•" +        // bullet
            "^" +        // caret
            "¤¢$€£¥₩₪" + // currency
            "†‡" +       // dagger
            "°" +        // degree
            "¡" +        // inverted exclamation point
            "¿" +        // inverted question mark
            "¬" +        // negation
            "#" +        // number sign (hashtag)
            "№" +        // numero sign ()
            "%‰‱" +      // percent and related signs
            "¶" +        // pilcrow
            "′" +        // prime
            "§" +        // section sign
            "~" +        // tilde/swung dash
            "¨" +        // umlaut/diaeresis
            "_" +        // underscore/understrike
            "|¦" +       // vertical/pipe/broken bar
            "⁂" +        // asterism
            "☞" +        // index/fist
            "∴" +        // therefore sign
            "‽" +        // interrobang
            "※" +          // reference mark
            "]";



    public static String[] takeData(String filename) {

        Scanner scanner;
        //fetch attempt
        try {
            scanner = new Scanner(new File(filename));
        }
        catch (FileNotFoundException e) {
            System.out.println("Error: File \""+filename+"\" not found. Skipping.");
            return null;
        }

        scanner.useDelimiter("\\Z");

        String text = scanner.next().toLowerCase();

        return text.split(DELIMITERS);
    }
}
