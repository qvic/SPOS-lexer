import automaton.FiniteStateMachine;
import token.Patterns;
import token.Token;
import token.TokenType;
import util.ErrorRegistry;

import java.nio.CharBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;

public class Lexer {

    private final FiniteStateMachine fsm;
    private final List<Token> tokens;
    private final ErrorRegistry errors;
    private final Deque<Integer> indents;

    public Lexer() {
        tokens = new ArrayList<>();
        errors = new ErrorRegistry();
        fsm = new FiniteStateMachine(errors);

        indents = new ArrayDeque<>();
        indents.addLast(0);
    }

    public void tokenize(String input) {
        tokens.clear();
        errors.clear();
        int position = 0;

        CharSequence charSequence = CharBuffer.wrap(input);
        while (position < input.length()) {
            Token token = getNextToken(charSequence, position);
            if (token == null) {
                break;
            }
            tokens.add(token);
            position = token.getEndIndex();
        }

        errors.printAll();
    }

    public List<Token> getTokens() {
        return tokens;
    }

    private Token getNextToken(CharSequence sourceText, int startPosition) {
        if (isEnd(sourceText, startPosition)) {
            return null;
        }
        if (isLineBreak(sourceText, startPosition)) {
            return getNewlineToken(startPosition);
        }

        CharSequence charSequence = getTruncatedSequence(sourceText, startPosition);

        Matcher whitespaceMatcher = Patterns.WHITESPACE.matcher(charSequence);
        if (whitespaceMatcher.matches()) {
            int offset = whitespaceMatcher.end(1);

            Token indentationToken = processIndentation(sourceText, startPosition, offset);
            if (indentationToken != null) {
                return indentationToken;
            }

            startPosition += offset;
            charSequence = getTruncatedSequence(charSequence, offset);
        }

        Matcher commentMatcher = Patterns.COMMENT.matcher(charSequence);
        if (commentMatcher.matches()) {
            int offset = commentMatcher.end(1);
            startPosition += offset;
            // process comments
            charSequence = getTruncatedSequence(charSequence, offset);
        }

        if (isEnd(sourceText, startPosition)) {
            return null;
        }
        if (isLineBreak(sourceText, startPosition)) {
            return getNewlineToken(startPosition);
        }

        Token fsmToken = fsm.run(charSequence, startPosition);
        if (fsmToken != null) {
            return fsmToken;
        }

        if (!isEnd(sourceText, startPosition)) {
            errors.addError(startPosition, charSequence.toString(), "Error");
        }

        return null;
    }

    private boolean isEnd(CharSequence charSequence, int position) {
        return position > charSequence.length() - 1;
    }

    private boolean isLineBreak(CharSequence charSequence, int position) {
        return charSequence.charAt(position) == '\n';
    }

    private CharSequence getTruncatedSequence(CharSequence charSequence, int from) {
        return charSequence.subSequence(from, charSequence.length());
    }

    private Token processIndentation(CharSequence input, int startPosition, int matchedCount) {
        if (startPosition > 0 && isLineBreak(input, startPosition - 1)) {
            int topIndent = indents.getLast();
            if (topIndent < matchedCount) {
                indents.addLast(matchedCount);
                return new Token(startPosition, startPosition + matchedCount, "", TokenType.INDENT);
            } else if (topIndent > matchedCount) {
                do {
                    indents.removeLast();
                    topIndent = indents.getLast();
                } while (matchedCount < topIndent);
                if (matchedCount != topIndent) {
                    errors.addError(startPosition, "", "Unexpected indentation");
                    indents.removeLast();
                    indents.addLast(matchedCount);
                }
                return new Token(startPosition, startPosition + matchedCount, "", TokenType.DEDENT);
            }
        }
        return null;
    }

    private Token getNewlineToken(int startPosition) {
        return new Token(startPosition, startPosition + 1, "", TokenType.NEWLINE);
    }
}