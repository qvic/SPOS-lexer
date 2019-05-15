package token;

import java.util.List;
import java.util.regex.Pattern;

public class Patterns {

    public static final Pattern KEYWORD = Pattern.compile(
            "(and|del|from|not|while|as|elif|global|or|" +
                    "with|assert|else|if|pass|yield|break|except|import|" +
                    "print|class|exec|in|raise|continue|finally|is|return|" +
                    "def|for|lambda|try)(?![_a-zA-Z0-9]).*",
            Pattern.DOTALL
    );
    
    public static final Pattern DELIMITER = Pattern.compile(
            "(\\(|\\)|\\[|\\]|\\{|\\}|@|,|:|\\.|`|=(?!=)|;|\\+=|-=|\\*=|/=|//=|%=|&=|\\|=|\\^=|>>=|<<=|\\*\\*=).*",
            Pattern.DOTALL
    );

    public static final Pattern OPERATOR = Pattern.compile(
            "(\\+|-|\\*\\*|\\*|//|/|%|<<|>>|&|\\||\\^|~|<=|>=|<>|<|>|==|!=).*",
            Pattern.DOTALL
    );
    public static final Pattern IDENTIFIER = Pattern.compile(
            "([_a-zA-Z][_a-zA-Z0-9]*).*",
            Pattern.DOTALL
    );

    public static final Pattern WHITESPACE = Pattern.compile(
            "((?:[^\\S\\n]|\\\\\\n)+).*", // matches whitespace or escaped line break
            Pattern.DOTALL
    );

    public static final Pattern COMMENT = Pattern.compile(
            "(#[^\\n]*).*", // matches comment to the end of the line
            Pattern.DOTALL
    );
    
    public static List<TokenPattern> getTokenPatterns() {
        return List.of(
                new TokenPattern(TokenType.KEYWORD, KEYWORD),
                new TokenPattern(TokenType.DELIMITER, DELIMITER),
                new TokenPattern(TokenType.OPERATOR, OPERATOR),
                new TokenPattern(TokenType.IDENTIFIER, IDENTIFIER)
        );
    }
}
