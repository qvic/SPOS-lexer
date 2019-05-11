import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String... args) throws IOException {
        String program = new String(Files.readAllBytes(Paths.get("src/main/resources/test.py")));

        Lexer lexer = new Lexer();
        lexer.tokenize(program);
    }
}
