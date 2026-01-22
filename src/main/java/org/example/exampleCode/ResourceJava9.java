package org.example.exampleCode;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class ResourceJava9 {
    static void main() throws IOException {
        final Writer x = new FileWriter("org/example/dump/Example4.1.txt");

        try (FileWriter y = new FileWriter("org/example/dump/Example4.2.txt"); x) {

        }
    }
}
