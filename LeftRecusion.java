/*
IP: A->Aa/Ab/c/d
OP: After removing left recursion
A -> cA' / dA'
A' -> aA' / bA' / ε
*/

import java.util.*;

public class LeftRecusion {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter the Grammar:");
        String input = sc.nextLine().trim();
        sc.close();
        eliminateLeftRecursion(input);
    }

    public static void eliminateLeftRecursion(String production) {

        String[] parts = production.split("->");
        String A = parts[0].trim();
        String rhs = parts[1].trim();

        String[] alternatives = rhs.split("/");

        List<String> alpha = new ArrayList<>();
        List<String> beta = new ArrayList<>();

        for (String prod : alternatives) {
            prod = prod.trim();

            if (prod.startsWith(A)) {
                alpha.add(prod.substring(A.length()));
            } else {
                beta.add(prod);
            }
        }

        // No left recursion
        if (alpha.isEmpty()) {
            System.out.println("\nNo left Recursion found");
            System.out.println("Grammar remains same");
            System.out.println(production);
            return;
        }

        String A_dash = A + "'";

        System.out.println("\nAfter removing left recursion");

        // A → β A'
        System.out.print(A + " -> ");
        for (int i = 0; i < beta.size(); i++) {
            System.out.print(beta.get(i) + A_dash);
            if (i != beta.size() - 1) {
                System.out.print(" / ");
            }
        }

        System.out.println();

        // A' → α A' | ε
        System.out.print(A_dash + " -> ");
        for (int i = 0; i < alpha.size(); i++) {
            System.out.print(alpha.get(i) + A_dash + " / ");
        }
        System.out.println("ε");
    }
}