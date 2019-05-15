import automaton.FiniteStateMachine;
import token.Patterns;
import token.Token;
import token.TokenType;
import util.LexerException;

import java.nio.CharBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;

public class Lexer {

    private final FiniteStateMachine fsm;
    private final List<Token> result;
    private Deque<Integer> indents;

    public Lexer() {
        result = new ArrayList<>();
        fsm = new FiniteStateMachine();
        indents = new ArrayDeque<>();
        indents.addLast(0);
    }

    public void tokenize(String input) {
        result.clear();
        int position = 0;

        CharSequence charSequence = CharBuffer.wrap(input);
        while (position < input.length()) {
            Token token = getNextToken(charSequence, position);
            if (token == null) {
                break;
            }
            result.add(token);
            position = token.getEndIndex();
        }

        System.out.println(fsm.getErrors());
    }

    public List<Token> getTokens() {
        return result;
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



//        for (TokenPattern pattern : Patterns.getTokenPatterns()) {
//            Matcher matcher = pattern.getPattern().matcher(charSequence);
//
//            if (matcher.matches()) {
//                String token = matcher.group(1);
//                if (token != null) {
//                    return new Token(startPosition, startPosition + token.length(), token, pattern.getTokenType());
//                }
//            }
//        }

        if (!isEnd(sourceText, startPosition)) {
            throw new LexerException("Exception at position " + startPosition);
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
                    topIndent = indents.removeLast();
                } while (matchedCount < topIndent);
                if (matchedCount != topIndent) {
                    throw new LexerException("Unexpected indentation at " + startPosition);
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