import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return beginIndex == token.beginIndex &&
                endIndex == token.endIndex &&
                tokenType == token.tokenType &&
                Objects.equals(tokenString, token.tokenString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beginIndex, endIndex, tokenType, tokenString);
    }
}