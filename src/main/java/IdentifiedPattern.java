import java.util.regex.Pattern;

public class IdentifiedPattern {

    private TokenType tokenType;
    private Pattern pattern;

    public IdentifiedPattern(TokenType tokenType, Pattern pattern) {
        this.tokenType = tokenType;
        this.pattern = pattern;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public Pattern getPattern() {
        return pattern;
    }
}
