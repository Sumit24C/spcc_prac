import java.util.*;

class Quadruple {
    String op, arg1, arg2, result;

    Quadruple(String op, String arg1, String arg2, String result) {
        this.op = op;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.result = result;
    }
}

class Triple {
    String op, arg1, arg2;

    Triple(String op, String arg1, String arg2) {
        this.op = op;
        this.arg1 = arg1;
        this.arg2 = arg2;
    }
}

public class IntermediateCodeGen {

    static List<Quadruple> quads = new ArrayList<>();
    static List<Triple> triples = new ArrayList<>();
    static int tempCount = 1;

    static int precedence(char op) {
        if (op == '~') return 3;   // unary minus
        if (op == '*' || op == '/') return 2;
        if (op == '+' || op == '-') return 1;
        return 0;
    }

    static String infixToPostfix(String infix) {
        StringBuilder postfix = new StringBuilder();
        Stack<Character> stack = new Stack<>();

        for (int i = 0; i < infix.length(); i++) {
            char c = infix.charAt(i);

            if (Character.isWhitespace(c))
                continue;

            if (Character.isLetterOrDigit(c)) {
                while (i < infix.length() && Character.isLetterOrDigit(infix.charAt(i))) {
                    postfix.append(infix.charAt(i++));
                }
                postfix.append(" ");
                i--;
            }

            else if (c == '(') {
                stack.push(c);
            }

            else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    postfix.append(stack.pop()).append(" ");
                }
                stack.pop();
            }

            else {
                while (!stack.isEmpty() && precedence(stack.peek()) >= precedence(c)) {
                    postfix.append(stack.pop()).append(" ");
                }
                stack.push(c);
            }
        }

        while (!stack.isEmpty()) {
            postfix.append(stack.pop()).append(" ");
        }

        return postfix.toString();
    }

    static void generateIntermediateCode(String postfix, String lhs) {

        Stack<String> quadStack = new Stack<>();
        Stack<String> tripleStack = new Stack<>();

        String[] tokens = postfix.split("\\s+");

        for (String token : tokens) {

            if (token.isEmpty())
                continue;

            if (Character.isLetterOrDigit(token.charAt(0))) {
                quadStack.push(token);
                tripleStack.push(token);
            }

            else {
                if (token.equals("~")) {
                    // unary operator

                    String arg = quadStack.pop();
                    String temp = "t" + (tempCount++);

                    quads.add(new Quadruple("uminus", arg, "-", temp));
                    quadStack.push(temp);

                    String tArg = tripleStack.pop();
                    triples.add(new Triple("uminus", tArg, "-"));
                    tripleStack.push("(" + (triples.size() - 1) + ")");
                } else {
                    // binary operator

                    String qArg2 = quadStack.pop();
                    String qArg1 = quadStack.pop();
                    String temp = "t" + (tempCount++);

                    quads.add(new Quadruple(token, qArg1, qArg2, temp));
                    quadStack.push(temp);

                    String tArg2 = tripleStack.pop();
                    String tArg1 = tripleStack.pop();

                    triples.add(new Triple(token, tArg1, tArg2));
                    tripleStack.push("(" + (triples.size() - 1) + ")");
                }
            }
        }

        // final assignment
        String finalQuadVal = quadStack.pop();
        quads.add(new Quadruple("=", finalQuadVal, "-", lhs.trim()));

        String finalTripleVal = tripleStack.pop();
        triples.add(new Triple("=", lhs.trim(), finalTripleVal));
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("Enter expression (use ~ for unary minus):");
        String input = sc.nextLine();

        String[] parts = input.split("=");

        generateIntermediateCode(infixToPostfix(parts[1]), parts[0]);

        System.out.println("\n--- Quadruple Table ---");
        System.out.println("Op\tArg1\tArg2\tResult");

        for (Quadruple q : quads) {
            System.out.println(q.op + "\t" + q.arg1 + "\t" + q.arg2 + "\t" + q.result);
        }

        System.out.println("\n--- Triple Table ---");
        System.out.println("Index\tOp\tArg1\tArg2");

        for (int i = 0; i < triples.size(); i++) {
            Triple t = triples.get(i);
            System.out.println("(" + i + ")\t" + t.op + "\t" + t.arg1 + "\t" + t.arg2);
        }

        sc.close();
    }
}