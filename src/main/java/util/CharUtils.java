package util;

public class CharUtils {

    public static boolean isHexCharacter(char c) {
        if (Character.isDigit(c) || Character.toLowerCase(c) == 'a' ||
                Character.toLowerCase(c) == 'b' || Character.toLowerCase(c) == 'c' ||
                Character.toLowerCase(c) == 'd' || Character.toLowerCase(c) == 'e' ||
                Character.toLowerCase(c) == 'f') {
            return true;
        }
        return false;
    }
}
