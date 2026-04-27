import java.io.*;
import java.util.*;

class MNTEntry {
    String name;
    int mdtp;

    MNTEntry(String name, int mdtp) {
        this.name = name;
        this.mdtp = mdtp;
    }
}

public class MacroProcessor {

    static List<String> MDT = new ArrayList<>();
    static Map<String, MNTEntry> MNT = new HashMap<>();
    static List<String> ALA = new ArrayList<>(); // stores dummy args

    public static void main(String[] args) throws Exception {
        String inputFile = "sample.asm";
        String outputFile = "output.asm";

        pass1(inputFile);
        pass2(inputFile, outputFile);
    }

    // ---------------- PASS 1 ----------------
    static void pass1(String fileName) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        boolean insideMacro = false;

        System.out.println("\n--- PASS 1 OUTPUT ---");

        while ((line = br.readLine()) != null) {
            line = line.trim();

            // Detect macro start
            if (line.contains("MACRO")) {
                insideMacro = true;

                String[] parts = line.split("\\s+");

                String macroName = parts[0]; // DISP
                MNT.put(macroName, new MNTEntry(macroName, MDT.size()));

                // Extract dummy arguments (skip "MACRO")
                for (int i = 2; i < parts.length; i++) {
                    String[] args = parts[i].split(",");
                    for (String arg : args) {
                        ALA.add(arg.trim());
                    }
                }
                continue;
            }

            // End of macro
            if (insideMacro && line.equals("ENDM")) {
                MDT.add("MEND");
                insideMacro = false;
                continue;
            }

            // Store macro body
            if (insideMacro) {
                MDT.add(line);
            }
        }

        br.close();

        // Print MNT
        System.out.println("\nMacro Name Table (MNT):");
        System.out.println("Name\tMDTP");
        for (String key : MNT.keySet()) {
            System.out.println(key + "\t" + MNT.get(key).mdtp);
        }

        // Print ALA (dummy args)
        System.out.println("\nArgument List Array (Dummy Args):");
        System.out.println("Index\tArgument");
        for (int i = 0; i < ALA.size(); i++) {
            System.out.println(i + "\t" + ALA.get(i));
        }

        // Print MDT
        System.out.println("\nMacro Definition Table (MDT):");
        System.out.println("Index\tDefinition");
        for (int i = 0; i < MDT.size(); i++) {
            System.out.println(i + "\t" + MDT.get(i));
        }
    }

    // ---------------- PASS 2 ----------------
    static void pass2(String inputFile, String outputFile) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));

        String line;
        boolean insideMacro = false;

        System.out.println("\n--- PASS 2 OUTPUT (EXPANDED CODE) ---\n");

        while ((line = br.readLine()) != null) {
            line = line.trim();

            // Skip macro definitions
            if (line.contains("MACRO")) {
                insideMacro = true;
                continue;
            }

            if (insideMacro && line.equals("ENDM")) {
                insideMacro = false;
                continue;
            }

            if (insideMacro) continue;

            String[] parts = line.split("\\s+");
            String opcode = parts[0];

            // If macro call
            if (MNT.containsKey(opcode)) {

                Map<String, String> actualALA = new HashMap<>();

                // Get actual arguments
                if (parts.length > 1) {
                    String[] actualArgs = parts[1].split(",");

                    for (int i = 0; i < actualArgs.length; i++) {
                        actualALA.put(ALA.get(i), actualArgs[i].trim());
                    }
                }

                int mdtp = MNT.get(opcode).mdtp;

                // Expand macro
                for (int i = mdtp; i < MDT.size(); i++) {
                    String def = MDT.get(i);

                    if (def.equals("MEND"))
                        break;

                    // Replace dummy with actual
                    for (String dummy : actualALA.keySet()) {
                        def = def.replace(dummy, actualALA.get(dummy));
                    }

                    System.out.println(def);
                    bw.write(def);
                    bw.newLine();
                }

            } else {
                // Normal line
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