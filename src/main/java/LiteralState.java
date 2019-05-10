public enum LiteralState implements State {

    INITIAL,
    NO_NEXT_STATE,

    LONG_INTEGER(TokenType.LONG_INTEGER),
    DECIMAL_INTEGER,
    OCT_INTEGER(TokenType.INTEGER),
    HEX_INTEGER(TokenType.INTEGER),
    BIN_INTEGER(TokenType.INTEGER),
    INTEGER(TokenType.INTEGER),

    FLOAT(TokenType.FLOAT),

    IMAGINARY_NUMBER(TokenType.IMAGINARY_NUMBER),

    STRING_PREFIX,
    RAW_STRING_START,
    SHORT_STRING_ITEM,
    ESCAPE_SEQ,
    SHORT_STRING_ITEM_SINGLE_QUOTED,
    ESCAPE_SEQ_SINGLE_QUOTED,
    LONG_STRING_ITEM,
    ESCAPE_SEQ_LONG_STRING,
    LONG_STRING_ITEM_SINGLE_QUOTED,
    ESCAPE_SEQ_LONG_STRING_SINGLE_QUOTED,
    STRING(TokenType.STRING);


    private boolean accepting;
    private TokenType correspondingTokenType;

    LiteralState() {
        this.accepting = false;
    }

    LiteralState(TokenType correspondingTokenType) {
        this.accepting = true;
        this.correspondingTokenType = correspondingTokenType;
    }

    @Override
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
