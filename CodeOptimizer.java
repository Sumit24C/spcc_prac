
/*
A = B + C
D = B + C
E = A * 2
F = 5 + 3
G = F + 0
H = G * 1
I = H
end
*/
import java.util.*;

public class CodeOptimizer {
    static class Instruction {
        String lhs, op1, operator, op2;

        public Instruction(String lhs, String op1, String operator, String op2) {
            this.lhs = lhs;
            this.op1 = op1;
            this.operator = operator;
            this.op2 = op2;
        }

        @Override
        public String toString() {
            if (operator == null) {
                return lhs + " = " + op1;
            }
            return lhs + " = " + op1 + " " + operator + " " + op2;
        }
    }

    static List<Instruction> parseInput(List<String> code) {
        List<Instruction> list = new ArrayList<>();

        for (String line : code) {
            line = line.replace(" ", "");
            String[] parts = line.split("=");

            String lhs = parts[0].trim();
            String rhs = parts[1].trim();

            if (rhs.contains("+") || rhs.contains("-") ||
                    rhs.contains("*") || rhs.contains("/")) {

                String op = "";

                if (rhs.contains("+"))
                    op = "+";
                else if (rhs.contains("-"))
                    op = "-";
                else if (rhs.contains("*"))
                    op = "*";
                else if (rhs.contains("/"))
                    op = "/";

                String[] operands = rhs.split("\\" + op);
                list.add(new Instruction(lhs, operands[0], op, operands[1]));

            } else {
                list.add(new Instruction(lhs, rhs, null, null));
            }
        }
        return list;
    }

    static boolean isConstant(String s) {
        return s != null && s.matches("-?\\d+");
    }

    static List<Instruction> cse(List<Instruction> code) {
        Map<String, String> map = new HashMap<>();
        List<Instruction> optimized = new ArrayList<>();

        for (Instruction ins : code) {
            if (ins.operator != null) {
                String expr = ins.op1 + ins.operator + ins.op2;
                if (map.containsKey(expr)) {
                    optimized.add(new Instruction(ins.lhs, map.get(expr), null, null));
                } else {
                    map.put(expr, ins.lhs);
                    optimized.add(ins);
                }
            } else {
                optimized.add(ins);
            }
        }
        return optimized;
    }

    static List<Instruction> strengthRed(List<Instruction> code) {
        List<Instruction> optimized = new ArrayList<>();

        for (Instruction ins : code) {
            if (ins.operator != null && ins.operator.equals("*")) {
                if (ins.op2 != null && ins.op2.equals("2")) {
                    optimized.add(new Instruction(ins.lhs, ins.op1, "+", ins.op1));
                } else {
                    optimized.add(ins);
                }
            } else {
                optimized.add(ins);
            }
        }

        return optimized;
    }

    static List<Instruction> constantFolding(List<Instruction> code) {
        List<Instruction> optimized = new ArrayList<>();

        for (Instruction ins : code) {
            String operator = ins.operator;
            if (operator != null) {
                if (isConstant(ins.op1) && isConstant(ins.op2)) {
                    int a = Integer.parseInt(ins.op1);
                    int b = Integer.parseInt(ins.op2);
                    int val = 0;
                    if (operator.equals("+"))
                        val = a + b;
                    else if (operator.equals("*"))
                        val = a * b;
                    else if (operator.equals("-"))
                        val = a - b;
                    else if (operator.equals("/"))
                        val = a / b;
                    optimized.add(new Instruction(ins.lhs, String.valueOf(val), null, null));
                } else {
                    optimized.add(ins);
                }
            } else {
                optimized.add(ins);
            }
        }
        return optimized;
    }

    static List<Instruction> algebraicSimplication(List<Instruction> code) {
        List<Instruction> optimized = new ArrayList<>();

        for (Instruction ins : code) {
            String operator = ins.operator;
            if (operator == null) {
                optimized.add(ins);
            } else {
                String a = ins.op1;
                String b = ins.op2;

                if ((operator.equals("+") || operator.equals("-")) && (b.equals("0") || a.equals("0"))) {
                    if (a.equals("0")) {
                        optimized.add(new Instruction(ins.lhs, b, null, null));
                    } else {
                        optimized.add(new Instruction(ins.lhs, a, null, null));
                    }
                } else if ((operator.equals("*") || operator.equals("/")) && (b.equals("1") || a.equals("1"))) {
                    if (a.equals("1")) {
                        optimized.add(new Instruction(ins.lhs, b, null, null));
                    } else {
                        optimized.add(new Instruction(ins.lhs, a, null, null));
                    }
                } else {
                    optimized.add(ins);
                }
            }
        }
        return optimized;
    }

    static List<Instruction> copyPropagation(List<Instruction> code) {
        List<Instruction> optimized = new ArrayList<>();
        Map<String, String> copyMap = new HashMap<>();
        for (Instruction ins : code) {
            String a = ins.op1;
            String b = ins.op2;
            if (copyMap.containsKey(a)) {
                a = copyMap.get(a);
            }
            if (copyMap.containsKey(b)) {
                b = copyMap.get(b);
            }
            if (ins.operator == null) {
                copyMap.put(ins.lhs, a);
                optimized.add(new Instruction(ins.lhs, a, null, null));
            } else {
                optimized.add(new Instruction(ins.lhs, a, ins.operator, b));
            }
        }
        return optimized;
    }

    static void printCode(String title, List<Instruction> code) {
        System.out.println("\n" + title);
        for (Instruction instruction : code) {
            System.out.println(instruction);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<String> input = new ArrayList<>();
        System.out.println("Enter three addr code");
        while (true) {
            String line = sc.nextLine().trim();
            if (line.equalsIgnoreCase("end")) {
                break;
            }
            input.add(line);
        }

        List<Instruction> code = parseInput(input);
        printCode("Original Code", code);

        code = cse(code);
        printCode("cse Code", code);
        code = strengthRed(code);
        printCode("strengthRed Code", code);
        code = constantFolding(code);
        printCode("constantFolding Code", code);
        code = algebraicSimplication(code);
        printCode("algebra Code", code);
        code = copyPropagation(code);
        printCode("copy Code", code);

    }

}
