import java.io.*;
import java.util.*;

class Assembler {

    static class Symbol {
        String name;
        int value;

        Symbol(String n, int v) {
            name = n;
            value = v;
        }
    }

    static class Literal {
        String name;
        int value;

        Literal(String n, int v) {
            name = n;
            value = v;
        }
    }

    static Map<String, String> MOT = new HashMap<>();
    static Set<String> POT = new HashSet<>(Arrays.asList("START", "USING", "DC", "DS", "END"));

    static Map<Integer, Integer> baseRegister = new HashMap<>();
    static List<Symbol> symbolTable = new ArrayList<>();
    static List<Literal> literalTable = new ArrayList<>();

    static List<String[]> program = new ArrayList<>();
    static List<String> pass1 = new ArrayList<>();
    static List<String> pass2 = new ArrayList<>();
    static List<Integer> LC_list = new ArrayList<>();

    public static void main(String[] args) throws Exception {

        initializeMOT();
        initializeBase();

        BufferedReader br = new BufferedReader(new FileReader("sample1.asm"));
        String line;
        int LC = 0;

        while ((line = br.readLine()) != null) {

            String[] p = parse(line);

            String label = p[0];
            String m = p[1];
            String op1 = p[2];
            String op2 = p[3];

            program.add(p);
            LC_list.add(LC);

            // Symbol Table
            if (!label.equals("-")) {
                symbolTable.add(new Symbol(label, LC));
            }

            if (m.equals("L") || m.equals("A") || m.equals("ST")) {
                pass1.add(m + " " + op1 + ",__");
                LC += 4;
            }

            else if (m.equals("DC")) {
                int val = extractNumber(op1);
                pass1.add(String.valueOf(val));
                literalTable.add(new Literal(op1, LC));
                LC += 4;
            }

            else if (m.equals("DS")) {
                int val = Integer.parseInt(op1.replace("F", ""));
                pass1.add(String.valueOf(val));
                // literalTable.add(new Literal(op1, LC));
                LC += val * 4;
            }

            else if (m.equals("USING")) {
                baseRegister.put(Integer.parseInt(op2), LC);
                pass1.add("");
            }

            else {
                pass1.add("");
            }
        }

        br.close();

        int base = getBaseRegister();

        // PASS 2
        for (int i = 0; i < program.size(); i++) {

            String[] p = program.get(i);
            String m = p[1];
            String op1 = p[2];
            String op2 = p[3];

            if (m.equals("L") || m.equals("A") || m.equals("ST")) {

                int symbolAddr = getSymbolAddress(op2);
                int displacement = symbolAddr - base;

                pass2.add(m + " " + op1 + "," + displacement + "(0," + baseRegisterKey() + ")");
            } else {
                pass2.add("");
            }
        }

        printProgram();
        printMOT();
        printPOT();
        printSymbolTable();
        printLiteralTable();
        printBase();
        printPass();
    }

    static String[] parse(String line) {
        String label = "-", m = "-", op1 = "-", op2 = "-";

        String[] t = line.trim().split("\\s+");
        if (t.length == 0) {
            return new String[] { label, m, op1, op2 };
        }

        if (MOT.containsKey(t[0]) || POT.contains(t[0])) {
            m = t[0];

            if (t.length > 1) {
                if (t[1].contains(",")) {
                    String[] o = t[1].split(",");
                    op1 = o[0];
                    op2 = o.length > 1 ? o[1] : "-";
                } else {
                    op1 = t[1];
                }
            }
        } else {
            label = t[0];

            if (t.length > 1)
                m = t[1];

            if (t.length > 2) {
                if (t[2].contains(",")) {
                    String[] o = t[2].split(",");
                    op1 = o[0];
                    op2 = o.length > 1 ? o[1] : "-";
                } else
                    op1 = t[2];
            }
        }

        return new String[] { label, m, op1, op2 };
    }

    static void initializeMOT() {
        MOT.put("L", "001");
        MOT.put("A", "001");
        MOT.put("ST", "001");
    }

    static void initializeBase() {
        for (int i = 0; i < 16; i++)
            baseRegister.put(i, -1);
    }

    static int extractNumber(String s) {
        return Integer.parseInt(s.replaceAll("[^0-9]", ""));
    }

    static int getSymbolAddress(String name) {
        for (Symbol s : symbolTable)
            if (s.name.equals(name))
                return s.value;
        return 0;
    }

    static int getBaseRegister() {
        for (int i = 0; i < 16; i++)
            if (baseRegister.get(i) != -1)
                return baseRegister.get(i);
        return 0;
    }

    static int baseRegisterKey() {
        for (int i = 0; i < 16; i++)
            if (baseRegister.get(i) != -1)
                return i;
        return 0;
    }

    static void printProgram() {
        System.out.println("\n1. PROGRAM SEGREGATION");
        System.out.println("Label\tMnemonic\tOp1\tOp2");

        for (String[] r : program)
            System.out.println(r[0] + "\t" + r[1] + "\t\t" + r[2] + "\t" + r[3]);
    }

    static void printMOT() {
        System.out.println("\n2. MNEMONIC OPCODE TABLE");
        System.out.println("Mnemonic\tBinary\tLength\tFormat");

        for (String k : MOT.keySet())
            System.out.println(k + "\t\t\t\t10\t001");
    }

    static void printPOT() {
        System.out.println("\n3. PSEUDO OPCODE TABLE");

        for (String p : POT)
            System.out.println(p + "\t-");
    }

    static void printSymbolTable() {
        System.out.println("\n4. SYMBOL TABLE");

        for (Symbol s : symbolTable)
            System.out.println(s.name + "\t" + s.value);
    }

    static void printLiteralTable() {
        System.out.println("\n5. LITERAL TABLE");

        for (Literal l : literalTable)
            System.out.println(l.name + "\t" + l.value);
    }

    static void printBase() {
        System.out.println("\n6. BASE REGISTER TABLE");

        for (int i = 0; i < 16; i++) {
            if (baseRegister.get(i) == -1)
                System.out.println(i + "\t-");
            else
                System.out.println(i + "\t" + baseRegister.get(i));
        }
    }

    static void printPass() {
        System.out.println("\n7. PASS TABLE");
        System.out.println("LC\tPass1\t\tPass2");

        for (int i = 0; i < program.size(); i++) {
            System.out.println(LC_list.get(i) + "\t" + pass1.get(i) + "\t\t" + pass2.get(i));
        }
    }
}