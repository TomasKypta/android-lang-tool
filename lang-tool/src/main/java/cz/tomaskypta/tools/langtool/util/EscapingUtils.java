package cz.tomaskypta.tools.langtool.util;

/**
 * Created by tomas on 02.10.14.
 */
public class EscapingUtils {

    public static String escape(String value) {
        return '"' + value + '"';
    }
}
