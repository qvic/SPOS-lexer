package util;

import token.TokenType;

public class LexicalError {

    private int beginIndex;
    private String tokenString;
    private String message;

    public LexicalError(int beginIndex, String tokenString, String message) {
        this.beginIndex = beginIndex;
        this.tokenString = tokenString;
        this.message = message;
    }

    public int getBeginIndex() {
        return beginIndex;
    }

    public String getTokenString() {
        return tokenString;
    }

    @Override
    public String toString() {
        return "LexicalError{" +
                "beginIndex=" + beginIndex +
                ", tokenString='" + tokenString + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
