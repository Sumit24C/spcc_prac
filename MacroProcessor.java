import java.io.*;
import java.util.*;

public class MacroProcessor {

    static List<String> MDT = new ArrayList<>(); // Macro Definition Table
    static Map<String, Integer> MNT = new HashMap<>(); // Macro Name Table
    static List<String> ALA = new ArrayList<>(); // Argument List Array

    public static void main(String[] args) throws Exception {
        String inputFile = "sample.asm";
        String outputFile = "output.asm";

        pass1(inputFile);
        pass2(inputFile, outputFile);
    }

    // PASS 1: Process Macro Definitions
    static void pass1(String fileName) throws Exception {

        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        boolean insideMacro = false;

        System.out.println("\n--- PASS 1 OUTPUT ---");

        while ((line = br.readLine()) != null) {
            line = line.trim();

            // Detect MACRO
            if (line.contains("MACRO")) {
                insideMacro = true;

                String macroName = line.split(" ")[0];
                MNT.put(macroName, MDT.size());

                System.out.println("\nMacro Name Table:");
                System.out.println("Index\tName");
                System.out.println((MNT.size() - 1) + "\t" + macroName);

                continue;
            }

            // End of macro
            if (insideMacro && line.equals("ENDM")) {
                insideMacro = false;
                continue;
            }

            // Store macro body
            if (insideMacro) {
                MDT.add(line);
            }

            // Build ALA (simple logic)
            if (line.startsWith("DISP")) {
                String arg = line.split(" ")[1];
                if (!ALA.contains(arg)) {
                    ALA.add(arg);
                }
            }
        }

        br.close();

        // Print ALA
        System.out.println("\nArgument List Array:");
        System.out.println("Index\tArgument");
        for (int i = 0; i < ALA.size(); i++) {
            System.out.println(i + "\t" + ALA.get(i));
        }

        // Print MDT
        System.out.println("\nMacro Definition Table:");
        System.out.println("Index\tDefinition");
        for (int i = 0; i < MDT.size(); i++) {
            System.out.println(i + "\t" + MDT.get(i));
        }
    }

    // PASS 2: Expand Macros
    static void pass2(String inputFile, String outputFile) throws Exception {

        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));

        String line;
        boolean insideMacro = false;

        System.out.println("\n--- PASS 2 OUTPUT (EXPANDED CODE) ---\n");

        while ((line = br.readLine()) != null) {
            line = line.trim();

            if (line.contains("MACRO")) {
                insideMacro = true;
                continue;
            }

            if (insideMacro && line.equals("ENDM")) {
                insideMacro = false;
                continue;
            }

            if (insideMacro)
                continue;

            // Macro Call Handling
            if (line.startsWith("DISP")) {

                String arg = line.split(" ")[1];

                for (String def : MDT) {
                    if (def.contains("XX")) {
                        def = def.replace("XX", arg);
                    }

                    System.out.println(def);
                    bw.write(def);
                    bw.newLine();
                }
            } else {
                System.out.println(line);
                bw.write(line);
                bw.newLine();
            }
        }

        br.close();
        bw.close();

        System.out.println("\nExpanded code saved in: " + outputFile);
    }
}