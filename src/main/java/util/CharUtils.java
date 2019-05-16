package util;

public class CharUtils {

    public static boolean isHexCharacter(char c) {
        return Character.isDigit(c) || Character.toLowerCase(c) == 'a' ||
                Character.toLowerCase(c) == 'b' || Character.toLowerCase(c) == 'c' ||
                Character.toLowerCase(c) == 'd' || Character.toLowerCase(c) == 'e' ||
                Character.toLowerCase(c) == 'f';
    }

    public static boolean isBinCharacter(char c) {
        return c == '0' || c == '1';
    }

    public static boolean isOctCharacter(char c) {
        return c >= '0' && c <= '7';
    }

    public static boolean isDelimiterOrOperatorStart(char c) {
        return c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}' || c == '@' ||
                c == ',' || c == ':' || c == '.' || c == '`' || c == ';' ||
                c == '+' || c == '-' || c == '*' || c == '/' || c == '%' ||
                c == '&' || c == '|' || c == '^' || c == '~' ||
                c == '>' || c == '=' || c == '!' || c == '<';
    }
}
