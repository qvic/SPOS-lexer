package automaton;

import token.Token;
import util.CharUtils;

import static automaton.LiteralState.*;

public class LiteralsFiniteStateMachine {

    private int currentPosition;
    private CharSequence charSequence;

    private LiteralState nextState(LiteralState currentState) {

        char character = charSequence.charAt(currentPosition);
        CharSequence lookahead = charSequence.subSequence(currentPosition + 1, charSequence.length());

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

                if (character == '.') {
                    return FLOAT;
                }

                if (character == '"') {
                    if (lookahead.length() >= 2 && lookahead.charAt(0) == '"' && lookahead.charAt(1) == '"') {
                        currentPosition += 2;
                        return LONG_STRING_ITEM;
                    }
                    return SHORT_STRING_ITEM;
                }

                if (character == '\'') {
                    if (lookahead.length() >= 2 && lookahead.charAt(0) == '\'' && lookahead.charAt(1) == '\'') {
                        currentPosition += 2;
                        return LONG_STRING_ITEM_SINGLE_QUOTED;
                    }
                    return SHORT_STRING_ITEM_SINGLE_QUOTED;
                }

                if (Character.toLowerCase(character) == 'r') {
                    return RAW_STRING_START;
                }

                if (Character.toLowerCase(character) == 'u' || Character.toLowerCase(character) == 'b') {
                    return STRING_PREFIX;
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

            case STRING_PREFIX:
                if (Character.toLowerCase(character) == 'r') {
                    return RAW_STRING_START;
                }

                if (character == '"') {
                    if (lookahead.length() >= 2 && lookahead.charAt(0) == '"' && lookahead.charAt(1) == '"') {
                        currentPosition += 2;
                        return LONG_STRING_ITEM;
                    }
                    return SHORT_STRING_ITEM;
                }

                if (character == '\'') {
                    if (lookahead.length() >= 2 && lookahead.charAt(0) == '\'' && lookahead.charAt(1) == '\'') {
                        currentPosition += 2;
                        return LONG_STRING_ITEM_SINGLE_QUOTED;
                    }
                    return SHORT_STRING_ITEM_SINGLE_QUOTED;
                }

                break;

            case RAW_STRING_START:
                if (character == '"') {
                    if (lookahead.length() >= 2 && lookahead.charAt(0) == '"' && lookahead.charAt(1) == '"') {
                        currentPosition += 2;
                        return LONG_STRING_ITEM;
                    }
                    return SHORT_STRING_ITEM;
                }

                if (character == '\'') {
                    if (lookahead.length() >= 2 && lookahead.charAt(0) == '\'' && lookahead.charAt(1) == '\'') {
                        currentPosition += 2;
                        return LONG_STRING_ITEM_SINGLE_QUOTED;
                    }
                    return SHORT_STRING_ITEM_SINGLE_QUOTED;
                }

                break;

            case SHORT_STRING_ITEM:
                if (character == '"') {
                    return STRING;
                }

                if (character == '\\') {
                    return ESCAPE_SEQ;
                }

                if (character != '\n') {
                    return SHORT_STRING_ITEM;
                }

                break;

            case SHORT_STRING_ITEM_SINGLE_QUOTED:
                if (character == '\'') {
                    return STRING;
                }

                if (character == '\\') {
                    return ESCAPE_SEQ_SINGLE_QUOTED;
                }

                if (character != '\n') {
                    return SHORT_STRING_ITEM_SINGLE_QUOTED;
                }

                break;

            case ESCAPE_SEQ:
                return SHORT_STRING_ITEM;

            case ESCAPE_SEQ_SINGLE_QUOTED:
                return SHORT_STRING_ITEM_SINGLE_QUOTED;

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

                break;

            case HEX_INTEGER:
                if (CharUtils.isHexCharacter(character)) {
                    return HEX_INTEGER;
                }

                break;

            case BIN_INTEGER:
                if (CharUtils.isBinCharacter(character)) {
                    return BIN_INTEGER;
                }

                break;

            case OCT_INTEGER:
                if (CharUtils.isOctCharacter(character)) {
                    return OCT_INTEGER;
                }

                break;
            default:
                break;
        }
        return NO_NEXT_STATE;
    }

    public Token run(CharSequence input, int position) {

        charSequence = input;
        currentPosition = 0;
        LiteralState currentState = INITIAL;

        for (currentPosition = 0; currentPosition < input.length(); currentPosition++) {
            LiteralState nextState = nextState(currentState);

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
