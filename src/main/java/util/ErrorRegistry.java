package util;

import java.util.ArrayList;
import java.util.List;

public class ErrorRegistry {

    private final List<LexicalError> errors;

    public ErrorRegistry() {
        errors = new ArrayList<>();
    }

    public void addError(int beginIndex, String tokenString, String message) {
        errors.add(new LexicalError(beginIndex, tokenString, message));
    }

    public void printAll() {
        errors.forEach(System.out::println);
    }

    public void clear() {
        errors.clear();
    }

    public boolean isEmpty() {
        return errors.isEmpty();
    }
}
