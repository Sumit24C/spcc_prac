import java.util.*;

public class CodeOptimizer {

    static class Instruction {
        String lhs, op1, op2, operator;

        Instruction(String lhs, String op1, String operator, String op2) {
            this.lhs = lhs;
            this.op1 = op1;
            this.op2 = op2;
            this.operator = operator;
        }

        @Override
        public String toString() {
            if (operator == null) {
                return lhs + " = " + op1;
            }
            return lhs + " = " + op1 + " " + operator + " " + op2;
        }
    }

    // ================= PARSE =================
    static List<Instruction> parseInput(List<String> code) {
        List<Instruction> list = new ArrayList<>();

        for (String line : code) {
            line = line.replace(" ", "");
            String[] parts = line.split("=");

            String lhs = parts[0];
            String rhs = parts[1];

            if (rhs.contains("+") || rhs.contains("-") ||
                    rhs.contains("*") || rhs.contains("/")) {

                String op = "";

                if (rhs.contains("+")) op = "+";
                else if (rhs.contains("-")) op = "-";
                else if (rhs.contains("*")) op = "*";
                else if (rhs.contains("/")) op = "/";

                String[] operands = rhs.split("\\" + op);
                list.add(new Instruction(lhs, operands[0], op, operands[1]));
            } else {
                list.add(new Instruction(lhs, rhs, null, null));
            }
        }

        return list;
    }

    // ================= CSE =================
    static List<Instruction> commonSubexpression(List<Instruction> code) {
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

    // ================= STRENGTH REDUCTION =================
    static List<Instruction> strengthReduction(List<Instruction> code) {
        List<Instruction> optimized = new ArrayList<>();

        for (Instruction ins : code) {
            if (ins.operator != null && ins.operator.equals("*")) {
                if (ins.op2.equals("2")) {
                    optimized.add(new Instruction(ins.lhs, ins.op1, "+", ins.op1));
                    continue;
                }
            }
            optimized.add(ins);
        }
        return optimized;
    }

    // ================= FREQUENCY REDUCTION =================
    // FIX: Only reorder PURE constant expressions safely (no dependency break)
    static List<Instruction> frequencyReduction(List<Instruction> code) {
        List<Instruction> constants = new ArrayList<>();
        List<Instruction> others = new ArrayList<>();

        for (Instruction ins : code) {
            if (ins.operator != null &&
                isConstant(ins.op1) && isConstant(ins.op2)) {
                constants.add(ins);
            } else {
                others.add(ins);
            }
        }

        // Move constants safely to top
        List<Instruction> result = new ArrayList<>();
        result.addAll(constants);
        result.addAll(others);

        return result;
    }

    static boolean isConstant(String s) {
        if (s == null) return false;
        return s.matches("\\d+");
    }

    // ================= DEAD CODE ELIMINATION =================
    // FIX: Proper backward liveness analysis
    static List<Instruction> deadCodeElimination(List<Instruction> code) {

        Set<String> live = new HashSet<>();
        List<Instruction> optimized = new ArrayList<>();

        // Assume LAST variable is output → must be kept
        if (!code.isEmpty()) {
            live.add(code.get(code.size() - 1).lhs);
        }

        // Traverse backward
        for (int i = code.size() - 1; i >= 0; i--) {
            Instruction ins = code.get(i);

            if (live.contains(ins.lhs)) {
                optimized.add(0, ins);

                if (ins.op1 != null) live.add(ins.op1);
                if (ins.op2 != null) live.add(ins.op2);
            }
        }

        return optimized;
    }

    // ================= PRINT =================
    static void printCode(String title, List<Instruction> code) {
        System.out.println("\n" + title);
        for (Instruction ins : code) {
            System.out.println(ins);
        }
    }

    // ================= MAIN =================
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<String> input = new ArrayList<>();

        System.out.println("Enter Three Address Code (type 'end' to stop):");

        while (true) {
            String line = sc.nextLine();
            if (line.equalsIgnoreCase("end"))
                break;
            input.add(line);
        }

        List<Instruction> code = parseInput(input);

        printCode("Original Code:", code);

        code = commonSubexpression(code);
        printCode("After Common Subexpression Elimination:", code);

        code = strengthReduction(code);
        printCode("After Strength Reduction:", code);

        code = frequencyReduction(code);
        printCode("After Frequency Reduction:", code);

        code = deadCodeElimination(code);
        printCode("After Dead Code Elimination:", code);
    }
}