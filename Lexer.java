import java.io.*;
import java.util.*;

public class Lexer {

    static Set<String> keywords = new HashSet<>(
            Arrays.asList("int", "float", "double", "char", "if", "else", "while", "for", "return"));

    static Set<Character> operators = new HashSet<>(
            Arrays.asList('+', '-', '*', '/', '=', '<', '>'));

    static Set<Character> seperators = new HashSet<>(
            Arrays.asList(';', ',', '(', ')', '{', '}'));

    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(
                new FileReader("sample.txt"));

        BufferedWriter bw = new BufferedWriter(
                new FileWriter("tokens.txt"));

        String line;

        while ((line = br.readLine()) != null) {

            // HEADER
            if (line.startsWith("#include")) {
                bw.write("Lexeme: " + line + " Token: HEADER\n");
                continue;
            }

            // COMMENT
            if (line.trim().startsWith("//")) {
                bw.write("Lexeme: " + line + " Token : COMMENT\n");
                continue;
            }

            // STRING LITERAL (inline extraction)
            if (line.contains("\"")) {
                int start = line.indexOf("\"");
                int end = line.lastIndexOf("\"");

                if (start != end) {
                    String str = line.substring(start, end + 1);
                    bw.write("Lexeme: " + str + " Token: STRING_LITERALS\n");
                    line = line.replace(str, "");
                }
            }

            // TOKENIZATION
            StringTokenizer tokenizer = new StringTokenizer(line, " +-*/=;(),{}", true);

            while (tokenizer.hasMoreTokens()) {

                String token = tokenizer.nextToken().trim();

                if (token.isEmpty())
                    continue;
                if (keywords.contains(token)) {
                    bw.write("Lexeme: " + token + " Token: KEYWORDS\n");
                } else if (token.matches("0|[1-9]\\d*(\\.\\d+)?")) {
                    bw.write("Lexeme: " + token + " Token: NUMBERS\n");
                } else if (operators.contains(token.charAt(0))) {
                    bw.write("Lexeme: " + token + " Token: OPERATORS\n");
                } else if (token.length() == 1 && seperators.contains(token.charAt(0))) {
                    bw.write("Lexeme: " + token + " Token: SEPERATORS\n");
                } else if (token.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                    bw.write("Lexeme: " + token + " Token: IDENTIFIER\n");
                } else if (token.matches("\".*\"")) {
                    bw.write("Lexeme: " + token + " Token: STRING_LITERALS\n");
                } else {
                    bw.write("Lexeme: " + token + " Token: UNKNOWN\n");
                }
            }
        }

        br.close();
        bw.close();

        System.out.println("Lexical Analysis Completed");
    }
}