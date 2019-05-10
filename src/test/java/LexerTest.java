import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LexerTest {

    @Test
    void testFile() throws IOException {
        String program = new String(Files.readAllBytes(Paths.get("src/main/resources/test.py")));

        Lexer lexer = new Lexer();
        lexer.tokenize(program);
    }

    @Test
    void testKeywords() {
        Lexer lexer = new Lexer();
        String input = "assert 123and del _fromi tryfor =if( while123 return";

        lexer.tokenize(input);
        List<Token> tokens = lexer.getTokens();

        assertThat(tokens).containsExactly(
                new Token(0, 6, "assert", TokenType.KEYWORD),
                new Token(7, 10, "123", TokenType.INTEGER),
                new Token(10, 13, "and", TokenType.KEYWORD),
                new Token(14, 17, "del", TokenType.KEYWORD),
                new Token(18, 24, "_fromi", TokenType.IDENTIFIER),
                new Token(25, 31, "tryfor", TokenType.IDENTIFIER),
                new Token(32, 33, "=", TokenType.DELIMITER),
                new Token(33, 35, "if", TokenType.KEYWORD),
                new Token(35, 36, "(", TokenType.DELIMITER),
                new Token(37, 45, "while123", TokenType.IDENTIFIER),
                new Token(46, 52, "return", TokenType.KEYWORD)
        );
    }

    @Test
    void testIdentifiers() {
        Lexer lexer = new Lexer();
        String input = "234_123 test _test from_test";

        lexer.tokenize(input);
        List<Token> tokens = lexer.getTokens();

        assertThat(tokens).containsExactly(
                new Token(0, 3, "234", TokenType.INTEGER),
                new Token(3, 7, "_123", TokenType.IDENTIFIER),
                new Token(8, 12, "test", TokenType.IDENTIFIER),
                new Token(13, 18, "_test", TokenType.IDENTIFIER),
                new Token(19, 28, "from_test", TokenType.IDENTIFIER)
        );
    }

    @Test
    void testDelimitersAndOperators() {
        Lexer lexer = new Lexer();
        String input = ">>= >> =>= -=<> === ***";

        lexer.tokenize(input);
        List<Token> tokens = lexer.getTokens();

        assertThat(tokens).containsExactly(
                new Token(0, 3, ">>=", TokenType.DELIMITER),
                new Token(4, 6, ">>", TokenType.OPERATOR),
                new Token(7, 8, "=", TokenType.DELIMITER),
                new Token(8, 10, ">=", TokenType.OPERATOR),
                new Token(11, 13, "-=", TokenType.DELIMITER),
                new Token(13, 15, "<>", TokenType.OPERATOR),
                new Token(16, 18, "==", TokenType.OPERATOR),
                new Token(18, 19, "=", TokenType.DELIMITER),
                new Token(20, 22, "**", TokenType.OPERATOR),
                new Token(22, 23, "*", TokenType.OPERATOR)
        );
    }

    @Test
    void testFloats() {
        Lexer lexer = new Lexer();
        String input = "3.14 10. .001 1e100 3.14e-10 0e0";

        lexer.tokenize(input);
        List<Token> tokens = lexer.getTokens();

        assertThat(tokens).containsExactly(
                new Token(0, 4, "3.14", TokenType.FLOAT),
                new Token(5, 8, "10.", TokenType.FLOAT),
                new Token(9, 13, ".001", TokenType.FLOAT),
                new Token(14, 19, "1e100", TokenType.FLOAT),
                new Token(20, 28, "3.14e-10", TokenType.FLOAT),
                new Token(29, 32, "0e0", TokenType.FLOAT)
        );
    }

    @Test
    void testIntegers() {
        Lexer lexer = new Lexer();
        String input = "7 21474847 0177 3L 7922816336L 0377L 0x100000000 792281639536 0xdeadbeef";

        lexer.tokenize(input);
        List<Token> tokens = lexer.getTokens();

        assertThat(tokens).containsExactly(
                new Token(0, 1, "7", TokenType.INTEGER),
                new Token(2, 10, "21474847", TokenType.INTEGER),
                new Token(11, 15, "0177", TokenType.INTEGER),
                new Token(16, 18, "3L", TokenType.LONG_INTEGER),
                new Token(19, 30, "7922816336L", TokenType.LONG_INTEGER),
                new Token(31 , 36, "0377L", TokenType.LONG_INTEGER),
                new Token(37 , 48, "0x100000000", TokenType.INTEGER),
                new Token(49 , 61, "792281639536", TokenType.INTEGER),
                new Token(62 , 72, "0xdeadbeef", TokenType.INTEGER)
        );
    }
}