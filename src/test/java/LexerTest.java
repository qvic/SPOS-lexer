import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class LexerTest {

    @BeforeEach
    void setUp() {

    }

    @Test
    void tokenize() throws IOException {
        String program = new String(Files.readAllBytes(Paths.get("src/main/resources/test.py")));

        Lexer lexer = new Lexer();
        lexer.tokenize(program);
    }
}