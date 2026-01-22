package org.example.exampleCode;

public class UnnamedVars {
    static int count(Iterable<Integer> orders) {
        int total = 0;
        for (Integer _ : orders)    // Unnamed variable
            total++;
        return total;
    }
}
