import java.io.*;
import java.util.*;

public class Lexer {

    static Set<String> keywords = new HashSet<>(Arrays.asList(
            "int", "float", "double", "if", "else", "while", "for", "return"
    ));

    static Set<String> operators = new HashSet<>(Arrays.asList(
            "+", "-", "*", "/", "=", "==", "!=", "<", ">", "<=", ">=", "&&", "||", "++", "--"
    ));

    static Set<Character> symbols = new HashSet<>(Arrays.asList(
            '(', ')', '{', '}', '[', ']', ';', ',', '#'
    ));

    static String generateOutput(String lexeme, String token) {
        return "Lexeme: " + lexeme + " Token: " + token;
    }

    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader("sample.txt"));
        BufferedWriter bw = new BufferedWriter(new FileWriter("tokens.txt"));

        String line;

        while ((line = br.readLine()) != null) {

            int i = 0;
            int n = line.length();

            while (i < n) {
                char c = line.charAt(i);

                // Skip whitespace
                if (Character.isWhitespace(c)) {
                    i++;
                    continue;
                }

                // 🔹 HEADER (#include ...)
                if (c == '#') {
                    bw.write(generateOutput(line.substring(i), "header"));
                    bw.newLine();
                    break;
                }

                // 🔹 COMMENT (//...)
                if (c == '/' && i + 1 < n && line.charAt(i + 1) == '/') {
                    bw.write(generateOutput(line.substring(i), "comment"));
                    bw.newLine();
                    break;
                }

                // 🔹 STRING ("...")
                if (c == '"') {
                    int j = i + 1;
                    while (j < n && line.charAt(j) != '"') j++;

                    String str = line.substring(i, j + 1);
                    bw.write(generateOutput(str, "string_literal"));
                    bw.newLine();

                    i = j + 1;
                    continue;
                }

                // 🔹 IDENTIFIER or KEYWORD
                if (Character.isLetter(c) || c == '_') {
                    int j = i;
                    while (j < n && (Character.isLetterOrDigit(line.charAt(j)) || line.charAt(j) == '_'))
                        j++;

                    String word = line.substring(i, j);

                    if (keywords.contains(word)) {
                        bw.write(generateOutput(word, "keyword"));
                    } else {
                        bw.write(generateOutput(word, "identifier"));
                    }
                    bw.newLine();

                    i = j;
                    continue;
                }

                // 🔹 NUMBER (int + float)
                if (Character.isDigit(c)) {
                    int j = i;

                    while (j < n && (Character.isDigit(line.charAt(j)) || line.charAt(j) == '.')) {
                        j++;
                    }

                    String num = line.substring(i, j);
                    bw.write(generateOutput(num, "number"));
                    bw.newLine();

                    i = j;
                    continue;
                }

                // 🔹 OPERATORS (multi-character first)
                if (i + 1 < n) {
                    String twoChar = line.substring(i, i + 2);
                    if (operators.contains(twoChar)) {
                        bw.write(generateOutput(twoChar, "operator"));
                        bw.newLine();
                        i += 2;
                        continue;
                    }
                }

                // 🔹 SINGLE CHARACTER OPERATOR
                if (operators.contains(String.valueOf(c))) {
                    bw.write(generateOutput(String.valueOf(c), "operator"));
                    bw.newLine();
                    i++;
                    continue;
                }

                // 🔹 SYMBOL
                if (symbols.contains(c)) {
                    bw.write(generateOutput(String.valueOf(c), "symbol"));
                    bw.newLine();
                    i++;
                    continue;
                }

                // 🔹 UNKNOWN
                bw.write(generateOutput(String.valueOf(c), "unknown"));
                bw.newLine();
                i++;
            }
        }

        br.close();
        bw.close();

        System.out.println("Lexical analysis completed");
    }
}