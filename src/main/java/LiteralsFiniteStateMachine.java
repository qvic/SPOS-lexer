import util.CharUtils;

public class LiteralsFiniteStateMachine {

    private int currentPosition;
    private CharSequence charSequence;

    public LiteralState nextState(LiteralState currentState) {
        char character = charSequence.charAt(currentPosition);
        CharSequence lookahead = charSequence.subSequence(currentPosition + 1, charSequence.length());

        switch (currentState) {
            case INITIAL:
                if (Character.isDigit(character)) {
                    if (character == '0') {
                        if (lookahead.charAt(0) == 'x') {
                            currentPosition++;
                            return LiteralState.HEX_INTEGER;
                        } else if (lookahead.charAt(0) == 'b') {
                            currentPosition++;
                            return LiteralState.BIN_INTEGER;
                        } else if (lookahead.charAt(0) == 'o') {
                            currentPosition++;
                            return LiteralState.OCT_INTEGER;
                        }
                    }
                    return LiteralState.INTEGER;
                }

                if (character == '.') {
                    return LiteralState.FLOAT;
                }

                if (character == '"') {
                    if (lookahead.charAt(0) == '"' && lookahead.charAt(1) == '"') {
                        currentPosition += 2;
                        return LiteralState.LONG_STRING_ITEM;
                    }
                    return LiteralState.SHORT_STRING_ITEM;
                }

                if (character == '\'') {
                    if (lookahead.charAt(0) == '\'' && lookahead.charAt(1) == '\'') {
                        currentPosition += 2;
                        return LiteralState.LONG_STRING_ITEM_SINGLE_QUOTED;
                    }
                    return LiteralState.SHORT_STRING_ITEM_SINGLE_QUOTED;
                }

                if (Character.toLowerCase(character) == 'r') {
                    return LiteralState.RAW_STRING_START;
                }

                if (Character.toLowerCase(character) == 'u' ||
                        Character.toLowerCase(character) == 'b') {
                    return LiteralState.STRING_PREFIX;
                }

                break;

            case LONG_STRING_ITEM:
                if (character == '"') {
                    if (lookahead.charAt(0) == '"' && lookahead.charAt(1) == '"') {
                        currentPosition += 2;
                        return LiteralState.STRING;
                    }
                }

                if (character == '\\') {
                    return LiteralState.ESCAPE_SEQ_LONG_STRING;
                }

                return LiteralState.LONG_STRING_ITEM;

            case LONG_STRING_ITEM_SINGLE_QUOTED:
                if (character == '\'') {
                    if (lookahead.charAt(0) == '\'' && lookahead.charAt(1) == '\'') {
                        currentPosition += 2;
                        return LiteralState.STRING;
                    }
                }

                if (character == '\\') {
                    return LiteralState.ESCAPE_SEQ_LONG_STRING_SINGLE_QUOTED;
                }

                return LiteralState.LONG_STRING_ITEM_SINGLE_QUOTED;

            case STRING_PREFIX:
                if (Character.toLowerCase(character) == 'r') {
                    return LiteralState.RAW_STRING_START;
                }

                if (character == '"') {
                    if (lookahead.charAt(0) == '"' && lookahead.charAt(1) == '"') {
                        currentPosition += 2;
                        return LiteralState.LONG_STRING_ITEM;
                    }
                    return LiteralState.SHORT_STRING_ITEM;
                }

                if (character == '\'') {
                    if (lookahead.charAt(0) == '\'' && lookahead.charAt(1) == '\'') {
                        currentPosition += 2;
                        return LiteralState.LONG_STRING_ITEM_SINGLE_QUOTED;
                    }
                    return LiteralState.SHORT_STRING_ITEM_SINGLE_QUOTED;
                }

                break;
            case RAW_STRING_START:
                if (character == '"') {
                    if (lookahead.charAt(0) == '"' && lookahead.charAt(1) == '"') {
                        currentPosition += 2;
                        return LiteralState.LONG_STRING_ITEM;
                    }
                    return LiteralState.SHORT_STRING_ITEM;
                }

                if (character == '\'') {
                    if (lookahead.charAt(0) == '\'' && lookahead.charAt(1) == '\'') {
                        currentPosition += 2;
                        return LiteralState.LONG_STRING_ITEM_SINGLE_QUOTED;
                    }
                    return LiteralState.SHORT_STRING_ITEM_SINGLE_QUOTED;
                }

                break;
            case SHORT_STRING_ITEM:
                if (character == '"') {
                    return LiteralState.STRING;
                }

                if (character == '\\') {
                    return LiteralState.ESCAPE_SEQ;
                }

                if (character != '\n') {
                    return LiteralState.SHORT_STRING_ITEM;
                }

                break;
            case SHORT_STRING_ITEM_SINGLE_QUOTED:
                if (character == '\'') {
                    return LiteralState.STRING;
                }

                if (character == '\\') {
                    return LiteralState.ESCAPE_SEQ_SINGLE_QUOTED;
                }

                if (character != '\n') {
                    return LiteralState.SHORT_STRING_ITEM_SINGLE_QUOTED;
                }

                break;
            case ESCAPE_SEQ:
                return LiteralState.SHORT_STRING_ITEM;

            case ESCAPE_SEQ_SINGLE_QUOTED:
                return LiteralState.SHORT_STRING_ITEM_SINGLE_QUOTED;

            case ESCAPE_SEQ_LONG_STRING:
                return LiteralState.LONG_STRING_ITEM;

            case ESCAPE_SEQ_LONG_STRING_SINGLE_QUOTED:
                return LiteralState.LONG_STRING_ITEM_SINGLE_QUOTED;

            case INTEGER:
                if (Character.isDigit(character)) {
                    return LiteralState.INTEGER;
                }

                if (Character.toLowerCase(character) == 'l') {
                    return LiteralState.LONG_INTEGER;
                }

                if (character == '.') {
                    return LiteralState.FLOAT;
                }

                if (character == 'e' || character == 'E') { // to lower case
                    return LiteralState.FLOAT;
                }

                if (character == 'j' || character == 'J') {
                    return LiteralState.IMAGINARY_NUMBER;
                }

                break;
            case FLOAT:
                if (Character.isDigit(character)) {
                    return LiteralState.FLOAT;
                }

                if (character == 'e' || character == 'E') {
                    if (lookahead.charAt(0) == '+' || lookahead.charAt(0) == '-') {
                        currentPosition++;
                    }
                    return LiteralState.FLOAT;
                }

                if (character == 'j' || character == 'J') {
                    return LiteralState.IMAGINARY_NUMBER;
                }

                break;

            case HEX_INTEGER:
                if (CharUtils.isHexCharacter(character)) {
                    return LiteralState.HEX_INTEGER;
                }

                break;
            case BIN_INTEGER:

                if (character == '0' || character == '1') {
                    return LiteralState.BIN_INTEGER;
                }

                break;

            case OCT_INTEGER:
                if (character >= '0' && character <= '7') {
                    return LiteralState.OCT_INTEGER;
                }

                break;
            default:
                break;
        }
        return LiteralState.NO_NEXT_STATE;
    }

    public Token run(CharSequence input, int position) {

        charSequence = input;
        currentPosition = 0;
        LiteralState currentState = LiteralState.INITIAL;
        for (currentPosition = 0; currentPosition < input.length(); currentPosition++) {
            LiteralState nextState = nextState(currentState);

            if (nextState == LiteralState.NO_NEXT_STATE) {
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
