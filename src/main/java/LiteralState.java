public enum LiteralState implements State {

    INITIAL(false),
    NO_NEXT_STATE(false),

    DECIMAL_INTEGER(false),
    OCT_INTEGER(false),
    HEX_INTEGER(false),
    BIN_INTEGER(false),
    LONG_INTEGER(true),
    INTEGER(true),

    FLOAT(true),

    IMAGINARY_NUMBER(true),

    STRING_PREFIX(false),
    RAW_STRING_START(false),
    SHORT_STRING_ITEM(false),
    ESCAPE_SEQ(false),
    SHORT_STRING_ITEM_SINGLE_QUOTED(false),
    ESCAPE_SEQ_SINGLE_QUOTED(false),
    LONG_STRING_ITEM(false),
    ESCAPE_SEQ_LONG_STRING(false),
    LONG_STRING_ITEM_SINGLE_QUOTED(false),
    ESCAPE_SEQ_LONG_STRING_SINGLE_QUOTED(false),
    STRING(true);

    private boolean accepting;

    LiteralState(boolean accepting) {
        this.accepting = accepting;
    }

    @Override
    public boolean isAccepting() {
        return accepting;
    }
}
