package automaton;

import token.TokenType;

public enum State {

    INITIAL,
    NO_NEXT_STATE,

    LONG_INTEGER(TokenType.LONG_INTEGER),
    OCT_INTEGER(TokenType.INTEGER),
    HEX_INTEGER(TokenType.INTEGER),
    BIN_INTEGER(TokenType.INTEGER),
    INTEGER(TokenType.INTEGER),

    FLOAT(TokenType.FLOAT),

    IMAGINARY_NUMBER(TokenType.IMAGINARY_NUMBER),

    STRING_START,
    STRING_ITEM,
    ESCAPE_SEQ,
    STRING_ITEM_SINGLE_QUOTED,
    ESCAPE_SEQ_SINGLE_QUOTED,
    LONG_STRING_ITEM,
    ESCAPE_SEQ_LONG_STRING,
    LONG_STRING_ITEM_SINGLE_QUOTED,
    ESCAPE_SEQ_LONG_STRING_SINGLE_QUOTED,
    STRING(TokenType.STRING),

    DELIMITER(TokenType.DELIMITER),
    OPERATOR(TokenType.OPERATOR),
    KEYWORD(TokenType.KEYWORD),
    IDENTIFIER(TokenType.IDENTIFIER),
    ERROR_SEQUENCE(TokenType.ERROR_SEQUENCE);


    private boolean accepting;
    private TokenType correspondingTokenType;

    State() {
        this.accepting = false;
    }

    State(TokenType correspondingTokenType) {
        this.accepting = true;
        this.correspondingTokenType = correspondingTokenType;
    }

    public boolean isAccepting() {
        return accepting;
    }

    public TokenType getCorrespondingTokenType() {
        if (correspondingTokenType == null) {
            throw new IllegalStateException("Not an accepting state");
        }
        return correspondingTokenType;
    }
}
