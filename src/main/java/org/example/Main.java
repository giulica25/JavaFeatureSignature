package org.example;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

public class Main {
    
    public static void main(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("Provide the path to the directory to be analysed.");
        }
        String sourcePath = args[0];
        File f2 = new File(sourcePath);
        for (File f : Objects.requireNonNull(f2.listFiles())) {
            ModHandler handler = new ModHandler();
            System.out.println(f.getName());
            System.out.println(Arrays.toString(handler.handle(f)));
        }
    }
}