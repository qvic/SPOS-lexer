import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

    private final LiteralsFiniteStateMachine fsm;
    private List<Token> result;
    private int lastIndent;

    public Lexer() {
        result = new ArrayList<>();

//        compiledRegex = Pattern.compile(
//                "(?:(?<DELIMITER>\\(|\\)|\\[|\\]|\\{|\\}|@|,|:|\\.|`|=|;|" +
//                        "\\+=|-=|\\*=|/=|//=|%=|&=|\\|=|\\^=|>>=|<<=|\\*\\*=)|" +
//                        "(?<OPERATOR>\\+|-|\\*|\\*\\*|/|//|%|<<|>>|&|\\||\\^|~|<=|>=|<|>|==|!=|<>)|" +
//                        "(?<KEYWORD>and|del|from|not|while|as|elif|global|or|" +
//                        "with|assert|else|if|pass|yield|break|except|import|" +
//                        "print|class|exec|in|raise|continue|finally|is|return|" +
//                        "def|for|lambda|try)[^_a-zA-Z0-9]|" +
//                        "(?<IDENTIFIER>[_a-zA-Z][_a-zA-Z0-9]*)).*", Pattern.DOTALL);

        fsm = new LiteralsFiniteStateMachine();

        lastIndent = 0;
    }

    public void tokenize(String input) {
        int position = 0;

        CharSequence charSequence = CharBuffer.wrap(input);
        while (position < input.length()) {
            Token token = getNextToken(charSequence, position);
            if (token == null) {
                break;
            }
            System.out.println(token);
            result.add(token);
            position = token.getEndIndex();
        }
    }

    public List<Token> getTokens() {
        return result;
    }

    private Token getNextToken(CharSequence input, int startPosition) {
        if (isEnd(input, startPosition)) {
            return null;
        }
        if (isLineBreak(input, startPosition)) {
            return new Token(startPosition, startPosition + 1, "", TokenType.NEWLINE);
        }

        CharSequence charSequence = truncateSequence(input, startPosition);

        Matcher whitespaceMatcher = Patterns.WHITESPACE.matcher(charSequence);
        if (whitespaceMatcher.matches()) {
            int offset = whitespaceMatcher.end(1);
            if (startPosition > 0 && isLineBreak(input, startPosition - 1)) {
                if (lastIndent < offset) {
                    lastIndent = offset;
                    return new Token(startPosition, startPosition + offset, "", TokenType.INDENT);
                } else if (lastIndent > offset) {
                    lastIndent = offset;
                    return new Token(startPosition, startPosition + offset, "", TokenType.DEDENT);
                } else {
                    System.out.println("On same level");
                }
            }
            startPosition += offset;
            charSequence = truncateSequence(charSequence, offset);
        }

        Matcher commentMatcher = Patterns.COMMENT.matcher(charSequence);
        if (commentMatcher.matches()) {
            int offset = commentMatcher.end(1);
            startPosition += offset;
            System.out.println("Comment: " + charSequence.subSequence(0, offset));
            charSequence = truncateSequence(charSequence, offset);
        }

        if (isEnd(input, startPosition)) {
            return null;
        }
        if (isLineBreak(input, startPosition)) {
            return new Token(startPosition, startPosition + 1, "", TokenType.NEWLINE);
        }

        Token fsmToken = fsm.run(charSequence, startPosition);
        if (fsmToken != null) {
            return fsmToken;
        }

        for (IdentifiedPattern pattern : Patterns.getCompiledRegexesForTokens()) {
            Matcher matcher = pattern.getPattern().matcher(charSequence);
            if (matcher.matches()) {
                String token = matcher.group(1);
                if (token != null) {
                    return new Token(startPosition, startPosition + token.length(), token, pattern.getTokenType());
                }
            }
        }

        if (!isEnd(input, startPosition)) throw new RuntimeException("Lexer error at position " + startPosition);

        return null;
    }

    private boolean isEnd(CharSequence charSequence, int position) {
        return position > charSequence.length() - 1;
    }

    private boolean isLineBreak(CharSequence charSequence, int position) {
        return charSequence.charAt(position) == '\n';
    }

    private CharSequence truncateSequence(CharSequence charSequence, int from) {
        return charSequence.subSequence(from, charSequence.length());
    }
}