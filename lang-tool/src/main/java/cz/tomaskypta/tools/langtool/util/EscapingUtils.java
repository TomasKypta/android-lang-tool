package cz.tomaskypta.tools.langtool.util;

import java.util.regex.Pattern;

/**
 * Created by Tomáš Kypta on 02.10.14.
 */
public class EscapingUtils {

    private static final Pattern escapingPattern = Pattern.compile("([^\\\\])('|\")");
    private static final String replacement = "$1\\\\$2";

    public static String escapeWithQuotes(String value) {
        return '"' + value + '"';
    }

    public static String escapeWithBackslash(String value) {
        return escapingPattern.matcher(value).replaceAll(replacement);
    }

    public static String unescapeQuotes(String value) {
        if (value.startsWith("\\\"") && value.endsWith("\\\"")) {
            return value.substring(2, value.length() - 2);
        }
        return value;
    }
}
