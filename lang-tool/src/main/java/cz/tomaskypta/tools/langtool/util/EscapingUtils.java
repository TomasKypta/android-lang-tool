package cz.tomaskypta.tools.langtool.util;

/**
 * Created by tomas on 02.10.14.
 */
public class EscapingUtils {

    public static String escape(String value) {
        return '"' + value + '"';
    }

    public static String unescapeQuotes(String value) {
        if (value.startsWith("\\\"") && value.endsWith("\\\"")) {
            return value.substring(2, value.length() - 2);
        }
        return value;
    }
}
