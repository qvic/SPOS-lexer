package automaton;

import token.Patterns;
import token.Token;
import util.CharUtils;
import util.ErrorRegistry;
import util.LexicalError;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static automaton.State.*;

public class FiniteStateMachine {

    private int currentPosition;
    private CharSequence charSequence;
    private int positionInSource;
    private ErrorRegistry errors;

    public FiniteStateMachine(ErrorRegistry errors) {
        this.errors = errors;
    }

    private State nextState(State currentState) {

        CharSequence truncatedSource = charSequence.subSequence(currentPosition, charSequence.length());

        char character = truncatedSource.charAt(0);
        CharSequence lookahead = truncatedSource.subSequence(1, truncatedSource.length());

        switch (currentState) {
            case INITIAL:
                if (Character.isDigit(character)) {
                    if (character == '0' && lookahead.length() >= 1) {
                        if (Character.toLowerCase(lookahead.charAt(0)) == 'x') {
                            currentPosition++;
                            return HEX_INTEGER;
                        } else if (Character.toLowerCase(lookahead.charAt(0)) == 'b') {
                            currentPosition++;
                            return BIN_INTEGER;
                        } else if (Character.toLowerCase(lookahead.charAt(0)) == 'o') {
                            currentPosition++;
                            return OCT_INTEGER;
                        }
                    }
                    return INTEGER;
                }

                if (character == '.' && lookahead.length() >= 1 && Character.isDigit(lookahead.charAt(0))) {
                    return FLOAT;
                }

                if (character == '"') {
                    if (lookahead.length() >= 2 && lookahead.charAt(0) == '"' && lookahead.charAt(1) == '"') {
                        currentPosition += 2;
                        return LONG_STRING_ITEM;
                    }
                    return STRING_ITEM;
                }

                if (character == '\'') {
                    if (lookahead.length() >= 2 && lookahead.charAt(0) == '\'' && lookahead.charAt(1) == '\'') {
                        currentPosition += 2;
                        return LONG_STRING_ITEM_SINGLE_QUOTED;
                    }
                    return STRING_ITEM_SINGLE_QUOTED;
                }

                if (Character.toLowerCase(character) == 'r') {
                    if (lookahead.length() >= 1 && (lookahead.charAt(0) == '"' || lookahead.charAt(0) == '\'')) {
                        return STRING_START;
                    }
                }

                if (Character.toLowerCase(character) == 'u' || Character.toLowerCase(character) == 'b') {
                    if (lookahead.length() >= 1 && (lookahead.charAt(0) == '"' || lookahead.charAt(0) == '\'')) {
                        return STRING_START;
                    } else if (lookahead.length() >= 2 && Character.toLowerCase(lookahead.charAt(0)) == 'r' &&
                            (lookahead.charAt(1) == '"' || lookahead.charAt(1) == '\'')) {
                        currentPosition++;
                        return STRING_START;
                    }
                }

                if (Character.isLowerCase(character) || Character.isUpperCase(character) || character == '_') {
                    Matcher matcher = Patterns.KEYWORD.matcher(truncatedSource);
                    if (matcher.matches()) {
                        currentPosition = matcher.end(1) - 1;
                        return KEYWORD;
                    }
                    return IDENTIFIER;
                }

                if (character == '+' || character == '-' || character == '%' || character == '&' || character == '|' || character == '^') {
                    if (lookahead.length() >= 1 && lookahead.charAt(0) == '=') {
                        currentPosition++;
                        return DELIMITER;
                    }
                    return OPERATOR;
                }

                if (character == '~') {
                    return OPERATOR;
                }

                if (character == '(' || character == ')' || character == '[' || character == ']' ||
                        character == '{' || character == '}' || character == '@' || character == ',' ||
                        character == ':' || character == '.' || character == '`' || character == ';') {
                    return DELIMITER;
                }

                if (character == '=') {
                    if (lookahead.length() >= 1 && lookahead.charAt(0) == '=') {
                        currentPosition++;
                        return OPERATOR;
                    }
                    return DELIMITER;
                }

                if (character == '*' || character == '/') {
                    if (lookahead.length() >= 1) {
                        if (lookahead.charAt(0) == '=') {
                            currentPosition++;
                            return DELIMITER;
                        } else if (lookahead.charAt(0) == character) {
                            currentPosition++;
                            if (lookahead.length() >= 2 && lookahead.charAt(1) == '=') {
                                currentPosition++;
                                return DELIMITER;
                            }
                            return OPERATOR;
                        }
                    }
                    return OPERATOR;
                }

                if (character == '<' || character == '>') {
                    if (lookahead.length() >= 1) {
                        if (character == '<' && lookahead.charAt(0) == '>') {
                            currentPosition++;
                            return OPERATOR;
                        } else if (lookahead.charAt(0) == '=') {
                            currentPosition++;
                            return OPERATOR;
                        } else if (lookahead.charAt(0) == character) {
                            currentPosition++;
                            if (lookahead.length() >= 2 && lookahead.charAt(1) == '=') {
                                currentPosition++;
                                return DELIMITER;
                            }
                            return OPERATOR;
                        }
                    }
                    return OPERATOR;
                }

                if (character == '$' || character == '?') {
                    errors.addError(positionInSource,
                            charSequence.subSequence(0, currentPosition + 1).toString(),
                            "Unexpected symbol");
                    return ERROR_SEQUENCE;
                }

                break;

            case IDENTIFIER:
                if (Character.isLetterOrDigit(character) || character == '_') {
                    return IDENTIFIER;
                }

                if (!CharUtils.isDelimiterOrOperatorStart(character) && !Character.isWhitespace(character)) {
                    errors.addError(positionInSource,
                            charSequence.subSequence(0, currentPosition + 1).toString(),
                            "Unexpected symbol in identifier");
                    return ERROR_SEQUENCE;
                }

                break;

            case LONG_STRING_ITEM:
                if (character == '"') {
                    if (lookahead.length() >= 2 && lookahead.charAt(0) == '"' && lookahead.charAt(1) == '"') {
                        currentPosition += 2;
                        return STRING;
                    }
                }

                if (character == '\\') {
                    return ESCAPE_SEQ_LONG_STRING;
                }

                return LONG_STRING_ITEM;

            case LONG_STRING_ITEM_SINGLE_QUOTED:
                if (character == '\'') {
                    if (lookahead.length() >= 2 && lookahead.charAt(0) == '\'' && lookahead.charAt(1) == '\'') {
                        currentPosition += 2;
                        return STRING;
                    }
                }

                if (character == '\\') {
                    return ESCAPE_SEQ_LONG_STRING_SINGLE_QUOTED;
                }

                return LONG_STRING_ITEM_SINGLE_QUOTED;

            case STRING_START:

                if (character == '"') {
                    if (lookahead.length() >= 2 && lookahead.charAt(0) == '"' && lookahead.charAt(1) == '"') {
                        currentPosition += 2;
                        return LONG_STRING_ITEM;
                    }
                    return STRING_ITEM;
                }

                if (character == '\'') {
                    if (lookahead.length() >= 2 && lookahead.charAt(0) == '\'' && lookahead.charAt(1) == '\'') {
                        currentPosition += 2;
                        return LONG_STRING_ITEM_SINGLE_QUOTED;
                    }
                    return STRING_ITEM_SINGLE_QUOTED;
                }

                break;

            case STRING_ITEM:
                if (character == '"') {
                    return STRING;
                }

                if (character == '\\') {
                    return ESCAPE_SEQ;
                }

                if (character == '\n') {
                    errors.addError(positionInSource,
                            charSequence.subSequence(0, currentPosition + 1).toString(),
                            "Unexpected line break in string literal");
                    return ERROR_SEQUENCE;
                }

                return STRING_ITEM;

            case STRING_ITEM_SINGLE_QUOTED:
                if (character == '\'') {
                    return STRING;
                }

                if (character == '\\') {
                    return ESCAPE_SEQ_SINGLE_QUOTED;
                }

                if (character == '\n') {
                    errors.addError(positionInSource,
                            charSequence.subSequence(0, currentPosition + 1).toString(),
                            "Unexpected line break in string literal");
                    return ERROR_SEQUENCE;
                }

                return STRING_ITEM_SINGLE_QUOTED;

            case ESCAPE_SEQ:
                return STRING_ITEM;

            case ESCAPE_SEQ_SINGLE_QUOTED:
                return STRING_ITEM_SINGLE_QUOTED;

            case ESCAPE_SEQ_LONG_STRING:
                return LONG_STRING_ITEM;

            case ESCAPE_SEQ_LONG_STRING_SINGLE_QUOTED:
                return LONG_STRING_ITEM_SINGLE_QUOTED;

            case INTEGER:
                if (Character.isDigit(character)) {
                    return INTEGER;
                }

                if (Character.toLowerCase(character) == 'l') {
                    return LONG_INTEGER;
                }

                if (character == '.') {
                    return FLOAT;
                }

                if (Character.toLowerCase(character) == 'e') {
                    return FLOAT;
                }

                if (Character.toLowerCase(character) == 'j') {
                    return IMAGINARY_NUMBER;
                }

                if (!CharUtils.isDelimiterOrOperatorStart(character) && !Character.isWhitespace(character)) {
                    errors.addError(positionInSource,
                            charSequence.subSequence(0, currentPosition + 1).toString(),
                            "Unexpected symbol in integer literal");
                    return ERROR_SEQUENCE;
                }

                break;

            case FLOAT:
                if (Character.isDigit(character)) {
                    return FLOAT;
                }

                if (Character.toLowerCase(character) == 'e') {
                    if (lookahead.length() >= 1 && lookahead.charAt(0) == '+' || lookahead.charAt(0) == '-') {
                        currentPosition++;
                    }
                    return FLOAT;
                }

                if (Character.toLowerCase(character) == 'j') {
                    return IMAGINARY_NUMBER;
                }

                if (!CharUtils.isDelimiterOrOperatorStart(character) && !Character.isWhitespace(character)) {
                    errors.addError(positionInSource,
                            charSequence.subSequence(0, currentPosition + 1).toString(),
                            "Unexpected symbol in float literal");
                    return ERROR_SEQUENCE;
                }

                break;

            case HEX_INTEGER:
                if (CharUtils.isHexCharacter(character)) {
                    return HEX_INTEGER;
                }

                if (!CharUtils.isDelimiterOrOperatorStart(character) && !Character.isWhitespace(character)) {
                    errors.addError(positionInSource,
                            charSequence.subSequence(0, currentPosition + 1).toString(),
                            "Unexpected symbol in hex integer literal");
                    return ERROR_SEQUENCE;
                }

                break;

            case BIN_INTEGER:
                if (CharUtils.isBinCharacter(character)) {
                    return BIN_INTEGER;
                }

                if (!CharUtils.isDelimiterOrOperatorStart(character) && !Character.isWhitespace(character)) {
                    errors.addError(positionInSource,
                            charSequence.subSequence(0, currentPosition + 1).toString(),
                            "Unexpected symbol in bin integer literal");
                    return ERROR_SEQUENCE;
                }

                break;

            case OCT_INTEGER:
                if (CharUtils.isOctCharacter(character)) {
                    return OCT_INTEGER;
                }

                if (!CharUtils.isDelimiterOrOperatorStart(character) && !Character.isWhitespace(character)) {
                    errors.addError(positionInSource,
                            charSequence.subSequence(0, currentPosition + 1).toString(),
                            "Unexpected symbol in oct integer literal");
                    return ERROR_SEQUENCE;
                }

                break;

            default:
                break;
        }
        return NO_NEXT_STATE;
    }

    public Token run(CharSequence input, int position) {

        charSequence = input;
        positionInSource = position;
        currentPosition = 0;
        State currentState = INITIAL;

        for (currentPosition = 0; currentPosition < input.length(); currentPosition++) {
            State nextState = nextState(currentState);

            if (nextState == NO_NEXT_STATE) {
                if (currentState.isAccepting()) {
                    return new Token(position, position + currentPosition,
                            input.subSequence(0, currentPosition).toString(),
                            currentState.getCorrespondingTokenType());
                } else {
                    return null;
                }
            }

            currentState = nextState;
        }

        if (currentState.isAccepting()) {
            return new Token(position, position + currentPosition,
                    input.subSequence(0, currentPosition).toString(),
                    currentState.getCorrespondingTokenType());
        }

        return null;
    }
}
