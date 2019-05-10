public class Token {

    private int beginIndex;
    private int endIndex;
    private TokenType tokenType;
    private String tokenString;

    public Token(int beginIndex, int endIndex, String tokenString, TokenType tokenType) {
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
        this.tokenType = tokenType;
        this.tokenString = tokenString;
    }

    public int getBeginIndex() {
        return beginIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public String getTokenString() {
        return tokenString;
    }

    @Override
    public String toString() {
        return String.format("%3d to %3d  - %11s - %s", beginIndex, endIndex, tokenType, tokenString);
    }
}