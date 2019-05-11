package token;

import java.util.regex.Pattern;

public class TokenPattern {

    private TokenType tokenType;
    private Pattern pattern;

    public TokenPattern(TokenType tokenType, Pattern pattern) {
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
