package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            throw new IllegalArgumentException("Provide the path to the directory to be analysed and the file where to store the result");
        }
        String sourcePath = args[0];
        String outputPath = args[1];

        File f = new File(sourcePath);
        ModHandler handler = new ModHandler();
        FeatureUsageMap result = handler.handle(f);

        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write(result.toJSON());
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("There was an error while writing to the file.");
        }
    }
}